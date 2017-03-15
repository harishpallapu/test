package com.resolvat.model;

/**
 * Created by korteke on 11/03/17.
 */

public class HealthResponse {

    private final static String SERVICE_DOWN = "DOWN";
    private final static String SERVICE_UP = "UP";
    private String statusMessage;
    private boolean status;
    private static final String template = "Service is, %s";

    public HealthResponse(boolean repoStatus) {
        this.status = repoStatus;

        if (repoStatus) {
            this.statusMessage = SERVICE_UP;
        } else
            this.statusMessage = SERVICE_DOWN;

    }

    public String getStatusMessage() {
        return String.format(template, this.statusMessage);
    }

    public boolean getStatus() {
        return status;
    }
}
