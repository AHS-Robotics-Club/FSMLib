package com.arcrobotics.fsm.builders;

import com.arcrobotics.fsm.StateMachine;
import com.arcrobotics.fsm.exceptions.BuildFailureException;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.function.BooleanSupplier;

/**
 * A ConvergentBuilder builds a non-looping state machine. That is,
 * no state returns to a previously visited state.
 */
public class ConvergentBuilder<T extends Enum<T>> extends StateMachineBuilder<T> {

    /**
     * Constructs an FSM builder
     *
     * @param loopEvent a boolean supplier that represents the condition for continuing the FSM
     */
    public ConvergentBuilder(BooleanSupplier loopEvent) {
        super(loopEvent);
    }

    @Override
    public StateMachine<T> build() throws BuildFailureException {
        StateMachine<T> fsm = super.build();

        // ensures no looping
        Set<T> visited = new HashSet<>();
        Stack<T> stack = new Stack<>();
        stack.push(initialState);
        while (!stack.isEmpty()) {
            T state = stack.pop();
            if (finalStates.contains(state)) continue;  // doesn't count as looping since it ends
            visited.add(state);
            transitionMap.get(state).forEach((k, v) -> {
                if (visited.contains(v)) {
                    throw new BuildFailureException("The state machine is not convergent. " +
                            "There is a loop from " + state.name() + " to " + v.name() + ".");
                }
                stack.push(v);
            });
        }

        return fsm;
    }

}
