package com.arcrobotics.fsm;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class SequentialBuilder<T extends Enum<T>> extends StateMachineBuilder<T> {

    /**
     * Constructs an FSM builder
     * @param loopEvent a boolean supplier that represents the condition for continuing the FSM
     */
    public SequentialBuilder(BooleanSupplier loopEvent) {
        super(loopEvent);
    }

    @Override
    public SequentialBuilder<T> build() throws IllegalStateException {
        super.build();
        // checks to make sure everything is sequential
        Set<T> seen = new HashSet<>();  // our check set
        seen.add(initialState); // add initial state - cannot loop back to initial state
        for (T state : transitionMap.keySet()) {
            transitionMap.get(state).forEach((k, v) -> {
                if (seen.contains(v)) {
                    throw new IllegalStateException("State Machine is not sequential. " +
                           v.name() + " is already in the map.");
                }
                seen.add(v);
            });
        }
        return this;
    }

}
