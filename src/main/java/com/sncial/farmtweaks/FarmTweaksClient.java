package com.sncial.farmtweaks;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = FarmTweaks.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FarmTweaksClient {
    private FarmTweaksClient() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    FarmTweaks.SEED_BAG.get(),
                    ResourceLocation.fromNamespaceAndPath(FarmTweaks.MODID, "seed_bag_full"),
                    (stack, level, entity, seed) -> SeedBagItem.isFull(stack) ? 1.0f : 0.0f
            );
        });
    }
}

