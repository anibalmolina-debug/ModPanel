package com.modpanel.model;

public record ReloadRequirement(boolean restartRequired, String message) {
    public static ReloadRequirement none() { return new ReloadRequirement(false, "No restart required"); }
}