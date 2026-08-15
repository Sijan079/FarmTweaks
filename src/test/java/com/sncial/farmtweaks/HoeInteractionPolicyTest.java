package com.sncial.farmtweaks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HoeInteractionPolicyTest {
    @Test
    void custom_tilling_cancels_vanilla_handling_on_both_client_and_server() {
        assertTrue(HoeInteractionPolicy.ownsVanillaTillingAction(true, true));
    }

    @Test
    void custom_tilling_does_not_cancel_unrelated_interactions() {
        assertFalse(HoeInteractionPolicy.ownsVanillaTillingAction(false, true));
        assertFalse(HoeInteractionPolicy.ownsVanillaTillingAction(true, false));
    }
}
