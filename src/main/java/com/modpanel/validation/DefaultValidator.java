package com.modpanel.validation;

import com.modpanel.model.ConfigEntry;
import com.modpanel.model.SettingType;

import java.util.List;

public class DefaultValidator implements ValueValidator {
    @Override
    public ValidationResult validate(ConfigEntry entry, Object value) {
        if (entry == null) return ValidationResult.fail("Unknown entry");
        if (value == null) return ValidationResult.fail("Value is null");
        SettingType type = entry.getType();
        switch (type) {
            case BOOLEAN:
                if (!(value instanceof Boolean)) return ValidationResult.fail("Expected boolean");
                break;
            case INTEGER:
                if (!(value instanceof Number)) return ValidationResult.fail("Expected integer");
                if (value instanceof Double || value instanceof Float) return ValidationResult.fail("Expected integer");
                checkRange(entry, value);
                break;
            case DECIMAL:
                if (!(value instanceof Number)) return ValidationResult.fail("Expected number");
                double d = ((Number) value).doubleValue();
                if (Double.isNaN(d) || Double.isInfinite(d)) return ValidationResult.fail("Invalid number");
                checkRange(entry, value);
                break;
            case STRING:
                if (!(value instanceof String)) return ValidationResult.fail("Expected string");
                break;
            case ENUM:
                List<Object> choices = entry.getChoices();
                if (choices != null && !choices.isEmpty() && !choices.contains(value)) {
                    return ValidationResult.fail("Value not in allowed choices");
                }
                break;
            default:
                break;
        }
        Object min = entry.getMinValue();
        Object max = entry.getMaxValue();
        if (min instanceof Number && max instanceof Number && value instanceof Number) {
            double v = ((Number) value).doubleValue();
            if (v < ((Number) min).doubleValue() || v > ((Number) max).doubleValue()) {
                return ValidationResult.fail("Value out of range");
            }
        }
        return ValidationResult.ok();
    }

    private void checkRange(ConfigEntry entry, Object value) {
        // handled generically above
    }
}