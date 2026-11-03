package com.modpanel.persistence;

public class RestartTracker {
    private boolean restartRequired = false;

    public void markRestartRequired() { restartRequired = true; }
    public boolean isRestartRequired() { return restartRequired; }
    public void clear() { restartRequired = false; }
}