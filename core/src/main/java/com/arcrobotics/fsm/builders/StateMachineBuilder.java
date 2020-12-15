package com.arcrobotics.fsm.builders;

import com.arcrobotics.fsm.StateMachine;
import com.arcrobotics.fsm.exceptions.BuildFailureException;
import com.arcrobotics.fsm.exceptions.MissingStateException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class StateMachineBuilder<T extends Enum<T>> {

    protected final Map<T, Runnable> stateMap = new HashMap<>();
    private T currentStateInConfig;
    private List<T> finalStates;
    protected T initialState;
    protected final Map<T, Map<BooleanSupplier, T>> transitionMap = new HashMap<>();
    private final BooleanSupplier loopEvent;
    private Runnable loopAction = () -> {};

    /**
     * Constructs an FSM builder
     *
     * @param loopEvent a boolean supplier that represents the condition for continuing the FSM
     */
    public StateMachineBuilder(BooleanSupplier loopEvent) {
        this.loopEvent = loopEvent;
    }

    /**
     * Initializes the starting state for the FSM
     *
     * @param state the starting state
     */
    public StateMachineBuilder<T> startOn(T state) {
        initialState = state;
        return this;
    }

    /**
     * Sets up a basic event to be run at a current state
     *
     * @param state  the state
     * @param action the event to be run
     */
    public StateMachineBuilder<T> onState(T state, Runnable action) {
        stateMap.put(state, action);
        currentStateInConfig = state;
        return this;
    }

    /**
     * Creates a transition binding from the current state to a next state
     *
     * @param nextState         the next state in the FSM
     * @param transitionEvent   the event that triggers the transition
     */
    public StateMachineBuilder<T> transitionOn(T nextState, BooleanSupplier transitionEvent) {
        if (!transitionMap.containsKey(currentStateInConfig)) {
            transitionMap.put(currentStateInConfig,
              new HashMap<BooleanSupplier, T>() {{
                  put(transitionEvent, nextState);
              }}
            );
        } else {
            transitionMap.get(currentStateInConfig).put(transitionEvent, nextState);
        }
        return this;
    }

    /**
     * Sets up a protocol that is run each iteration of the FSM
     *
     * @param action the protocol to be run
     */
    public StateMachineBuilder<T> eachLoop(Runnable action) {
        loopAction = action;
        return this;
    }

    /**
     * Sets up an optional binding for an end state
     * or several end states.
     *
     * @param states the states on which to end the FSM
     */
    @SafeVarargs
    public final StateMachineBuilder<T> endOn(T... states) {
        finalStates = Arrays.asList(states);
        return this;
    }

    /**
     * Ensures the builder was formed properly and all bindings are possible
     *
     * @throws IllegalStateException    if the builder was created improperly
     */
    public StateMachine<T> build() throws BuildFailureException {
        // checks to make sure all values in transition map are in state map
        for (T state : stateMap.keySet()) {
            transitionMap.get(state).forEach((k, v) -> {
                if (!stateMap.containsKey(v) && !finalStates.contains(v)) {
                    throw new MissingStateException(v.name());
                }
            });
        }
        return new StateMachine<>(
                stateMap, transitionMap, initialState,
                finalStates, loopAction, loopEvent
        );
    }

}
