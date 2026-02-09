package com.fortytwo.demeter.common.exception;

public class TenantMismatchException extends RuntimeException {

    public TenantMismatchException(String message) {
        super(message);
    }

    public TenantMismatchException() {
        super("Tenant mismatch: operation attempted on resource belonging to another tenant");
    }
}
