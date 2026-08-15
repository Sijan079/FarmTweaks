package com.sncial.farmtweaks;

import net.neoforged.neoforge.common.ModConfigSpec;

final class ClientConfig {
    static final ModConfigSpec.BooleanValue SHOW_MODE_HUD;
    static final ModConfigSpec.BooleanValue ENABLE_MODE_SCROLLING;
    static final ModConfigSpec.BooleanValue INVERT_MODE_SCROLL_DIRECTION;
    static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("FarmTweaks client preferences").translation("config.farmtweaks.title").push("client");
        SHOW_MODE_HUD = builder.comment("Shows the current Hoe or Seed Bag mode above the hotbar.")
                .translation("config.farmtweaks.controls.showModeHud").define("showModeHud", true);
        ENABLE_MODE_SCROLLING = builder.comment("Allows Ctrl+scroll to cycle Hoe and Seed Bag modes.")
                .translation("config.farmtweaks.controls.enableModeScrolling").define("enableModeScrolling", true);
        INVERT_MODE_SCROLL_DIRECTION = builder.comment("Reverses the Ctrl+scroll direction used to cycle modes.")
                .translation("config.farmtweaks.controls.invertModeScrollDirection").define("invertModeScrollDirection", false);
        builder.pop();
        SPEC = builder.build();
    }

    private ClientConfig() {
    }

    static boolean showModeHud() { return SHOW_MODE_HUD.get(); }
    static boolean enableModeScrolling() { return ENABLE_MODE_SCROLLING.get(); }
    static boolean invertModeScrollDirection() { return INVERT_MODE_SCROLL_DIRECTION.get(); }
}
