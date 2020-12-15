package com.arcrobotics.fsm.exceptions;

public class MissingStateException extends BuildFailureException {

    public MissingStateException(String stateName) {
        super("Expected state map to contain " + stateName + " but did not. " +
                "Ensure that you have made a proper onState() binding for this state.");
    }

}
