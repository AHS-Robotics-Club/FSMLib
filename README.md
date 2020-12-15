# FSM Builder Lib

A simple library to build custom finite state machines in Java 8.

## Usage

To create the builder, you first declare and instantiate it. To instantiate it, the constructor requires a `loopEvent` boolean supplier, which will supply the boolean that will continue to run the state machine.
```java
StateMachineBuilder<MyEnum> builder = new StateMachineBuilder<MyEnum>(... /* BooleanSupplier */);
```
You can chain the state building with the constructor call because each of those builder methods returns that instance of `StateMachineBuilder<T>`.

To begin building the state machine, call the `startOn()` method, which sets the initial state for the FSM.
```java
builder
  .startOn(MyEnum.INITIAL_STATE) // replace this with your initial state
  ...;
```
Then, you create an event to be run for each state in your FSM. Ensure all states are added this way. Perform an `onState()` call followed immediately by a `transitionOn()` call. `onState()` takes two parameters: the state and the action to be run. `transitionOn()` takes two parameters: the next state that comes after the state you just passed into `onState()` and the boolean that, when true, changes the current state to the next state.
```java
builder
  ...
  .onState(MyEnum.CURRENT_STATE, () -> {
    /* Runnable Event */
  })
  .transitionOn(MyEnum.NEXT_STATE, () -> {
    // return a boolean that triggers the transition
   return ...;
  })
  ...;
```
When the transition requirement is met, the state will change from `CURRENT_STATE` to `NEXT_STATE`. If desired, you can add multiple transitions to the same state (chain several `transitionOn()` bindings to the same `onState()` call).

After adding all of your states to the builder, you can add an optional `eachLoop()` action that will run some determined action at the end of every loop of the FSM. This is useful for if you want to extract hardware bytes on the same thread that runs the FSM, along with several other use cases.
```java
builder
  ...
  .eachLoop(() -> {
    /* Runnable Event */
  })
  ...;
```
You can add an `endOn()` binding which will end the FSM loop once that state is reached after at least one transition has occurred. If no `endOn()` binding exists, the loop will simply exit if the FSM transitions to a `null` state.
```java
builder
  ...
  .endOn(MyEnum.END_STATE_1, MyEnum.END_STATE_2, ...); // replace this with your end state (optional)
```
To make sure you built the FSM properly, you make the `build()` call. This outputs a `StateMachine<T>` object based on the construction of the builder.
```java
StateMachine<MyEnum> fsm = builder.build(); // will throw a BuildFailureException if built incorrectly
```
Alternatively, you can chain the construction and then output to the model.
```java
StateMachine<MyEnum> fsm
	= new StateMachineBuilder<MyEnum>(...)
		...
		.build();
```
If the transitions are incorrect, then a `MissingStateException` can be thrown, which is a type of `BuildFailureException`.

To run a single iteration of the FSM, simply call `run()` on the FSM as such:
```java
fsm.run();
```
You can put this in your own control loop or you can simply do the following if you just want to run the FSM:
```java
while (fsm.isRunning()) fsm.run();
```