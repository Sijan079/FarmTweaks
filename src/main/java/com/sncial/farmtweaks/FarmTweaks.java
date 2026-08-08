package com.sncial.farmtweaks;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import java.util.ArrayDeque;
import java.util.Collection;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(FarmTweaks.MODID)
public class FarmTweaks {
    public static final String MODID = "farmtweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final TagKey<Block> RIGHT_CLICK_HARVESTABLE = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(MODID, "right_click_harvestable")
    );

    private static final TagKey<Item> SEEDLIKE_ITEMS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "seedlike")
    );

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<Item> SEED_BAG = ITEMS.register("seed_bag", () -> new SeedBagItem(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FARM_TWEAKS_TAB = CREATIVE_MODE_TABS.register("farmtweaks", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.farmtweaks"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> SEED_BAG.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                if (Config.enableSeedBags()) {
                    output.accept(SEED_BAG.get());
                }
            }).build());

    public FarmTweaks(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC, "farmtweaks.toml");

        // Optional: if we're on the physical client and Cloth Config is installed, expose a nicer in-game config UI.
        // This is reflection-only so we don't hard-depend on client-only classes or Cloth Config at runtime.
        try {
            Class.forName("net.neoforged.neoforge.client.gui.IConfigScreenFactory");
            Class<?> compat = Class.forName("com.sncial.farmtweaks.ClothConfigCompat");
            compat.getDeclaredMethod("registerConfigScreen", ModContainer.class).invoke(null, modContainer);
        } catch (Throwable ignored) {
            // Dedicated server, older NeoForge, or Cloth Config not present; no config screen integration.
        }

        NeoForge.EVENT_BUS.register(this);
    }

    private static int getRadiusForLevel(int efficiencyLevel, boolean isHarvest) {
        if (efficiencyLevel <= 0) {
            return 0;
        }
        // Hardcoded mapping for now; same for tilling and harvest, but split if needed later.
        return switch (efficiencyLevel) {
            case 1, 2 -> 1;
            case 3, 4 -> 2;
            default -> 3; // level 5+ -> radius 3
        };
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("FarmTweaks common setup");
    }

    /**
     * Right-click harvest for mature vanilla-style crops with auto replant.
     * Fortune on the held tool is respected via the standard loot system.
     */
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        if (player == null) {
            return;
        }

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack tool = player.getMainHandItem();
        boolean sneaking = player.isShiftKeyDown();

        // Branch 1: tilling with hoes (single clicked block when sneaking, flood-fill AoE otherwise).
        if (Config.enableAoETilling() && tool.getItem() instanceof HoeItem) {
            BlockPos pos = event.getPos();
            BlockState state = level.getBlockState(pos);

            // Only attempt tilling when right-clicking dirt-like blocks.
            if (!level.isClientSide() && isTillable(state)) {
                // Sneaking/crouching explicitly disables AoE: only ever till the clicked block.
                if (sneaking) {
                    if (level.isEmptyBlock(pos.above())) {
                        level.setBlockAndUpdate(pos, Blocks.FARMLAND.defaultBlockState());
                        if (!player.isCreative()) {
                            EquipmentSlot slot = event.getHand() == InteractionHand.MAIN_HAND
                                    ? EquipmentSlot.MAINHAND
                                    : EquipmentSlot.OFFHAND;
                            tool.hurtAndBreak(1, player, slot);
                        }
                        player.swing(event.getHand(), true);
                    }
                    // Prevent vanilla hoe handling; we own this interaction for tillable blocks.
                    event.setCanceled(true);
                    return;
                }

                int maxTillCount = 1;
                int efficiency = EnchantmentHelper.getItemEnchantmentLevel(
                        level.registryAccess()
                                .lookupOrThrow(Registries.ENCHANTMENT)
                                .getOrThrow(Enchantments.EFFICIENCY),
                        tool
                );
                int scaled = 1 + (efficiency * Config.aoeTillingCountStep());
                maxTillCount = Math.min(256, Math.max(1, scaled));

                int tilledCount = 0;
                boolean tilledAny = false;

                ArrayDeque<BlockPos> queue = new ArrayDeque<>();
                LongOpenHashSet visited = new LongOpenHashSet();
                queue.add(pos);
                visited.add(pos.asLong());

                // Avoid scanning huge areas if most blocks are not tillable (paths, etc.).
                int searchBudget = Math.min(4096, Math.max(64, maxTillCount * 64));
                int processed = 0;

                while (!queue.isEmpty() && tilledCount < maxTillCount && processed < searchBudget) {
                    BlockPos cur = queue.removeFirst();
                    processed++;

                    BlockState curState = level.getBlockState(cur);
                    if (!isTillable(curState)) {
                        continue;
                    }

                    if (level.isEmptyBlock(cur.above())) {
                        level.setBlockAndUpdate(cur, Blocks.FARMLAND.defaultBlockState());
                        tilledAny = true;
                        tilledCount++;

                        if (!player.isCreative()) {
                            EquipmentSlot slot = event.getHand() == InteractionHand.MAIN_HAND
                                    ? EquipmentSlot.MAINHAND
                                    : EquipmentSlot.OFFHAND;
                            tool.hurtAndBreak(1, player, slot);
                        }

                        if (tilledCount >= maxTillCount) {
                            break;
                        }
                    }

                    // 8-direction connectivity through tillable blocks.
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dz == 0) {
                                continue;
                            }
                            BlockPos nxt = cur.offset(dx, 0, dz);
                            long k = nxt.asLong();
                            if (visited.contains(k)) {
                                continue;
                            }
                            BlockState ns = level.getBlockState(nxt);
                            if (isTillable(ns)) {
                                visited.add(k);
                                queue.addLast(nxt);
                            }
                        }
                    }
                }

                if (tilledAny) {
                    player.swing(event.getHand(), true);
                }

                event.setCanceled(true);
                return;
            }
        }

        // Branch 2: right-click harvest with auto-replant and optional AoE.
        if (level.isClientSide() || !Config.enableRightClickHarvest()) {
            return;
        }

        // Allow harvesting even while holding an item.
        // Hoes remain "special": they keep AoE scaling + Fortune behavior below.

        BlockPos center = event.getPos();

        // Determine harvest budget: empty hand = single block, hoe can harvest up to N crops (scaled by Efficiency).
        int maxHarvestCount = 1;
        ItemStack toolForLoot = ItemStack.EMPTY;
        if (tool.getItem() instanceof HoeItem) {
            toolForLoot = tool;
            if (Config.enableAoEHarvest() && !sneaking) {
                int efficiency = EnchantmentHelper.getItemEnchantmentLevel(
                        level.registryAccess()
                                .lookupOrThrow(Registries.ENCHANTMENT)
                                .getOrThrow(Enchantments.EFFICIENCY),
                        tool
                );
                int scaled = 1 + (efficiency * Config.aoeHarvestCountStep());
                // Hard cap to avoid runaway scans in very large fields.
                maxHarvestCount = Math.min(256, Math.max(1, scaled));
            }
        }

        // Precompute Fortune level (if any) once for the hoe.
        int fortuneLevel = 0;
        if (Config.enableFortuneCrops() && !toolForLoot.isEmpty()) {
            fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    level.registryAccess()
                            .lookupOrThrow(Registries.ENCHANTMENT)
                            .getOrThrow(Enchantments.FORTUNE),
                    toolForLoot
            );
        }

        // Only start the harvest if the clicked block is a mature crop-like block.
        BlockState clickedState = level.getBlockState(center);
        if (!isMatureCropLike(level, center, clickedState)) {
            return;
        }

        int harvestedCount = 0;
        boolean harvestedAny = false;

        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        LongOpenHashSet visited = new LongOpenHashSet();
        queue.add(center);
        visited.add(center.asLong());

        // Avoid scanning huge areas when most crops are immature.
        int searchBudget = Math.min(4096, Math.max(64, maxHarvestCount * 64));
        int processed = 0;

        while (!queue.isEmpty() && harvestedCount < maxHarvestCount && processed < searchBudget) {
            BlockPos pos = queue.removeFirst();
            processed++;

            BlockState state = level.getBlockState(pos);
            CropLike cropLike = cropLike(level, pos, state);
            if (cropLike == null) {
                continue;
            }

            // Harvest mature crops; traverse through all crops (mature or not) for connectivity.
            if (cropLike.mature()) {
                boolean harvestedThis = false;
                boolean appliedCostsAndRewards = false;

                // Prefer letting blocks with custom right-click harvest behavior handle their own drops + state.
                // This avoids forcing an age reset (common for berry bushes and many modded crops).
                // If the block doesn't handle use-without-item, fall back to the generic drop+reset path.
                if (!(state.getBlock() instanceof CropBlock)) {
                    harvestedThis = tryHarvestViaUseWithoutItem(level, player, pos);
                }

                if (!harvestedThis) {
                    harvestedThis = completeCropHarvest(
                            level,
                            player,
                            event.getHand(),
                            pos,
                            state,
                            cropLike.resetState(),
                            toolForLoot,
                            fortuneLevel
                    );
                    appliedCostsAndRewards = harvestedThis;
                }

                if (!harvestedThis) {
                    continue;
                }

                if (!appliedCostsAndRewards) {
                    boolean inSeason = level instanceof ServerLevel serverLevel
                            && SereneSeasonsCompat.isCropInSeason(serverLevel, pos, state);
                    applyHarvestCostsAndRewards(level, player, event.getHand(), pos, toolForLoot, isSereneXpBoostEligible(inSeason));
                }

                harvestedAny = true;
                harvestedCount++;
                if (harvestedCount >= maxHarvestCount) {
                    break;
                }
            }

            // 8-direction flood fill through crops (including immature).
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) {
                        continue;
                    }

                    BlockPos n1 = pos.offset(dx, 0, dz);
                    long k1 = n1.asLong();
                    if (!visited.contains(k1)) {
                        BlockState s1 = level.getBlockState(n1);
                        if (cropLike(level, n1, s1) != null) {
                            visited.add(k1);
                            queue.addLast(n1);
                        }
                    }

                    // One-block air gap: crop -> air -> mature crop.
                    BlockPos mid = n1;
                    if (level.isEmptyBlock(mid)) {
                        BlockPos n2 = pos.offset(dx * 2, 0, dz * 2);
                        long k2 = n2.asLong();
                        if (!visited.contains(k2)) {
                            BlockState s2 = level.getBlockState(n2);
                            if (isMatureCropLike(level, n2, s2)) {
                                visited.add(k2);
                                queue.addLast(n2);
                            }
                        }
                    }
                }
            }
        }

        if (harvestedAny) {
            // Always show the swing animation (hand or tool) when we successfully harvest at least one crop.
            player.swing(event.getHand(), true);
            event.setCanceled(true);
        }
    }

    private static boolean completeCropHarvest(
            Level level,
            Player player,
            InteractionHand hand,
            BlockPos pos,
            BlockState harvestState,
            BlockState resetState,
            ItemStack toolForLoot,
            int fortuneLevel
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (!fireSyntheticHarvestBreakEvent(level, player, pos, harvestState)) {
            return false;
        }

        if (!fireSyntheticReplantEvent(serverLevel, player, pos, harvestState, resetState)) {
            return false;
        }

        boolean inSeason = SereneSeasonsCompat.isCropInSeason(serverLevel, pos, harvestState);
        boolean sereneXpBoostEligible = isSereneXpBoostEligible(inSeason);

        player.awardStat(Stats.BLOCK_MINED.get(harvestState.getBlock()));
        if (!toolForLoot.isEmpty()) {
            player.awardStat(Stats.ITEM_USED.get(toolForLoot.getItem()));
        }

        popHarvestDrops(serverLevel, pos, harvestState, player, toolForLoot, true);

        // Fortune bonus: add extra non-seed drops for "wheat-like" crops that drop both produce + seeds.
        if (fortuneLevel > 0 && shouldApplyFortuneBonus(inSeason)) {
            for (ItemStack drop : Block.getDrops(harvestState, serverLevel, pos, null, player, toolForLoot)) {
                if (drop.isEmpty() || drop.is(SEEDLIKE_ITEMS)) {
                    continue;
                }

                int extra = level.random.nextInt(fortuneLevel + 1);
                if (extra > 0) {
                    ItemStack bonus = drop.copy();
                    bonus.setCount(extra);
                    Block.popResource(level, pos, bonus);
                }
            }
        }

        harvestState.spawnAfterBreak(serverLevel, pos, toolForLoot, true);
        level.setBlockAndUpdate(pos, resetState);
        player.awardStat(Stats.ITEM_USED.get(resetState.getBlock().asItem()));
        applyHarvestCostsAndRewards(level, player, hand, pos, toolForLoot, sereneXpBoostEligible);
        return true;
    }

    private static boolean shouldApplyFortuneBonus(boolean inSeason) {
        return !Config.enableSereneSeasonsFortuneGating() || inSeason;
    }

    private static boolean isSereneXpBoostEligible(boolean inSeason) {
        return SereneSeasonsCompat.isLoaded() && inSeason;
    }

    private static void popHarvestDrops(
            ServerLevel level,
            BlockPos pos,
            BlockState harvestState,
            Player player,
            ItemStack toolForLoot,
            boolean removeReplant
    ) {
        BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        Item replantItem = harvestState.getCloneItemStack(hitResult, level, pos, player).getItem();
        boolean removedReplant = !removeReplant;

        for (ItemStack drop : Block.getDrops(harvestState, level, pos, null, player, toolForLoot)) {
            if (drop.isEmpty()) {
                continue;
            }

            ItemStack toPop = drop.copy();
            if (!removedReplant && toPop.getItem() == replantItem) {
                toPop.shrink(1);
                removedReplant = true;
            }

            if (!toPop.isEmpty()) {
                Block.popResource(level, pos, toPop);
            }
        }
    }

    private static void applyHarvestCostsAndRewards(
            Level level,
            Player player,
            InteractionHand hand,
            BlockPos pos,
            ItemStack toolForLoot,
            boolean sereneXpBoostEligible
    ) {
        // Damage the hoe once per harvested crop (if a hoe was used).
        if (!player.isCreative() && !toolForLoot.isEmpty()) {
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND
                    ? EquipmentSlot.MAINHAND
                    : EquipmentSlot.OFFHAND;
            toolForLoot.hurtAndBreak(1, player, slot);
        }

        // Reward a small amount of XP for each fully grown crop harvested.
        int xp = Config.xpForCrop(sereneXpBoostEligible);
        if (xp > 0) {
            level.addFreshEntity(new ExperienceOrb(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    xp
            ));
        }
    }

    private static boolean fireSyntheticHarvestBreakEvent(Level level, Player player, BlockPos pos, BlockState state) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return true;
        }

        var breakEvent = CommonHooks.fireBlockBreak(
                level,
                serverPlayer.gameMode.getGameModeForPlayer(),
                serverPlayer,
                pos,
                state
        );
        return !breakEvent.isCanceled();
    }

    private static boolean fireSyntheticReplantEvent(
            ServerLevel level,
            Player player,
            BlockPos pos,
            BlockState harvestState,
            BlockState resetState
    ) {
        level.setBlock(pos, resetState, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
        BlockSnapshot snapshot = BlockSnapshot.create(level.dimension(), level, pos);
        boolean canceled = EventHooks.onBlockPlace(player, snapshot, Direction.UP);
        level.setBlock(pos, harvestState, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
        return !canceled;
    }

    private static boolean isMatureCropLike(Level level, BlockPos pos, BlockState state) {
        CropLike cl = cropLike(level, pos, state);
        return cl != null && cl.mature();
    }

    private static boolean isTillable(BlockState state) {
        return state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT_PATH);
    }

    private static CropLike cropLike(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof CropBlock cropBlock) {
            IntegerProperty ageProp = findAgeProperty(state);
            BlockState reset = ageProp == null
                    ? cropBlock.getStateForAge(0)
                    : state.setValue(ageProp, minValue(ageProp.getPossibleValues()));
            return new CropLike(
                    cropBlock.isMaxAge(state),
                    reset
            );
        }

        if (!Config.enableGenericAgeCropHarvest()) {
            return null;
        }

        if (Config.useHarvestWhitelistTag() && !state.is(RIGHT_CLICK_HARVESTABLE)) {
            return null;
        }

        IntegerProperty ageProp = findAgeProperty(state);
        if (ageProp == null) {
            return null;
        }

        int age = state.getValue(ageProp);
        int max = maxValue(ageProp.getPossibleValues());
        int min = minValue(ageProp.getPossibleValues());
        boolean mature = age >= max;
        BlockState reset = state.setValue(ageProp, min);
        return new CropLike(mature, reset);
    }

    private static boolean tryHarvestViaUseWithoutItem(Level level, Player player, BlockPos pos) {
        // This should only be called server-side.
        BlockState state = level.getBlockState(pos);
        BlockHitResult bhr = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        InteractionResult r = state.useWithoutItem(level, player, bhr);
        return r.consumesAction();
    }

    private static IntegerProperty findAgeProperty(BlockState state) {
        for (var prop : state.getProperties()) {
            if (prop instanceof IntegerProperty ip && "age".equals(prop.getName())) {
                return ip;
            }
        }
        return null;
    }

    private static int minValue(Collection<Integer> values) {
        int min = Integer.MAX_VALUE;
        for (int v : values) {
            min = Math.min(min, v);
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }

    private static int maxValue(Collection<Integer> values) {
        int max = Integer.MIN_VALUE;
        for (int v : values) {
            max = Math.max(max, v);
        }
        return max == Integer.MIN_VALUE ? 0 : max;
    }

    private record CropLike(boolean mature, BlockState resetState) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("FarmTweaks server starting");

        // Debug: confirm the seed bag item + recipe are actually present at runtime.
        // This is intentionally noisy so we can quickly spot JSON/pack/namespace issues in logs.
        ResourceLocation seedBagId = ResourceLocation.fromNamespaceAndPath(MODID, "seed_bag");
        boolean itemRegistered = BuiltInRegistries.ITEM.containsKey(seedBagId);
        LOGGER.info("[SeedBag] Item registered in BuiltInRegistries: {} (id={})", itemRegistered, seedBagId);

        try {
            var rm = event.getServer().getRecipeManager();
            var recipe = rm.byKey(seedBagId);
            LOGGER.info("[SeedBag] Recipe present in RecipeManager: {} (id={})", recipe.isPresent(), seedBagId);

            if (recipe.isEmpty()) {
                int shown = (int) rm.getRecipeIds()
                        .filter(id -> MODID.equals(id.getNamespace()))
                        .limit(50)
                        .peek(id -> LOGGER.info("[SeedBag] Found recipe in namespace {}: {}", MODID, id))
                        .count();
                if (shown == 0) {
                    LOGGER.info("[SeedBag] No recipes found for namespace {} at all. This usually means data files are not being loaded.", MODID);
                } else if (shown >= 50) {
                    LOGGER.info("[SeedBag] (more recipes omitted; showing first 50)");
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("[SeedBag] Failed to query RecipeManager for diagnostics: {}", t.toString());
        }

        // Debug logging for tag membership / destroy speed (keep commented unless actively troubleshooting).
        // if (Config.logToolPreferenceDebug) {
        //     BlockState pumpkin = Blocks.PUMPKIN.defaultBlockState();
        //     BlockState melon = Blocks.MELON.defaultBlockState();
        //
        //     LOGGER.info("Pumpkin in mineable/hoe: {}", pumpkin.is(BlockTags.MINEABLE_WITH_HOE));
        //     LOGGER.info("Pumpkin in mineable/axe: {}", pumpkin.is(BlockTags.MINEABLE_WITH_AXE));
        //     LOGGER.info("Melon in mineable/hoe: {}", melon.is(BlockTags.MINEABLE_WITH_HOE));
        //     LOGGER.info("Melon in mineable/axe: {}", melon.is(BlockTags.MINEABLE_WITH_AXE));
        //
        //     ItemStack woodenHoe = new ItemStack(Items.WOODEN_HOE);
        //     ItemStack woodenAxe = new ItemStack(Items.WOODEN_AXE);
        //
        //     LOGGER.info("DestroySpeed wooden hoe on pumpkin: {}", woodenHoe.getDestroySpeed(pumpkin));
        //     LOGGER.info("DestroySpeed wooden axe on pumpkin: {}", woodenAxe.getDestroySpeed(pumpkin));
        //     LOGGER.info("DestroySpeed wooden hoe on melon: {}", woodenHoe.getDestroySpeed(melon));
        //     LOGGER.info("DestroySpeed wooden axe on melon: {}", woodenAxe.getDestroySpeed(melon));
        // }
    }
}
