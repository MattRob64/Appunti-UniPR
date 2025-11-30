package com.robuschi;

/**
 * This class is used to manage all the calculator-related functions and variables,
 * the menu with the various options to choose is located in the Main class of the program
 * @author Mattia Robuschi Caprara
**/
public class CalculatorController {
    /**
     * This is the current value, the result of the various mathematical operations is stored here
    **/
    private Integer currentVal;

    /**
     * The main and only construct of this class sets the currentValue to zero using the reset() function
     * so that at the beginning of the program we have the initial value set to zero
    **/
    public CalculatorController() {
        this.reset();
    }

    /**
     * This function resets the currentValue to zero (the initial value)
    **/
    public void reset() {
        this.currentVal = 0;
    }

    /**
     * This function returns the value of the variable currentValue,
     * if used after an operation it will give you the exact result of the operation
     * @return the value of the variable currentValue
    **/
    public Integer getResult() {
        return currentVal;
    }

    /**
     * This function is used to reduce the redundancies in the code,
     * instead of printing the same message over and over in the Main class
     * this function can be called to print the message and write less code in the Main class
    **/
    public void showInputMessage() {
        System.out.print("Insert the second number for the operation(the first value is: " + "\033[0;33m" + this.currentVal + "\033[0m" + "): ");
    }

    /**
     * This function is used to compute the mathematical operation of addition.
     * It adds the value entered by the user to the currentVal variable.
     * @param value the value that gets added to currentVal to get the result of the operation
    **/
    public void addition(Integer value) {
        currentVal += value;
    }

    /**
     * This function is used to compute the mathematical operation of subtraction.
     * It subtracts the value entered by the user to the currentVal variable.
     * @param value the value that gets subtracted to currentVal to get the result of the operation
    **/
    public void subtraction(Integer value) {
        currentVal -= value;
    }

    /**
     * This function is used to compute the mathematical operation of multiplication.
     * It multiplies the value entered by the user to the currentVal variable
     * @param value the value that gets multiplied to currentVal to get the result of the operation
    **/
    public void multiplication(Integer value) {
        currentVal *= value;
    }

    /**
     * This function is used to compute the mathematical operation of division.
     * It divides the currentVal variable to the value entered by the user
     * @param value is the divider of the operation
    **/
    public void division(Integer value) {
        try {
            currentVal /= value;
        } catch (Exception e) {
            System.out.println("This calculator cannot divide by zero");
        }
    }

    /**
     * This function is used to compute the mathematical operation of modulo.
     * It calculates the value of the modulo between currentVal and the value inserted by the user (divider)
     * @param value is the divider of the operation
    **/
    public void modulo(Integer value) {
        try {
            currentVal %= value;
        } catch (Exception e) {
            System.out.println("This calculator cannot divide by zero");
        }
    }

}
