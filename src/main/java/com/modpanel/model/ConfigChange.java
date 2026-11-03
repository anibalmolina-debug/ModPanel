package com.modpanel.model;

public record ConfigChange(String entryId, Object originalValue, Object proposedValue) {
}