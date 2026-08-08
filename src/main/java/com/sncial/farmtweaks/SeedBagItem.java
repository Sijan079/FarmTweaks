package com.sncial.farmtweaks;

import java.util.Optional;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.Container;

// Extends BundleItem intentionally: MouseTweaks special-cases BundleItem for RMB-dragging compatibility
// ("bundle-like" items are expected to subclass BundleItem).
public class SeedBagItem extends BundleItem {
    private static final String NBT_ROOT = "farmtweaks";
    private static final String NBT_SEED_STACK = "seedStack";
    private static final String NBT_COUNT = "count";

    private static final int CAPACITY = 640; // 10 stacks

    private static final TagKey<Item> C_SEEDS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("c", "seeds")
    );

    private static final TagKey<Item> SEED_BAG_PLANTABLES = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(FarmTweaks.MODID, "seed_bag_plantables")
    );

    public SeedBagItem(Properties props) {
        super(props);
    }

    public static boolean isFull(ItemStack stack) {
        return read(stack).count >= CAPACITY;
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        // Allow enchanting at tables/random (primary items).
        if (enchantment != null && enchantment.is(Enchantments.EFFICIENCY)) {
            return true;
        }
        return super.isPrimaryItemFor(stack, enchantment);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        // Allow anvils/etc.
        if (enchantment != null && enchantment.is(Enchantments.EFFICIENCY)) {
            return true;
        }
        return super.supportsEnchantment(stack, enchantment);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (!Config.enableSeedBags()) {
            tooltip.add(Component.literal("Disabled in config").withStyle(ChatFormatting.RED));
            return;
        }

        SeedBagData data = read(stack);
        if (data.seedPrototype.isEmpty() || data.count <= 0) {
            tooltip.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltip.add(Component.literal("Seeds: ").withStyle(ChatFormatting.GRAY)
                .append(data.seedPrototype.getHoverName().copy().withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal("Stored: " + data.count + " / " + CAPACITY).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        SeedBagData data = read(stack);
        return data.count > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        SeedBagData data = read(stack);
        return Math.min(13, (int) Math.round(13.0 * data.count / CAPACITY));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // Same-ish vibe as bundle progress: green-ish, shifts toward yellow as it fills.
        SeedBagData data = read(stack);
        float t = Math.min(1.0f, Math.max(0.0f, (float) data.count / (float) CAPACITY));
        int r = (int) (120 + (80 * t));
        int g = (int) (200 - (40 * t));
        int b = 60;
        return (r << 16) | (g << 8) | b;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bag = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.pass(bag);
        }

        if (!Config.enableSeedBags()) {
            return InteractionResultHolder.pass(bag);
        }

        // Intentionally no "right-click in air" quick-deposit behavior.
        // It tends to conflict with normal right-click interactions on modded servers (claims, tools, etc.).
        return InteractionResultHolder.pass(bag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null || level.isClientSide()) {
            return InteractionResult.PASS;
        }

        if (!Config.enableSeedBags()) {
            return InteractionResult.PASS;
        }

        ItemStack bag = context.getItemInHand();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        // Compatibility: wd's Selling Bin
        // Only when sneaking: dump the bag contents into the bin inventory.
        if (tryDumpIntoSellingBin(serverLevel, player, context, bag)) {
            player.swing(context.getHand(), true);
            return InteractionResult.SUCCESS;
        }

        int planted = plantAoE(serverLevel, player, context.getHand(), context.getClickedPos(), bag);
        if (planted > 0) {
            player.swing(context.getHand(), true);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack bag, Slot slot, ClickAction action, Player player) {
        // Only right-click actions should trigger bag behavior.
        if (bag.getCount() != 1 || action != ClickAction.SECONDARY) {
            return false;
        }
        if (!Config.enableSeedBags()) {
            return false;
        }
        if (!slot.allowModification(player)) {
            return false;
        }

        SeedBagData data = read(bag);
        ItemStack proto = data.seedPrototype;

        // If right-clicking an empty slot while holding the bag: pull seeds out.
        // We consume the click even when empty to prevent RMB-drag helpers (Mouse Tweaks / vanilla quick craft)
        // from placing the bag into the first empty slot touched during a drag.
        if (!slot.hasItem()) {
            if (proto.isEmpty() || data.count <= 0) {
                return true;
            }

            int take = Math.min(64, data.count);
            ItemStack out = proto.copyWithCount(take);
            ItemStack remainder = slot.safeInsert(out);
            int inserted = take - remainder.getCount();
            if (inserted > 0) {
                write(bag, new SeedBagData(proto, data.count - inserted));
            }
            return true;
        }

        // If right-clicking a slot with seeds: pull into bag (single-type).
        ItemStack in = slot.getItem();
        if (!isAllowedPlantable(in)) {
            return false;
        }

        if (proto.isEmpty()) {
            proto = in.copyWithCount(1);
            data = new SeedBagData(proto, 0);
        } else if (!ItemStack.isSameItemSameComponents(proto, in)) {
            return false;
        }

        int space = CAPACITY - data.count;
        if (space <= 0) {
            return true;
        }

        int move = Math.min(space, in.getCount());
        in.shrink(move);
        slot.set(in.isEmpty() ? ItemStack.EMPTY : in);
        write(bag, new SeedBagData(proto, data.count + move));
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack bag, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess cursorAccess) {
        // When the bag is in a slot and you click it with seeds on cursor, deposit.
        // Mouse Tweaks can replay click actions in ways that are not always strictly SECONDARY in modded screens,
        // so accept PRIMARY here too (depositing is safe; withdrawal is guarded elsewhere).
        if (action != ClickAction.SECONDARY && action != ClickAction.PRIMARY) {
            return false;
        }
        if (!Config.enableSeedBags()) {
            return false;
        }
        if (!slot.allowModification(player)) return false;

        if (other.isEmpty() || !isAllowedPlantable(other)) {
            return false;
        }

        SeedBagData data = read(bag);
        ItemStack proto = data.seedPrototype;

        if (proto.isEmpty()) {
            proto = other.copyWithCount(1);
            data = new SeedBagData(proto, 0);
        } else if (!ItemStack.isSameItemSameComponents(proto, other)) {
            return false;
        }

        int space = CAPACITY - data.count;
        if (space <= 0) {
            return true;
        }

        int move = Math.min(space, other.getCount());
        ItemStack newOther = other.copy();
        newOther.shrink(move);
        cursorAccess.set(newOther.isEmpty() ? ItemStack.EMPTY : newOther);
        write(bag, new SeedBagData(proto, data.count + move));
        return true;
    }

    private static void depositFromInventory(Player player, ItemStack bag) {
        SeedBagData data = read(bag);
        ItemStack proto = data.seedPrototype;

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack inv = player.getInventory().getItem(slot);
            if (inv.isEmpty() || !isAllowedPlantable(inv)) {
                continue;
            }

            if (proto.isEmpty()) {
                // First seed type becomes the bag's type (exact components).
                proto = inv.copyWithCount(1);
                data = new SeedBagData(proto, 0);
            } else if (!ItemStack.isSameItemSameComponents(proto, inv)) {
                continue;
            }

            int space = CAPACITY - data.count;
            if (space <= 0) {
                break;
            }

            int move = Math.min(space, inv.getCount());
            inv.shrink(move);
            player.getInventory().setItem(slot, inv.isEmpty() ? ItemStack.EMPTY : inv);
            data = new SeedBagData(proto, data.count + move);
        }

        write(bag, data);
    }

    private static int plantAoE(ServerLevel level, Player player, InteractionHand hand, BlockPos clickedPos, ItemStack bag) {
        SeedBagData data = read(bag);
        ItemStack seedProto = data.seedPrototype;
        if (seedProto.isEmpty() || data.count <= 0) {
            return 0;
        }

        int radius = 0;
        if (!player.isShiftKeyDown()) {
            int efficiency = EnchantmentHelper.getItemEnchantmentLevel(
                    level.registryAccess()
                            .lookupOrThrow(Registries.ENCHANTMENT)
                            .getOrThrow(Enchantments.EFFICIENCY),
                    bag
            );
            radius = switch (efficiency) {
                case 1, 2 -> 1;
                case 3, 4 -> 2;
                default -> efficiency > 0 ? 3 : 0;
            };
        }

        int planted = 0;

        for (int dx = -radius; dx <= radius && data.count > 0; dx++) {
            for (int dz = -radius; dz <= radius && data.count > 0; dz++) {
                if (Math.abs(dx) + Math.abs(dz) > radius) {
                    continue;
                }

                BlockPos pos = clickedPos.offset(dx, 0, dz);
                if (!level.isEmptyBlock(pos.above())) {
                    continue;
                }

                // Delegate to the seed item's placement behavior.
                ItemStack oneSeed = seedProto.copyWithCount(1);
                Vec3 hit = Vec3.atCenterOf(pos);
                BlockHitResult bhr = new BlockHitResult(hit, Direction.UP, pos, false);
                UseOnContext uoc = new UseOnContext(level, player, hand, oneSeed, bhr);

                InteractionResult result = oneSeed.useOn(uoc);
                if (result.consumesAction()) {
                    planted++;
                    data = new SeedBagData(seedProto, data.count - 1);
                }
            }
        }

        if (planted > 0) {
            write(bag, data);
        }

        return planted;
    }

    private static SeedBagData read(ItemStack bag) {
        CompoundTag root = getRootTag(bag);
        int count = root.getInt(NBT_COUNT);

        if (count <= 0) {
            return new SeedBagData(ItemStack.EMPTY, 0);
        }

        ItemStack seed = ItemStack.EMPTY;
        if (root.contains(NBT_SEED_STACK)) {
            Tag t = root.get(NBT_SEED_STACK);
            if (t != null) {
                Optional<ItemStack> decoded = ItemStack.CODEC.parse(NbtOps.INSTANCE, t)
                        .resultOrPartial(msg -> FarmTweaks.LOGGER.warn("SeedBag decode error: {}", msg));
                if (decoded.isPresent()) {
                    seed = decoded.get().copyWithCount(1);
                }
            }
        }

        return new SeedBagData(seed, clampCount(count));
    }

    private static void write(ItemStack bag, SeedBagData data) {
        CompoundTag root = new CompoundTag();
        int count = clampCount(data.count);
        root.putInt(NBT_COUNT, count);

        // If empty, clear the stored seed type so the bag can accept a new one.
        if (count > 0 && !data.seedPrototype.isEmpty()) {
            ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, data.seedPrototype.copyWithCount(1))
                    .resultOrPartial(msg -> FarmTweaks.LOGGER.warn("SeedBag encode error: {}", msg))
                    .ifPresent(tag -> root.put(NBT_SEED_STACK, tag));
        }

        setRootTag(bag, root);
    }

    private static int clampCount(int count) {
        return Math.max(0, Math.min(CAPACITY, count));
    }

    private record SeedBagData(ItemStack seedPrototype, int count) {}

    private static CompoundTag getRootTag(ItemStack bag) {
        CustomData customData = bag.get(DataComponents.CUSTOM_DATA);
        CompoundTag full = customData == null ? new CompoundTag() : customData.copyTag();
        return full.getCompound(NBT_ROOT);
    }

    private static void setRootTag(ItemStack bag, CompoundTag root) {
        CustomData customData = bag.get(DataComponents.CUSTOM_DATA);
        CompoundTag full = customData == null ? new CompoundTag() : customData.copyTag();
        full.put(NBT_ROOT, root);
        bag.set(DataComponents.CUSTOM_DATA, CustomData.of(full));
    }

    private static boolean isAllowedPlantable(ItemStack stack) {
        return stack.is(C_SEEDS) || stack.is(SEED_BAG_PLANTABLES);
    }

    private static boolean tryDumpIntoSellingBin(ServerLevel level, Player player, UseOnContext context, ItemStack bag) {
        if (!player.isShiftKeyDown()) {
            return false;
        }

        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (id == null || !"selling_bin".equals(id.getNamespace())) {
            return false;
        }

        SeedBagData data = read(bag);
        if (data.seedPrototype.isEmpty() || data.count <= 0) {
            return false;
        }

        // Avoid hard-depending on Selling Bin internals: use the vanilla Container interface.
        // Selling Bin's block entity implements WorldlyContainer, so this should work reliably.
        var be = level.getBlockEntity(pos);
        if (!(be instanceof Container container)) {
            return false;
        }

        ItemStack proto = data.seedPrototype.copyWithCount(1);
        int remaining = data.count;
        int insertedTotal = 0;

        // Insert in reasonable chunks to respect max stack size and handlers that only accept small amounts.
        int maxStack = Math.max(1, proto.getMaxStackSize());
        while (remaining > 0) {
            int batch = Math.min(remaining, maxStack);
            ItemStack toInsert = proto.copyWithCount(batch);
            int inserted = insertIntoContainer(container, toInsert);
            if (inserted <= 0) {
                break;
            }
            insertedTotal += inserted;
            remaining -= inserted;
        }

        if (insertedTotal <= 0) {
            return false;
        }

        write(bag, new SeedBagData(proto, data.count - insertedTotal));
        return true;
    }

    private static int insertIntoContainer(Container container, ItemStack stack) {
        if (stack.isEmpty()) return 0;

        int inserted = 0;
        int count = stack.getCount();
        for (int i = 0; i < container.getContainerSize() && inserted < count; i++) {
            ItemStack slotStack = container.getItem(i);
            int max = Math.min(container.getMaxStackSize(stack), stack.getMaxStackSize());

            if (slotStack.isEmpty()) {
                int move = Math.min(max, count - inserted);
                ItemStack placed = stack.copyWithCount(move);
                container.setItem(i, placed);
                inserted += move;
                continue;
            }

            if (!ItemStack.isSameItemSameComponents(slotStack, stack)) {
                continue;
            }

            int space = max - slotStack.getCount();
            if (space <= 0) {
                continue;
            }

            int move = Math.min(space, count - inserted);
            slotStack.grow(move);
            container.setItem(i, slotStack);
            inserted += move;
        }
        if (inserted > 0) {
            container.setChanged();
        }
        return inserted;
    }
}
