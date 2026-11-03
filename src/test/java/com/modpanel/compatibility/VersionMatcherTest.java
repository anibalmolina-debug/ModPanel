package com.modpanel.compatibility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionMatcherTest {
    @Test
    void matches() {
        VersionMatcher vm = new VersionMatcher();
        assertTrue(vm.matches("1.2.3", "1.0.0", "2.0.0"));
        assertFalse(vm.matches("0.5.0", "1.0.0", "2.0.0"));
    }
}