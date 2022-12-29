package com.bbva.p25r.lib.r001.util;

public enum P25RErrors {
    KEY_NOT_FOUND("P25R00000704", "KEY_NOT_FOUND", false),
    INVALID_PARAMETER_FORMAT("P25R00000706", "INVALID_PARAMETER_FORMAT", false);


    private final String codeAdvice;
    private final String message;
    private final boolean rollback;

    P25RErrors(String codeAdvice, String message, boolean rollback) {
        this.codeAdvice = codeAdvice;
        this.message = message;
        this.rollback = rollback;
    }

    public String getCodeAdvice() {
        return codeAdvice;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRollback() {
        return rollback;
    }
}
