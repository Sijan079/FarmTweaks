package com.sncial.farmtweaks;

final class ModeScrollPolicy {
    private ModeScrollPolicy() {
    }

    static boolean shouldCycle(boolean controlDown, double scrollDeltaY, boolean enabled, boolean supportedItem) {
        return controlDown && scrollDeltaY != 0.0D && enabled && supportedItem;
    }

    static boolean isForward(double scrollDeltaY, boolean inverted) {
        return (scrollDeltaY < 0.0D) != inverted;
    }
}
