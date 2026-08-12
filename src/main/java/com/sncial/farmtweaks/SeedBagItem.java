package com.sncial.farmtweaks;

import java.util.Optional;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SeedBagItem extends Item {
    private static final String NBT_ROOT = "farmtweaks";
    private static final String NBT_SEED_STACK = "seedStack";
    private static final String NBT_COUNT = "count";

    private final SeedBagTier tier;

    private static final TagKey<Item> C_SEEDS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("c", "seeds")
    );

    private static final TagKey<Item> SEED_BAG_PLANTABLES = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(FarmTweaks.MODID, "seed_bag_plantables")
    );

    public SeedBagItem(SeedBagTier tier, Properties props) {
        super(props);
        this.tier = tier;
    }

    public boolean isFull(ItemStack stack) {
        return read(stack).count >= tier.capacity();
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
        tooltip.add(Component.literal("Stored: " + data.count + " / " + tier.capacity()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        SeedBagData data = read(stack);
        return data.count > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        SeedBagData data = read(stack);
        return Math.min(13, (int) Math.round(13.0 * data.count / tier.capacity()));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // Same-ish vibe as bundle progress: green-ish, shifts toward yellow as it fills.
        SeedBagData data = read(stack);
        float t = Math.min(1.0f, Math.max(0.0f, (float) data.count / (float) tier.capacity()));
        int r = (int) (120 + (80 * t));
        int g = (int) (200 - (40 * t));
        int b = 60;
        return (r << 16) | (g << 8) | b;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        if (!Config.enableSeedBags()) {
            return InteractionResult.PASS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.CONSUME;
        }

        int radius = tier.plantingRadius(player.isShiftKeyDown());
        int planted = plantArea(serverLevel, player, context.getHand(), context.getClickedPos(), context.getItemInHand(), radius);
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

        int space = tier.capacity() - data.count;
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
        if (action != ClickAction.SECONDARY) {
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

        int space = tier.capacity() - data.count;
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

    private int plantArea(ServerLevel level, Player player, InteractionHand hand, BlockPos clickedPos, ItemStack bag, int radius) {
        SeedBagData data = read(bag);
        ItemStack seedProto = data.seedPrototype;
        if (seedProto.isEmpty() || data.count <= 0) {
            return 0;
        }

        int planted = 0;

        for (int dx = -radius; dx <= radius && data.count > 0; dx++) {
            for (int dz = -radius; dz <= radius && data.count > 0; dz++) {
                if (!Config.seedBagAoeShape().includes(dx, dz, radius)) {
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

    private SeedBagData read(ItemStack bag) {
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

    private void write(ItemStack bag, SeedBagData data) {
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

    private int clampCount(int count) {
        return Math.max(0, Math.min(tier.capacity(), count));
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

}
