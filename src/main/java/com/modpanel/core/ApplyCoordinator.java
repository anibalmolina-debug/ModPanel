package com.modpanel.core;

import com.modpanel.model.ApplyResult;
import com.modpanel.model.ConfigChange;
import com.modpanel.model.ConfigEntry;
import com.modpanel.model.ConfigSource;
import com.modpanel.model.ReloadRequirement;
import com.modpanel.adapter.ConfigAdapter;
import com.modpanel.persistence.BackupService;
import com.modpanel.validation.DefaultValidator;
import com.modpanel.validation.ValidationResult;
import com.modpanel.validation.ValueValidator;

import java.util.ArrayList;
import java.util.List;

public class ApplyCoordinator {
    private final ValueValidator validator = new DefaultValidator();
    private final BackupService backupService = new BackupService();

    public ApplyResult apply(ConfigAdapter adapter, ConfigSource source, ChangeSet changeSet, List<ConfigEntry> entries) {
        List<ConfigChange> changes = changeSet.toChanges();
        // validate
        for (ConfigChange c : changes) {
            ConfigEntry e = findEntry(entries, c.entryId());
            if (e != null) {
                ValidationResult vr = adapter.validate(e, c.proposedValue());
                if (!vr.valid()) {
                    ValidationResult v2 = validator.validate(e, c.proposedValue());
                    if (!v2.valid()) {
                        return new ApplyResult(false, "Invalid value: " + v2.message());
                    }
                }
            }
        }
        // backup
        backupService.backup(source.getSourceId());
        ApplyResult result = adapter.apply(source, changes);
        if (result.success()) {
            changeSet.clear();
        }
        return result;
    }

    public ReloadRequirement reloadRequirement(ConfigAdapter adapter, ConfigSource source, List<ConfigChange> changes) {
        return adapter.reloadRequirement(source, changes);
    }

    private ConfigEntry findEntry(List<ConfigEntry> entries, String id) {
        for (ConfigEntry e : entries) if (e.getId().equals(id)) return e;
        return null;
    }
}