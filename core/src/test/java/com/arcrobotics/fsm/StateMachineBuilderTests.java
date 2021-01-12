package com.arcrobotics.fsm;

import com.arcrobotics.fsm.builders.StateMachineBuilder;
import com.arcrobotics.fsm.exceptions.MissingStateException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class StateMachineBuilderTests {

    enum MyEnum {
        ONE, TWO, THREE, FOUR
    }

    StateMachineBuilder<MyEnum> builder;
    StateMachine<MyEnum> fsm;
    private int x;

    @BeforeEach
    public void setup() {
        builder = new StateMachineBuilder<>(() -> true);
        x = 0;
        running = false;
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

        assertEquals(MyEnum.ONE, fsm.first());
        assertEquals(Collections.singletonList(MyEnum.THREE), fsm.last());

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

        fsm = builder.build();

        assertTrue(fsm.isRunning());
        assertEquals(0, x);
        fsm.run();
        assertEquals(1, x);
        fsm.run();
        assertEquals(2, x);
        fsm.run();
        assertEquals(3, x);
        fsm.run();
        assertEquals(3, x);
        fsm.run();
        assertEquals(4, x);
        fsm.run();
        assertEquals(4, x);
        assertFalse(fsm.isRunning());
    }

    @Test
    public void testImproperBuild() {
        builder
                .startOn(MyEnum.ONE)
                .onState(MyEnum.ONE, () -> x = 1)
                .transitionOn(MyEnum.TWO, () -> x == 1)
                .transitionOn(MyEnum.THREE, () -> x == 2)
                .endOn(MyEnum.THREE);

        assertThrows(MissingStateException.class, builder::build);
    }

    private boolean running = false;

    @Test
    public void fromIdleTest() {
        builder
                .startOn(MyEnum.ONE)
                .onState(MyEnum.ONE, () -> x = 1)
                .transitionOn(MyEnum.TWO, () -> running)
                .onState(MyEnum.TWO, () -> x = 2)
                .transitionOn(MyEnum.ONE, () -> false)
                .endOn(MyEnum.ONE);

        fsm = builder.build();
        assertEquals(0, x);
        assertTrue(fsm.isRunning());
        fsm.run();
        assertEquals(1, x);
        fsm.run();
        assertEquals(1, x);
        running = true;
        fsm.run();
        assertEquals(1, x);
        fsm.run();
        assertEquals(2, x);
        fsm.run();
        assertEquals(2, x);
    }

}
