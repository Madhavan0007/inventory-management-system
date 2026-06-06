package com.virtusa.procurement_service.exceptions;

public class RequestAlreadyProcessedException extends RuntimeException {

    public RequestAlreadyProcessedException(String message) {
        super(message);
    }
}