package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class FlowerCropTypeTest {
    @Test
    void covers_every_vanilla_small_flower_available_in_minecraft_1_21_1() {
        assertEquals(13, FlowerCropType.values().length);
        assertTrue(Arrays.stream(FlowerCropType.values())
                .anyMatch(type -> type.id().equals("wither_rose")));
        assertTrue(Arrays.stream(FlowerCropType.values())
                .anyMatch(type -> type.id().equals("lily_of_the_valley")));
    }
}
