package com.arcrobotics.fsm;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class StateMachine<T extends Enum<T>> {

    private final Map<T, Runnable> stateMap;
    private final Map<T, Map<BooleanSupplier, T>> transitionMap;
    private T currState;
    private final T initialState;
    private final List<T> finalStates;
    private final Runnable loopAction;
    private boolean isStarted;
    private final BooleanSupplier loopEvent;

    /**
     * Makes a state machine. Requires passing in all of these parameters,
     * so it is much easier to build a state machine using a
     * {@link com.arcrobotics.fsm.builders.StateMachineBuilder}.
     *
     * @param stateMap      the map of states to runnable actions pertaining to said states
     * @param transitions   a map of states to a map of boolean suppliers that trigger
     *                      a transition to another state
     * @param initialState  the initial state of the FSM
     * @param finalStates   the final states of the FSM (optional)
     * @param loopAction    the action to be run every loop
     * @param loopEvent     a boolean that, while true, continuously runs the FSM
     */
    public StateMachine(Map<T, Runnable> stateMap, Map<T, Map<BooleanSupplier, T>> transitions,
                        T initialState, List<T> finalStates, Runnable loopAction, BooleanSupplier loopEvent) {
        this.stateMap = stateMap;
        transitionMap = transitions;
        currState = initialState;
        this.initialState = initialState;
        this.finalStates = finalStates;
        this.loopAction = loopAction;
        this.loopEvent = loopEvent;
    }

    /**
     * Determines if the FSM is currently active and running.
     * If the current thread on which the FSM is running is interrupted,
     * then the FSM should end. If the FSM has transitioned at least once
     * and the current state is equal to the final state, then the FSM
     * has finished. If the loop event returns false at any point, the FSM also ends.
     *
     * @return if the FSM is currently running
     */
    public boolean isRunning() {
        return !Thread.currentThread().isInterrupted() &&
                loopEvent.getAsBoolean() && (!finalStates.contains(currState) || isStarted);
    }

    /**
     * @return the first state of the FSM
     */
    public T first() {
        return initialState;
    }

    /**
     * @return the final states of the FSM
     */
    public List<T> last() {
        return finalStates;
    }

    /**
     * Runs the current
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
