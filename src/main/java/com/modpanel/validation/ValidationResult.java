package com.modpanel.validation;

public record ValidationResult(boolean valid, String message) {
    public static ValidationResult ok() { return new ValidationResult(true, null); }
    public static ValidationResult fail(String msg) { return new ValidationResult(false, msg); }
}