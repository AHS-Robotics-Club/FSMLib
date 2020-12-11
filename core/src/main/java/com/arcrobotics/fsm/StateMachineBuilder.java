package com.arcrobotics.fsm;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class StateMachineBuilder<T extends Enum<T>> {

    protected final Map<T, Runnable> stateMap = new HashMap<>();
    private T currentStateInConfig, currState, finalState;
    protected T initialState;
    protected final Map<T, Map<BooleanSupplier, T>> transitionMap = new HashMap<>();
    private final BooleanSupplier loopEvent;
    private boolean isStarted = true;
    private Runnable loopAction = () -> {};

    /**
     * Constructs an FSM builder
     * @param loopEvent a boolean supplier that represents the condition for continuing the FSM
     */
    public StateMachineBuilder(BooleanSupplier loopEvent) {
        this.loopEvent = loopEvent;
    }

    /**
     * Initializes the starting state for the FSM
     * @param state the starting state
     */
    public StateMachineBuilder<T> startOn(T state) {
        initialState = state;
        return this;
    }

    /**
     * Sets up a basic event to be run at a current state
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
     * Sets up a protocol that is run each iteration of the
     * @param action the protocol to be run
     */
    public StateMachineBuilder<T> eachLoop(Runnable action) {
        loopAction = action;
        return this;
    }

    /**
     * Sets up an optional binding for an end state
     * @param state the state on which to end the FSM
     */
    public StateMachineBuilder<T> endOn(T state) {
        finalState = state;
        return this;
    }

    /**
     * Ensures the builder was formed properly and all bindings are possible
     * @throws IllegalStateException    if the builder was created improperly
     */
    public StateMachineBuilder<T> build() throws IllegalStateException {
        currState = initialState;
        // checks to make sure all values in transition map are in state map
        for (T state : stateMap.keySet()) {
            transitionMap.get(state).forEach((k, v) -> {
                if (!stateMap.containsKey(v) && v != finalState) {
                    throw new IllegalStateException(
                            "Builder was not created properly. Expected state map to contain "
                            + v.name() + " but did not."
                    );
                }
            });
        }
        return this;
    }

    /**
     * @return if the FSM is currently running
     */
    public boolean isRunning() {
        return loopEvent.getAsBoolean() && (currState != finalState || isStarted);
    }

    /**
     * Runs a single iteration of the FSM
     */
    public void run() {
        if (isRunning()) {
            stateMap.get(currState).run();
            transitionMap.get(currState).forEach((k, v) -> {
                if (k.getAsBoolean()) {
                    currState = v;
                    isStarted = false;
                }
            });
            loopAction.run();
        }
    }

}
