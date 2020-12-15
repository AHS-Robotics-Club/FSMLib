package com.arcrobotics.fsm;

import com.arcrobotics.fsm.builders.LinearBuilder;
import com.arcrobotics.fsm.builders.SequentialBuilder;
import com.arcrobotics.fsm.exceptions.BuildFailureException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LinearBuilderTests {

    enum MyEnum {
        ONE, TWO, THREE, FOUR
    }

    SequentialBuilder<MyEnum> builder;
    StateMachine<MyEnum> fsm;
    private int x;

    @BeforeEach
    public void setup() {
        builder = new LinearBuilder<>(() -> true);
        x = 0;
    }

    @Test
    public void nonLinearTest() {
        builder
                .startOn(MyEnum.ONE)
                .onState(MyEnum.ONE, () -> x = 1)
                .transitionOn(MyEnum.TWO, () -> x == 1)
                .onState(MyEnum.TWO, () -> x = 2)
                .transitionOn(MyEnum.THREE, () -> x == 2)
                .transitionOn(MyEnum.FOUR, () -> x == 4)
                .endOn(MyEnum.THREE, MyEnum.FOUR);

        assertThrows(BuildFailureException.class, builder::build);
    }

}
