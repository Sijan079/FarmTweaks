package com.sncial.farmtweaks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeConfigRestartPolicyTest {
    @Test
    void requires_restart_when_pumpkin_slice_recipe_mode_changes() {
        assertTrue(RecipeConfigRestartPolicy.requiresRestart(false, false, true, false));
        assertTrue(RecipeConfigRestartPolicy.requiresRestart(true, false, true, true));
    }

    @Test
    void does_not_require_restart_for_unchanged_recipe_mode() {
        assertFalse(RecipeConfigRestartPolicy.requiresRestart(true, true, true, true));
    }

    @Test
    void declining_restart_restores_the_original_recipe_settings() {
        assertEquals(
                new RecipeConfigRestartPolicy.RecipeSettings(true, false),
                RecipeConfigRestartPolicy.settingsAfterDecision(false, true, false, false, true)
        );
    }
}
