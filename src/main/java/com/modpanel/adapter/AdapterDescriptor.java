package com.modpanel.adapter;

import java.util.List;

public record AdapterDescriptor(
        String id,
        List<String> modIds,
        List<String> sourceTypes,
        String minVersion,
        String maxVersion
) {
}