package com.arcrobotics.fsm.builders;

import com.arcrobotics.fsm.StateMachine;
import com.arcrobotics.fsm.exceptions.BuildFailureException;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * A SequentialBuilder builds a state machine that runs states like
 * a tree. States can branch off and the tree has multiple leaves,
 * but the tree's branches cannot intersect. The reason
 * this is called sequential is because it prevents any looping behavior.
 */
public class SequentialBuilder<T extends Enum<T>> extends StateMachineBuilder<T> {

    /**
     * Constructs an FSM builder
     *
     * @param loopEvent a boolean supplier that represents the condition for continuing the FSM
     */
    public SequentialBuilder(BooleanSupplier loopEvent) {
        super(loopEvent);
    }

    @Override
    public StateMachine<T> build() throws BuildFailureException {
        StateMachine<T> fsm = super.build();

        // checks to make sure everything is sequential
        Set<T> seen = new HashSet<>();  // our check set
        seen.add(initialState); // add initial state - cannot loop back to initial state
        for (T state : transitionMap.keySet()) {
            transitionMap.get(state).forEach((k, v) -> {
                if (seen.contains(v)) {
                    throw new BuildFailureException("The state machine is not sequential. " +
                           v.name() + " is already in the map.");
                }
                seen.add(v);
            });
        }

        return fsm;
    }

}
