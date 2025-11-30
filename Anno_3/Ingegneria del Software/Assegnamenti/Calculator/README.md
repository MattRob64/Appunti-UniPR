# Calculator Application

## Before executing:
This program was made using the IntelliJ IDEA IDE by JetBrains and was set up using Maven so if something unusual is found in the project structure I want to clarify that the project structure was created by the IDE and for convenience it was not changed.

The comments are written using the Javadoc syntax but the program does not actually have a Javadoc, it was only done to get some practice with the syntax.

The messages in the execution terminal are coloured for a more readable experience.

## Step-by-step guide on how to use this product server application

### STEP 1: OPEN THE PROJECT IN THE PREFERRED IDE
1. Open your preferred IDE (e.g. Eclipse, IntelliJ, NetBeans, ecc...)
2. Open the project

### STEP 2: GET TO KNOW THE PROJECT STRUCTURE
1. The main Java files are located in /src/main/java/
2. Inside the java folder there is a package called com.robuschi, all the java files are located there (note that in the file explorer the package is usually shown as /com/robuschi)

### STEP 3: BRIEF EXPLANATION OF THE CLASSES
### Main
Is the main class of the program that handles all the execution.

Manages the menu with the various options to choose from.

### CalculatorController
This class is used to manage all the calculator-related functions and variables.

### STEP 4: EXECUTION

1. Run 'Main.main()'
2. A menu should pop up in the execution terminal, it should look like this:
- 1: Addition
- 2: Subtraction
- 3: Multiplication
- 4: Division
- 5: Modulo
- 6: Reset value (=0)
- 7: End Session
3. To select an action the user simply needs to insert the number of given action
4. After selecting one of these options (1, 2, 3, 4, 5) the user will be prompted with a message inviting to insert the second number of the operation
4. This is because the calculator has a "memory" it keeps track of the number in the operations and uses it as the first operand
5. After obtaining the result, it will be memorized inside the program and will be the first operand of the next operation
6. If the user wants to start a new calculation from zero the option 6 "Reset value (=0)" can be used and the first operand will return to zero
7. If the user wants to end the calculation session the option 7 "End session" can be used to exit and terminate the program execution