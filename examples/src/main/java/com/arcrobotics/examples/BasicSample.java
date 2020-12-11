package com.arcrobotics.examples;

import com.arcrobotics.fsm.SequentialBuilder;
import com.arcrobotics.fsm.StateMachineBuilder;

public class BasicSample {

    public enum MyEnum {
        LOW, MEDIUM, HIGH, END
    }

    private static int state = 2;
    private static int a = 0;

    public static void main(String[] args) {
        StateMachineBuilder<MyEnum> builder = new StateMachineBuilder<MyEnum>(() -> true)
                .startOn(MyEnum.LOW)
                .onState(MyEnum.LOW, () -> {
                    System.out.println("Low State");
                    if (a == 1) a = 0;
                })
                .transitionOn(MyEnum.HIGH, () -> state == 2)
                .transitionOn(MyEnum.MEDIUM, () -> state == 1)
                .onState(MyEnum.MEDIUM, () -> {
                    System.out.println("Med State");
                })
                .transitionOn(MyEnum.END, () -> a == 0)
                .transitionOn(MyEnum.LOW, () -> a == 1)
                .onState(MyEnum.HIGH, () -> {
                    System.out.println("High State");
                    state = 1;
                    a = 1;
                })
                .transitionOn(MyEnum.MEDIUM, () -> state == 1)
                .eachLoop(() -> System.out.println("Loop was run"))
                .endOn(MyEnum.END)
                .build();

        while (builder.isRunning())
            builder.run();
        System.out.println("Exited Loop");
    }

}