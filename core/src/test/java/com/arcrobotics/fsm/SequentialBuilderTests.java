package com.arcrobotics.fsm;

import com.arcrobotics.fsm.builders.SequentialBuilder;
import com.arcrobotics.fsm.builders.StateMachineBuilder;
import com.arcrobotics.fsm.exceptions.BuildFailureException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SequentialBuilderTests {

    enum MyEnum {
        ONE, TWO, THREE, FOUR
    }

    StateMachineBuilder<MyEnum> builder;
    StateMachine<MyEnum> fsm;
    private int x;

    @BeforeEach
    public void setup() {
        builder = new SequentialBuilder<>(() -> true);
        x = 0;
    }

    @Test
    public void basicTest() {
        builder
                .startOn(MyEnum.ONE)
                .onState(MyEnum.ONE, () -> x = 1)
                .transitionOn(MyEnum.TWO, () -> x == 1)
                .onState(MyEnum.TWO, () -> x = 2)
                .transitionOn(MyEnum.THREE, () -> x == 2)
                .endOn(MyEnum.THREE);

        fsm = builder.build();

        assertTrue(fsm.isRunning());
        assertEquals(0, x);
        fsm.run();
        assertEquals(1, x);
        fsm.run();
        assertEquals(2, x);
        fsm.run();
        assertEquals(2, x);
        assertFalse(fsm.isRunning());
    }

    @Test
    public void nonSequentialTest() {
        builder
                .startOn(MyEnum.ONE)
                .onState(MyEnum.ONE, () -> {
                    if (x == 0) x = 1;
                    else x = 4;
                })
                .transitionOn(MyEnum.FOUR, () -> x == 4)
                .transitionOn(MyEnum.THREE, () -> x == 1)
                .onState(MyEnum.TWO, () -> x = 3)
                .transitionOn(MyEnum.THREE, () -> x == 3)
                .onState(MyEnum.THREE, () -> {
                    if (x == 1) x = 2;
                })
                .transitionOn(MyEnum.TWO, () -> x == 2)
                .transitionOn(MyEnum.ONE, () -> x == 3)
                .endOn(MyEnum.FOUR);

        assertThrows(BuildFailureException.class, builder::build);
    }

    @Test
    public void sequentialTest() {
        builder
                .startOn(MyEnum.ONE)
                .onState(MyEnum.ONE, () -> x = 1)
                .transitionOn(MyEnum.TWO, () -> x == 1)
                .onState(MyEnum.TWO, () -> x = 2)
                .transitionOn(MyEnum.THREE, () -> x == 2)
                .transitionOn(MyEnum.FOUR, () -> x == 4)
                .endOn(MyEnum.THREE, MyEnum.FOUR);

        fsm = builder.build();

        assert true;
    }

}
