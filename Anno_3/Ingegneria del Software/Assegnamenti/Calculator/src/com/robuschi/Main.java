package com.robuschi;
import java.util.Scanner;

/**
 * Main class in which the program executes all of its main functions
 * @author Mattia Robuschi Caprara
**/
public class Main {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        int menuChoice;
        CalculatorController calculatorController = new CalculatorController();

        do {
            System.out.println("Welcome to a simple integer calculator, you have the following options:");
            System.out.println("- 1: Addition");
            System.out.println("- 2: Subtraction");
            System.out.println("- 3: Multiplication");
            System.out.println("- 4: Division");
            System.out.println("- 5: Modulo");
            System.out.println("- 6: Reset value (=0)");
            System.out.println("- 7: End Session");
            System.out.print(">> ");
            menuChoice = userInput.nextInt();
            switch (menuChoice) {
                case 1:
                    calculatorController.showInputMessage();
                    calculatorController.addition(userInput.nextInt());
                    break;
                case 2:
                    calculatorController.showInputMessage();
                    calculatorController.subtraction(userInput.nextInt());
                    break;
                case 3:
                    calculatorController.showInputMessage();
                    calculatorController.multiplication(userInput.nextInt());
                    break;
                case 4:
                    calculatorController.showInputMessage();
                    calculatorController.division(userInput.nextInt());
                    break;
                case 5:
                    calculatorController.showInputMessage();
                    calculatorController.modulo(userInput.nextInt());
                    break;
                case 6:
                    calculatorController.reset();
                    System.out.println("\033[0;31m" + "The value of the calculator has been reset to 0" + "\033[0m");
                    continue;
                case 7:
                    System.out.println("\033[0;32m" + "Goodbye" + "\033[0m");
                    continue;
                default:
                    System.out.println("\033[0;31m" + "Invalid input!!!" + "\033[0m");
                    continue;
            }
            System.out.println("\033[0;32m" + "The result is: " + calculatorController.getResult() + "\033[0m");
        } while (menuChoice != 7);
    }
}