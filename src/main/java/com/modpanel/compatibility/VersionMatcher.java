package com.modpanel.compatibility;

public class VersionMatcher {
    public boolean matches(String actual, String min, String max) {
        if (actual == null) return false;
        if (min != null && actual.compareTo(min) < 0) return false;
        if (max != null && actual.compareTo(max) > 0) return false;
        return true;
    }
}