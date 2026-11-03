package com.modpanel.compatibility;

import java.util.ArrayList;
import java.util.List;

public class CompatibilityReport {
    private final List<String> issues = new ArrayList<>();

    public void addIssue(String issue) { issues.add(issue); }
    public List<String> getIssues() { return issues; }
    public boolean isCompatible() { return issues.isEmpty(); }
}