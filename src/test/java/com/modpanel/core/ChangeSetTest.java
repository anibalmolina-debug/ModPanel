package com.modpanel.core;

import com.modpanel.model.ConfigEntry;
import com.modpanel.model.SettingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangeSetTest {
    @Test
    void stagesAndClears() {
        ConfigEntry e = new ConfigEntry("id", "src", "mod", "key", SettingType.BOOLEAN, false, true, null, null, null, null, null);
        ChangeSet cs = new ChangeSet("src");
        assertFalse(cs.isDirty());
        cs.stage(e, true);
        assertTrue(cs.isDirty());
        assertEquals(1, cs.toChanges().size());
        cs.clear();
        assertFalse(cs.isDirty());
        assertEquals(0, cs.toChanges().size());
    }
}