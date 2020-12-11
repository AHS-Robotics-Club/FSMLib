package com.arcrobotics.fsm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StateMachineBuilderTests {

    enum MyEnum {
        ONE, TWO, THREE, FOUR
    }

    StateMachineBuilder<MyEnum> builder;
    private int x;

    @BeforeEach
    public void setup() {
        builder = new StateMachineBuilder<>(() -> true);
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
            .endOn(MyEnum.THREE)
            .build();

        assertTrue(builder.isRunning());
        assertEquals(0, x);
        builder.run();
        assertEquals(1, x);
        builder.run();
        assertEquals(2, x);
        builder.run();
        assertEquals(2, x);
        assertFalse(builder.isRunning());
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
                .endOn(MyEnum.FOUR)
                .build();

        assertTrue(builder.isRunning());
        assertEquals(0, x);
        builder.run();
        assertEquals(1, x);
        builder.run();
        assertEquals(2, x);
        builder.run();
        assertEquals(3, x);
        builder.run();
        assertEquals(3, x);
        builder.run();
        assertEquals(4, x);
        builder.run();
        assertEquals(4, x);
        assertFalse(builder.isRunning());
    }

    @Test
    public void testImproperBuild() {
        builder
                .startOn(MyEnum.ONE)
                .onState(MyEnum.ONE, () -> x = 1)
                .transitionOn(MyEnum.TWO, () -> x == 1)
                .transitionOn(MyEnum.THREE, () -> x == 2)
                .endOn(MyEnum.THREE);

        assertThrows(IllegalStateException.class, builder::build);
    }

}
