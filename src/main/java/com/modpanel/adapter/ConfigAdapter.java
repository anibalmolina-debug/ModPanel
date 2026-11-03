package com.modpanel.adapter;

import com.modpanel.model.ApplyResult;
import com.modpanel.model.ConfigEntry;
import com.modpanel.model.ConfigSnapshot;
import com.modpanel.model.ConfigSource;
import com.modpanel.model.ConfigChange;
import com.modpanel.model.DiscoveryResult;
import com.modpanel.model.ReloadRequirement;
import com.modpanel.model.ResetResult;
import com.modpanel.validation.ValidationResult;

import java.util.List;

public interface ConfigAdapter {
    default int priority() { return 0; }
    AdapterDescriptor descriptor();
    boolean supports(AdapterContext context);
    DiscoveryResult discover(AdapterContext context);
    ConfigSnapshot read(ConfigSource source);
    ValidationResult validate(ConfigEntry entry, Object value);
    ApplyResult apply(ConfigSource source, List<ConfigChange> changes);
    ResetResult resetToDefault(ConfigSource source, List<String> entryIds);
    ReloadRequirement reloadRequirement(ConfigSource source, List<ConfigChange> changes);
}