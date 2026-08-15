package com.sncial.farmtweaks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FarmTweaksConfigTabTest {
    @Test
    void exposes_feature_oriented_tabs() {
        assertEquals(7, FarmTweaksConfigTab.values().length);
        assertEquals("config.farmtweaks.tab.harvesting", FarmTweaksConfigTab.HARVESTING.translationKey());
        assertEquals("config.farmtweaks.tab.hoe_actions", FarmTweaksConfigTab.HOE_ACTIONS.translationKey());
        assertEquals("config.farmtweaks.tab.controls", FarmTweaksConfigTab.CONTROLS.translationKey());
    }
}
