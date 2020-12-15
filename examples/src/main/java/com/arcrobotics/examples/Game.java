package com.arcrobotics.examples;

import com.arcrobotics.fsm.StateMachine;
import com.arcrobotics.fsm.builders.StateMachineBuilder;
import java.util.Scanner;

/**
 * A simple math game!
 */
public class Game {

    public enum GameState {
        START, PLAY, LOAD,
        ADD, MULT, SUB,
        WRONG, RIGHT
    }

    // Game Instance Objects
    private static int operation = 0;
    private static int firstNumber = 0;
    private static int secondNumber = 0;
    private static int correctAnswer = 0;

    // Load
    public static void load() {
        operation = (int)(Math.random() * 3);
        firstNumber = (int)(Math.random() * 201 - 100);
        secondNumber = (int)(Math.random() * 201 - 100);
        switch (operation) {
            case 0:
                correctAnswer = firstNumber + secondNumber;
                break;
            case 1:
                correctAnswer = firstNumber - secondNumber;
                break;
            case 2:
                correctAnswer = firstNumber * secondNumber;
        }
    }

    // Gameplay Methods
    public static void add() {
        System.out.print("What is " + firstNumber + " + " + secondNumber + "? ");
        userAnswer = scanner.nextInt();
        System.out.println();
    }

    public static void sub() {
        System.out.print("What is " + firstNumber + " - " + secondNumber + "? ");
        userAnswer = scanner.nextInt();
        System.out.println();
    }

    public static void mult() {
        System.out.print("What is " + firstNumber + " * " + secondNumber + "? ");
        userAnswer = scanner.nextInt();
        System.out.println();
    }

    private static Scanner scanner = new Scanner(System.in);
    private static String userInput = "p";
    private static int userAnswer = 0;

    public static void main(String[] args) {
        StateMachine<GameState> game = new StateMachineBuilder<GameState>(
                () -> !userInput.equalsIgnoreCase("Q"))
                .startOn(GameState.START)

                .onState(GameState.START, () -> {
                    System.out.print("Enter 'S' to start the game: ");
                    userInput = scanner.next();
                    System.out.println();
                })
                .transitionOn(GameState.PLAY, () -> userInput.equalsIgnoreCase("S"))

                .onState(GameState.PLAY, () -> {
                    System.out.print("Would you like to play a round? [Y/Q] ");
                    userInput = scanner.next();
                    System.out.println();
                })
                .transitionOn(GameState.LOAD, () -> userInput.equalsIgnoreCase("Y"))

                .onState(GameState.LOAD, Game::load)
                .transitionOn(GameState.ADD, () -> operation == 0)
                .transitionOn(GameState.SUB, () -> operation == 1)
                .transitionOn(GameState.MULT, () -> operation == 2)

                .onState(GameState.ADD, Game::add)
                .transitionOn(GameState.WRONG, () -> userAnswer != correctAnswer)
                .transitionOn(GameState.RIGHT, () -> userAnswer == correctAnswer)

                .onState(GameState.SUB, Game::sub)
                .transitionOn(GameState.WRONG, () -> userAnswer != correctAnswer)
                .transitionOn(GameState.RIGHT, () -> userAnswer == correctAnswer)

                .onState(GameState.MULT, Game::mult)
                .transitionOn(GameState.WRONG, () -> userAnswer != correctAnswer)
                .transitionOn(GameState.RIGHT, () -> userAnswer == correctAnswer)

                .onState(GameState.RIGHT, () -> System.out.println("Correct!"))
                .transitionOn(GameState.PLAY, () -> true)

                .onState(GameState.WRONG, () -> {
                    System.out.println("Incorrect, the answer was " + correctAnswer);
                })
                .transitionOn(GameState.PLAY, () -> true)

                .build();

        while (game.isRunning())
            game.run();
        System.out.println("Game over.");
    }

}
