package com.virtusa.procurement_service.exceptions;

public class PurchaseRequestNotFoundException extends RuntimeException {

    public PurchaseRequestNotFoundException(String message) {
        super(message);
    }
}