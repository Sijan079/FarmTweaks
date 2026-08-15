package com.sncial.farmtweaks;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CycleSeedBagShapePayload(boolean forward) implements CustomPacketPayload {
    public static final Type<CycleSeedBagShapePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FarmTweaks.MODID, "cycle_seed_bag_shape"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CycleSeedBagShapePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            CycleSeedBagShapePayload::forward,
            CycleSeedBagShapePayload::new
    );

    @Override
    public Type<CycleSeedBagShapePayload> type() {
        return TYPE;
    }
}
