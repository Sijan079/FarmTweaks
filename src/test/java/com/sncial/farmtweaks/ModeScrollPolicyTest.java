package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ModeScrollPolicyTest {
    @Test
    void requires_controls_control_key_a_scroll_and_a_supported_item() {
        assertTrue(ModeScrollPolicy.shouldCycle(true, -1.0D, true, true));
        assertFalse(ModeScrollPolicy.shouldCycle(false, -1.0D, true, true));
        assertFalse(ModeScrollPolicy.shouldCycle(true, 0.0D, true, true));
        assertFalse(ModeScrollPolicy.shouldCycle(true, -1.0D, false, true));
        assertFalse(ModeScrollPolicy.shouldCycle(true, -1.0D, true, false));
    }

    @Test
    void treats_downward_scroll_as_forward_unless_inverted() {
        assertTrue(ModeScrollPolicy.isForward(-1.0D, false));
        assertFalse(ModeScrollPolicy.isForward(1.0D, false));
        assertFalse(ModeScrollPolicy.isForward(-1.0D, true));
        assertTrue(ModeScrollPolicy.isForward(1.0D, true));
    }
}
