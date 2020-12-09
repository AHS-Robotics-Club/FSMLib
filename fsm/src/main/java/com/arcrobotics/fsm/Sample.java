package com.arcrobotics.fsm;

public class Sample {

    public enum MyEnum {
        LOW, MEDIUM, HIGH, END
    }

    private static int state = 0;

    public static void main(String[] args) {
        StateMachineBuilder<MyEnum> builder = new StateMachineBuilder<MyEnum>(() -> true)
                .startOn(MyEnum.LOW)
                .onState(MyEnum.LOW, () -> {
                    System.out.println("Low State");
                    state = 2;
                })
                .transitionOn(MyEnum.HIGH, () -> state == 2)
                .onState(MyEnum.MEDIUM, () -> {
                    System.out.println("Med State");
                    state = 0;
                })
                .transitionOn(MyEnum.END, () -> state == 0)
                .onState(MyEnum.HIGH, () -> {
                    System.out.println("High State");
                    state = 1;
                })
                .transitionOn(MyEnum.MEDIUM, () -> state == 1)
                .endOn(MyEnum.END);

        builder.run();
        System.out.println("Exited Loop");
    }

}