package com.modpanel.validation;

import com.modpanel.model.ConfigEntry;

public interface ValueValidator {
    ValidationResult validate(ConfigEntry entry, Object value);
}