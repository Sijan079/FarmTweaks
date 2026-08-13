package com.sncial.farmtweaks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PumpkinRecipePolicyTest {
    @Test
    void selects_only_the_vanilla_pumpkin_pie_recipe_for_replacement() {
        assertTrue(PumpkinRecipePolicy.replacesVanillaRecipe("minecraft", "pumpkin_pie"));
        assertFalse(PumpkinRecipePolicy.replacesVanillaRecipe("farmtweaks", "pumpkin_pie"));
        assertFalse(PumpkinRecipePolicy.replacesVanillaRecipe("minecraft", "pumpkin_seeds"));
    }
}
