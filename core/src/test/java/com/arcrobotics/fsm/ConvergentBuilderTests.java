package com.arcrobotics.fsm;

import com.arcrobotics.fsm.builders.ConvergentBuilder;
import com.arcrobotics.fsm.builders.StateMachineBuilder;
import com.arcrobotics.fsm.exceptions.BuildFailureException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConvergentBuilderTests {

    enum MyEnum {
        ONE, TWO, THREE, FOUR
    }

    StateMachineBuilder<MyEnum> builder;
    StateMachine<MyEnum> fsm;
    private int x;

    @BeforeEach
    public void setup() {
        builder = new ConvergentBuilder<>(() -> true);
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
                .transitionOn(MyEnum.FOUR, () -> x == 4)
                .onState(MyEnum.FOUR, () -> x = 3)
                .transitionOn(MyEnum.THREE, () -> false)
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
    public void improperTest() {
        builder
                .startOn(MyEnum.ONE)
                .onState(MyEnum.ONE, () -> x = 1)
                .transitionOn(MyEnum.TWO, () -> x == 1)
                .onState(MyEnum.TWO, () -> x = 2)
                .transitionOn(MyEnum.THREE, () -> x == 3)
                .transitionOn(MyEnum.FOUR, () -> x == 2)
                .onState(MyEnum.FOUR, () -> x = 3)
                .transitionOn(MyEnum.ONE, () -> x == 3)
                .endOn(MyEnum.THREE);

        assertThrows(BuildFailureException.class, builder::build);
    }

}
