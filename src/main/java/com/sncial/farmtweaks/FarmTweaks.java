package com.sncial.farmtweaks;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.Map;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(FarmTweaks.MODID)
public class FarmTweaks {
    public static final String MODID = "farmtweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final TagKey<Item> SEEDLIKE_ITEMS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "seedlike")
    );

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<SeedBagItem> SEED_BAG = ITEMS.register("seed_bag", () -> new SeedBagItem(SeedBagTier.BASIC, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<SeedBagItem> GOLD_SEED_BAG = ITEMS.register("gold_seed_bag", () -> new SeedBagItem(SeedBagTier.GOLD, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<SeedBagItem> DIAMOND_SEED_BAG = ITEMS.register("diamond_seed_bag", () -> new SeedBagItem(SeedBagTier.DIAMOND, new Item.Properties().stacksTo(1)));
    public static final Map<FlowerCropType, DeferredHolder<net.minecraft.world.level.block.Block, FlowerCropBlock>> FLOWER_CROPS = new EnumMap<>(FlowerCropType.class);
    public static final Map<FlowerCropType, DeferredItem<ItemNameBlockItem>> FLOWER_SEEDS = new EnumMap<>(FlowerCropType.class);

    static {
        for (FlowerCropType type : FlowerCropType.values()) {
            var crop = BLOCKS.register(type.id() + "_crop", () -> new FlowerCropBlock(() -> FLOWER_SEEDS.get(type).get()));
            FLOWER_CROPS.put(type, crop);
            FLOWER_SEEDS.put(type, ITEMS.register(type.id() + "_seeds", () -> new ItemNameBlockItem(crop.get(), new Item.Properties())));
        }
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FARM_TWEAKS_TAB = CREATIVE_MODE_TABS.register("farmtweaks", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.farmtweaks"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> SEED_BAG.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                if (Config.enableSeedBags()) {
                    output.accept(SEED_BAG.get());
                    output.accept(GOLD_SEED_BAG.get());
                    output.accept(DIAMOND_SEED_BAG.get());
                }
                if (Config.enableFlowerSeeds()) {
                    FLOWER_SEEDS.values().forEach(seed -> output.accept(seed.get()));
                }
            }).build());

    public FarmTweaks(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC, "farmtweaks.toml");

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("FarmTweaks common setup");
    }

    @SubscribeEvent
    public void onVanillaFlowerBroken(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !(event.getLevel() instanceof Level level)
                || level.isClientSide() || !Config.enableFlowerSeeds()) {
            return;
        }

        flowerType(event.getState()).ifPresent(type -> {
            if (level.random.nextInt(100) < Config.flowerSeedDropChancePercent()) {
                Block.popResource(level, event.getPos(), new ItemStack(FLOWER_SEEDS.get(type).get()));
            }
        });
    }

    @SubscribeEvent
    public void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (FarmlandProtection.cancelTrample()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onHoeBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack tool = event.getEntity().getMainHandItem();
        if (!(tool.getItem() instanceof HoeItem) || !isHoeEfficiencySpeedTarget(event.getState())) {
            return;
        }

        int efficiency = efficiencyLevel(event.getEntity().level(), tool);
        if (efficiency > 0 && HoeEfficiencySpeed.needsCustomBonus(tool.getDestroySpeed(event.getState()))) {
            event.setNewSpeed(event.getNewSpeed() + HoeEfficiencySpeed.bonus(efficiency));
        }
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
            HoeTillingMode tillingMode = HoeTillingMode.fromTarget(state.is(Blocks.FARMLAND));

            if (!level.isClientSide() && isTillingTarget(level, player, event.getHand(), pos, tillingMode)) {
                // Sneaking/crouching explicitly disables AoE: only ever till the clicked block.
                if (sneaking) {
                    BlockState tilledState = resolveTilledState(level, player, event.getHand(), pos, tillingMode);
                    if (tilledState != null) {
                        applyTilledState(level, player, pos, state, tilledState, tillingMode);
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

                int tilledCount = 0;
                boolean tilledAny = false;
                int durabilityLimit = HoeOperationLimits.maxActions(Integer.MAX_VALUE, remainingDurability(tool), player.isCreative());

                int sideLength = hoeSideLength(level, tool);
                for (FootprintBoundary.Cell cell : HoeTillingArea.cells(pos.getX(), pos.getZ(), false, sideLength)) {
                    if (tilledCount >= durabilityLimit) {
                        break;
                    }
                    BlockPos cur = new BlockPos(cell.x(), pos.getY(), cell.z());

                    if (!isTillingTarget(level, player, event.getHand(), cur, tillingMode)) {
                        continue;
                    }

                    BlockState tilledState = resolveTilledState(level, player, event.getHand(), cur, tillingMode);
                    if (tilledState != null) {
                        applyTilledState(level, player, cur, level.getBlockState(cur), tilledState, tillingMode);
                        tilledAny = true;
                        tilledCount++;

                        if (!player.isCreative()) {
                            EquipmentSlot slot = event.getHand() == InteractionHand.MAIN_HAND
                                    ? EquipmentSlot.MAINHAND
                                    : EquipmentSlot.OFFHAND;
                            tool.hurtAndBreak(1, player, slot);
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
                maxHarvestCount = HoeOperationLimits.maxActions(maxHarvestCount, remainingDurability(tool), player.isCreative());
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

                if (!harvestedThis) {
                    continue;
                }

                if (!appliedCostsAndRewards) {
                    applyHarvestCostsAndRewards(level, player, event.getHand(), pos, toolForLoot);
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

        player.awardStat(Stats.BLOCK_MINED.get(harvestState.getBlock()));
        if (!toolForLoot.isEmpty()) {
            player.awardStat(Stats.ITEM_USED.get(toolForLoot.getItem()));
        }

        popHarvestDrops(serverLevel, pos, harvestState, player, toolForLoot, true);

        // Fortune bonus: add extra non-seed drops for "wheat-like" crops that drop both produce + seeds.
        if (fortuneLevel > 0) {
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
        applyHarvestCostsAndRewards(level, player, hand, pos, toolForLoot);
        return true;
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
            ItemStack toolForLoot
    ) {
        // Damage the hoe once per harvested crop (if a hoe was used).
        if (!player.isCreative() && !toolForLoot.isEmpty()) {
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND
                    ? EquipmentSlot.MAINHAND
                    : EquipmentSlot.OFFHAND;
            toolForLoot.hurtAndBreak(1, player, slot);
        }

        // Reward a small amount of XP for each fully grown crop harvested.
        int xp = Config.xpPerCrop();
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

    private static java.util.Optional<FlowerCropType> flowerType(BlockState state) {
        if (state.is(Blocks.DANDELION)) return java.util.Optional.of(FlowerCropType.DANDELION);
        if (state.is(Blocks.POPPY)) return java.util.Optional.of(FlowerCropType.POPPY);
        if (state.is(Blocks.BLUE_ORCHID)) return java.util.Optional.of(FlowerCropType.BLUE_ORCHID);
        if (state.is(Blocks.ALLIUM)) return java.util.Optional.of(FlowerCropType.ALLIUM);
        if (state.is(Blocks.AZURE_BLUET)) return java.util.Optional.of(FlowerCropType.AZURE_BLUET);
        if (state.is(Blocks.RED_TULIP)) return java.util.Optional.of(FlowerCropType.RED_TULIP);
        if (state.is(Blocks.ORANGE_TULIP)) return java.util.Optional.of(FlowerCropType.ORANGE_TULIP);
        if (state.is(Blocks.WHITE_TULIP)) return java.util.Optional.of(FlowerCropType.WHITE_TULIP);
        if (state.is(Blocks.PINK_TULIP)) return java.util.Optional.of(FlowerCropType.PINK_TULIP);
        if (state.is(Blocks.OXEYE_DAISY)) return java.util.Optional.of(FlowerCropType.OXEYE_DAISY);
        if (state.is(Blocks.CORNFLOWER)) return java.util.Optional.of(FlowerCropType.CORNFLOWER);
        if (state.is(Blocks.LILY_OF_THE_VALLEY)) return java.util.Optional.of(FlowerCropType.LILY_OF_THE_VALLEY);
        if (state.is(Blocks.WITHER_ROSE)) return java.util.Optional.of(FlowerCropType.WITHER_ROSE);
        return java.util.Optional.empty();
    }

    private static CropLike cropLike(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof CropBlock cropBlock) {
            return new CropLike(cropBlock.isMaxAge(state), cropBlock.getStateForAge(0));
        }
        return null;
    }

    private static int remainingDurability(ItemStack tool) {
        return Math.max(0, tool.getMaxDamage() - tool.getDamageValue());
    }

    static int hoeSideLength(Level level, ItemStack tool) {
        if (!(tool.getItem() instanceof HoeItem hoe)) {
            return 1;
        }
        return HoeRange.sideLength(hoeTierSideLength(hoe.getTier()), efficiencyLevel(level, tool));
    }

    private static int hoeTierSideLength(Tier tier) {
        if (tier == Tiers.WOOD) return 1;
        if (tier == Tiers.STONE) return 2;
        if (tier == Tiers.IRON || tier == Tiers.GOLD) return 3;
        if (tier == Tiers.DIAMOND) return 4;
        if (tier == Tiers.NETHERITE) return 5;
        return 1;
    }

    static int efficiencyLevel(Level level, ItemStack tool) {
        return EnchantmentHelper.getItemEnchantmentLevel(
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY),
                tool
        );
    }

    private static boolean isHoeEfficiencySpeedTarget(BlockState state) {
        return state.is(Blocks.PUMPKIN)
                || state.is(Blocks.MELON)
                || state.is(Blocks.NETHER_WART)
                || state.is(Blocks.NETHER_WART_BLOCK)
                || state.is(Blocks.WARPED_WART_BLOCK)
                || state.is(Blocks.BROWN_MUSHROOM_BLOCK)
                || state.is(Blocks.RED_MUSHROOM_BLOCK)
                || state.is(Blocks.MUSHROOM_STEM);
    }

    private static BlockState hoeTilledState(
            Level level,
            Player player,
            InteractionHand hand,
            BlockPos pos,
            boolean simulate
    ) {
        BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        UseOnContext context = new UseOnContext(player, hand, hitResult);
        return level.getBlockState(pos).getToolModifiedState(context, ItemAbilities.HOE_TILL, simulate);
    }

    private static boolean isTillingTarget(
            Level level,
            Player player,
            InteractionHand hand,
            BlockPos pos,
            HoeTillingMode mode
    ) {
        return mode == HoeTillingMode.REVERT_TO_DIRT
                ? level.getBlockState(pos).is(Blocks.FARMLAND)
                : hoeTilledState(level, player, hand, pos, true) != null;
    }

    private static BlockState resolveTilledState(
            Level level,
            Player player,
            InteractionHand hand,
            BlockPos pos,
            HoeTillingMode mode
    ) {
        return mode == HoeTillingMode.REVERT_TO_DIRT
                ? Blocks.DIRT.defaultBlockState()
                : hoeTilledState(level, player, hand, pos, false);
    }

    private static void applyTilledState(
            Level level,
            Player player,
            BlockPos pos,
            BlockState previousState,
            BlockState nextState,
            HoeTillingMode mode
    ) {
        if (mode.usesVanillaFarmlandReversion()) {
            FarmBlock.turnToDirt(player, previousState, level, pos);
        } else {
            level.setBlockAndUpdate(pos, nextState);
        }
    }

    private record CropLike(boolean mature, BlockState resetState) {}
}
