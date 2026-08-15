package com.sncial.farmtweaks;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

final class HoeModeData {
    private static final String NBT_ROOT = "farmtweaks";
    private static final String NBT_MODE = "hoeMode";

    private HoeModeData() {
    }

    static HoeTillingMode read(ItemStack hoe) {
        CustomData customData = hoe.get(DataComponents.CUSTOM_DATA);
        CompoundTag full = customData == null ? new CompoundTag() : customData.copyTag();
        return HoeTillingMode.fromSerializedName(full.getCompound(NBT_ROOT).getString(NBT_MODE));
    }

    static HoeTillingMode cycle(ItemStack hoe) {
        return cycle(hoe, true);
    }

    static HoeTillingMode cycle(ItemStack hoe, boolean forward) {
        HoeTillingMode next = forward ? read(hoe).next() : read(hoe).previous();
        CustomData customData = hoe.get(DataComponents.CUSTOM_DATA);
        CompoundTag full = customData == null ? new CompoundTag() : customData.copyTag();
        CompoundTag root = full.getCompound(NBT_ROOT);
        root.putString(NBT_MODE, next.name());
        full.put(NBT_ROOT, root);
        hoe.set(DataComponents.CUSTOM_DATA, CustomData.of(full));
        return next;
    }
}
