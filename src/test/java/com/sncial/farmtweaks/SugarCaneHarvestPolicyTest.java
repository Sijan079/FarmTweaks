package com.sncial.farmtweaks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SugarCaneHarvestPolicyTest {
    @Test
    void keeps_the_bottom_stalk_when_clicking_the_base() {
        assertEquals(2, SugarCaneHarvestPolicy.segmentCount(64, 64, 66));
    }

    @Test
    void harvests_the_clicked_segment_and_any_segments_above_it() {
        assertEquals(2, SugarCaneHarvestPolicy.segmentCount(64, 65, 66));
        assertEquals(1, SugarCaneHarvestPolicy.segmentCount(64, 66, 66));
    }

    @Test
    void does_not_harvest_a_single_stalk() {
        assertEquals(0, SugarCaneHarvestPolicy.segmentCount(64, 64, 64));
    }
}
