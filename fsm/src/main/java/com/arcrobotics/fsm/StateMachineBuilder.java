package com.arcrobotics.fsm;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class StateMachineBuilder<T extends Enum<T>> {

    private final Map<T, Runnable> stateMap = new HashMap<>();
    private T currentStateInConfig, initialState, finalState;
    private final Map<T, Map<BooleanSupplier, T>> transitionMap = new HashMap<>();
    private final BooleanSupplier loopEvent;

    public StateMachineBuilder(BooleanSupplier loopEvent) {
        this.loopEvent = loopEvent;
    }

    public StateMachineBuilder<T> startOn(T state) {
        initialState = state;
        return this;
    }

    public StateMachineBuilder<T> onState(T state, Runnable action) {
        stateMap.put(state, action);
        currentStateInConfig = state;
        return this;
    }

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

    public StateMachineBuilder<T> endOn(T state) {
        finalState = state;
        return this;
    }

    public void run() {
        final T[] currState = (T[]) new Enum[]{initialState};
        final boolean[] isStarted = {true};
        while (loopEvent.getAsBoolean() && (currState[0] != finalState || isStarted[0])) {
            stateMap.get(currState[0]).run();
            transitionMap.get(currState[0]).forEach((k, v) -> {
                if (k.getAsBoolean()) {
                    currState[0] = v;
                    isStarted[0] = false;
                }
            });
        }
    }

}
