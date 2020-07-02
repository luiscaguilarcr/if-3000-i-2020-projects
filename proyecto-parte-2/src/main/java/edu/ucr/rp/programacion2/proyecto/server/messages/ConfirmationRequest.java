package edu.ucr.rp.programacion2.proyecto.server.messages;

import edu.ucr.rp.programacion2.proyecto.logic.ServiceException;

public class ConfirmationRequest {
    // Variables
    private boolean completed;
    private String details;
    // Methods

    public boolean isCompleted() {
        return completed;
    }

    public ConfirmationRequest setCompleted(boolean completed) {
        this.completed = completed;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public ConfirmationRequest setDetails(String details) {
        this.details = details;
        return this;
    }
}
