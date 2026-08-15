package com.sncial.farmtweaks;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CycleHoeModePayload(boolean forward) implements CustomPacketPayload {
    public static final Type<CycleHoeModePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FarmTweaks.MODID, "cycle_hoe_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CycleHoeModePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            CycleHoeModePayload::forward,
            CycleHoeModePayload::new
    );

    @Override
    public Type<CycleHoeModePayload> type() {
        return TYPE;
    }
}
