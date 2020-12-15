package com.arcrobotics.fsm.exceptions;

public class BuildFailureException extends IllegalStateException {

    public BuildFailureException() {
        super("Failed to build state machine.");
    }

    public BuildFailureException(String message) {
        super("Failed to build state machine. " + message);
    }

}
