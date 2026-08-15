package com.sncial.farmtweaks;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

@EventBusSubscriber(modid = FarmTweaks.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@Mod(value = FarmTweaks.MODID, dist = Dist.CLIENT)
public final class FarmTweaksClient {
    public FarmTweaksClient(ModContainer modContainer) {
        registerConfigScreen(modContainer);
    }

    public static void registerConfigScreen(ModContainer modContainer) {
        IConfigScreenFactory factory = (minecraft, parent) -> FarmTweaksClothConfigScreen.create(parent);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, factory);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerSeedBagFullness(FarmTweaks.SEED_BAG.get());
            registerSeedBagFullness(FarmTweaks.GOLD_SEED_BAG.get());
            registerSeedBagFullness(FarmTweaks.DIAMOND_SEED_BAG.get());
        });
    }

    private static void registerSeedBagFullness(SeedBagItem bag) {
        ItemProperties.register(
                bag,
                ResourceLocation.fromNamespaceAndPath(FarmTweaks.MODID, "seed_bag_full"),
                (stack, level, entity, seed) -> bag.isFull(stack) ? 1.0f : 0.0f
        );
    }

    @SubscribeEvent
    public static void registerSeedBagTierMarkers(RegisterItemDecorationsEvent event) {
        event.register(FarmTweaks.GOLD_SEED_BAG.get(), (graphics, font, stack, x, y) -> renderTierMarker(graphics, x, y, SeedBagTier.GOLD));
        event.register(FarmTweaks.DIAMOND_SEED_BAG.get(), (graphics, font, stack, x, y) -> renderTierMarker(graphics, x, y, SeedBagTier.DIAMOND));
    }

    private static boolean renderTierMarker(GuiGraphics graphics, int x, int y, SeedBagTier tier) {
        int[] colors = tier.guiMarkerPixels();
        graphics.fill(x + 1, y + 1, x + 2, y + 2, colors[0]);
        graphics.fill(x + 2, y + 1, x + 3, y + 2, colors[1]);
        graphics.fill(x + 1, y + 2, x + 2, y + 3, colors[2]);
        graphics.fill(x + 2, y + 2, x + 3, y + 3, colors[3]);
        return false;
    }
}
