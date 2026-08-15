package com.sncial.farmtweaks;

enum HoeTillingMode {
    TILL,
    UNTILL,
    HARVEST;

    HoeTillingMode next() {
        return switch (this) {
            case TILL -> UNTILL;
            case UNTILL -> HARVEST;
            case HARVEST -> TILL;
        };
    }

    HoeTillingMode previous() {
        return switch (this) {
            case TILL -> HARVEST;
            case UNTILL -> TILL;
            case HARVEST -> UNTILL;
        };
    }

    static HoeTillingMode fromSerializedName(String value) {
        if ("NONE".equals(value)) {
            return HARVEST;
        }
        for (HoeTillingMode mode : values()) {
            if (mode.name().equals(value)) {
                return mode;
            }
        }
        return TILL;
    }

    boolean usesVanillaFarmlandReversion() {
        return this == UNTILL;
    }

    boolean allowsTilling() {
        return this != HARVEST;
    }

    int hudColor() {
        return switch (this) {
            case TILL -> 0x4DE633;
            case UNTILL -> 0xF25933;
            case HARVEST -> 0xFFFFFF;
        };
    }

    String translationKey() {
        return "tooltip.farmtweaks.hoe_mode." + name().toLowerCase();
    }
}
