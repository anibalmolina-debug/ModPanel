package com.modpanel.model;

public record ConfigSection(
        String id,
        String displayKey,
        String parentId,
        int order
) {
}