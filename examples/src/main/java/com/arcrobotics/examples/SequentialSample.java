package com.arcrobotics.examples;

import com.arcrobotics.fsm.StateMachine;
import com.arcrobotics.fsm.builders.SequentialBuilder;
import com.arcrobotics.fsm.builders.StateMachineBuilder;

public class SequentialSample {

    public enum MyEnum {
        LOW, MEDIUM, HIGH, END
    }

    private static int state = 0;

    public static void main(String[] args) {
        StateMachine<MyEnum> fsm = new SequentialBuilder<MyEnum>(() -> true)
                .startOn(MyEnum.LOW)
                .onState(MyEnum.LOW, () -> {
                    System.out.println("Low State");
                    state = 1;
                })
                .transitionOn(MyEnum.MEDIUM, () -> state == 1)
                .onState(MyEnum.MEDIUM, () -> {
                    System.out.println("Med State");
                    state = 2;
                })
                .transitionOn(MyEnum.HIGH, () -> state == 2)
                .onState(MyEnum.HIGH, () -> {
                    System.out.println("High State");
                    state = 0;
                })
                .transitionOn(MyEnum.END, () -> true)
                .endOn(MyEnum.END)
                .build();

        while (fsm.isRunning())
            fsm.run();
        System.out.println("Exited Loop");
    }

}