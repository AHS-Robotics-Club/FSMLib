package com.arcrobotics.fsm.builders;

import com.arcrobotics.fsm.StateMachine;
import com.arcrobotics.fsm.exceptions.BuildFailureException;

import java.util.function.BooleanSupplier;

/**
 * A LinearBuilder constructs a state machine that
 * only runs on sequence. It essentially the degenerate
 * case of a sequential state machine and runs states
 * like a linked list.
 */
public class LinearBuilder<T extends Enum<T>> extends SequentialBuilder<T> {

    /**
     * Constructs an FSM builder
     *
     * @param loopEvent a boolean supplier that represents the condition for continuing the FSM
     */
    public LinearBuilder(BooleanSupplier loopEvent) {
        super(loopEvent);
    }

    @Override
    public StateMachine<T> build() throws BuildFailureException {
        StateMachine<T> fsm = super.build();

        // ensure all states only have one transition
        for (T state : stateMap.keySet()) {
            if (transitionMap.get(state).size() > 1)
                throw new BuildFailureException("State machine is not sequential. " +
                        "Expected " + state.name() + " to only have one transition. " +
                        "Ensure all of your states only have possible transition.");
        }

        return fsm;
    }

}
