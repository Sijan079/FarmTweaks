package com.sncial.farmtweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.HoeItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = FarmTweaks.MODID, value = Dist.CLIENT)
public final class FarmTweaksHoeModeClient {
    private FarmTweaksHoeModeClient() {
    }

    @SubscribeEvent
    public static void cycleHeldItemMode(InputEvent.MouseScrollingEvent event) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        var held = player.getMainHandItem();
        boolean hoe = held.getItem() instanceof HoeItem;
        boolean seedBag = held.getItem() instanceof SeedBagItem;
        if (!ModeScrollPolicy.shouldCycle(Screen.hasControlDown(), event.getScrollDeltaY(), ClientConfig.enableModeScrolling(), hoe || seedBag)) {
            return;
        }

        boolean forward = ModeScrollPolicy.isForward(event.getScrollDeltaY(), ClientConfig.invertModeScrollDirection());
        if (hoe) {
            event.setCanceled(true);
            PacketDistributor.sendToServer(new CycleHoeModePayload(forward));
        } else if (seedBag) {
            event.setCanceled(true);
            PacketDistributor.sendToServer(new CycleSeedBagShapePayload(forward));
        }
    }

    @SubscribeEvent
    public static void renderHeldHoeMode(RenderGuiEvent.Post event) {
        var player = Minecraft.getInstance().player;
        if (!ClientConfig.showModeHud() || player == null || !(player.getMainHandItem().getItem() instanceof HoeItem)) {
            return;
        }

        var graphics = event.getGuiGraphics();
        HoeTillingMode mode = HoeModeData.read(player.getMainHandItem());
        var text = net.minecraft.network.chat.Component.translatable(mode.translationKey());
        graphics.drawCenteredString(
                Minecraft.getInstance().font,
                text,
                graphics.guiWidth() / 2,
                graphics.guiHeight() - 47,
                mode.hudColor()
        );
    }

    @SubscribeEvent
    public static void renderHeldSeedBagShape(RenderGuiEvent.Post event) {
        var player = Minecraft.getInstance().player;
        if (!ClientConfig.showModeHud() || player == null || !(player.getMainHandItem().getItem() instanceof SeedBagItem)) {
            return;
        }

        var graphics = event.getGuiGraphics();
        var text = net.minecraft.network.chat.Component.translatable(SeedBagItem.plantingShape(player.getMainHandItem()).translationKey());
        graphics.drawCenteredString(
                Minecraft.getInstance().font,
                text,
                graphics.guiWidth() / 2,
                graphics.guiHeight() - 47,
                0xFFFFFF
        );
    }
}
