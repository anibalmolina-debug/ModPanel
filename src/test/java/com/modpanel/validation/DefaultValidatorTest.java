package com.modpanel.validation;

import com.modpanel.model.ConfigEntry;
import com.modpanel.model.SettingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultValidatorTest {
    @Test
    void validatesBooleanAndRange() {
        DefaultValidator v = new DefaultValidator();
        ConfigEntry b = new ConfigEntry("id", "s", "m", "k", SettingType.BOOLEAN, false, true, null, null, null, null, null);
        assertTrue(v.validate(b, true).valid());
        assertFalse(v.validate(b, "x").valid());

        ConfigEntry i = new ConfigEntry("id2", "s", "m", "k2", SettingType.INTEGER, 5, 5, null, 0, 10, null, null);
        assertTrue(v.validate(i, 3).valid());
        assertFalse(v.validate(i, 99).valid());
    }
}