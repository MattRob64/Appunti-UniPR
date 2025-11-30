# Product Sales Manager

## Before executing:
This program was made using the IntelliJ IDEA IDE by JetBrains and was set up using Maven so if something unusual is found in the project structure I want to clarify that the project structure was created by the IDE and for convenience it was not changed.

The javadoc is located inside the javadoc folder, at the root of the project. 

## Step-by-step guide on how to use the Product Sales Manager application

### STEP 1: OPEN THE PROJECT IN THE PREFERRED IDE
1. Open your preferred IDE (e.g. Eclipse, IntelliJ, NetBeans, ecc...)
2. Open the project

### STEP 2: GET TO KNOW THE PROJECT STRUCTURE
1. The main Java files are located in /src/main/java/
2. Inside the java folder there is a package called com.robuschi, all the java files are located there (note that in the file explorer the package is usually shown as /com/robuschi)
3. The various resources (e.g. fxml files, css files, ecc...) are found inside the resources folder in the com.robuschi package at the following path: /src/main/resources/

The project is based on the MVC architecture to obtain a separation between Model, View and Controller.
###### NOTE: Only one package was created because the files are not too much and a too specific packaging method could have resulted in a more confused way to set up the project structure instead of simplifying and organizing the structure.

### STEP 3: BRIEF EXPLANATION OF THE CLASSES
### Product
This class represents a product in the "online" sales system.

It servers as the main object of the system, users can buy, return or add products.

The server keeps track of all the products.

A product has a name, price, and unique identifier.
### Protocol
This class defines the communication protocol between client and server.

All messages are serializable for transmission over sockets.

Inside of it are defined all the various message types, the message class, the authentication credential class, the product list class and the dialog window manager class.

This class also contains other classes, which are:
* **MessageType**: an ENUM that contains all the possible message types of the program
* **Message**: A generic message class for client-server communication.
* **AuthCredentials**: It serves as the authentication credentials container (username and password).
* **ProductList**: It is a container for product list responses and serves as the container for all the products that are stored in the program.
* **InfoDialog**: It is used as a manager for all the dialog windows that pop up during the execution of the program, except for the one that is used to add a product.
### LoginController
This class serves as the controller for the login view.

Handles user authentication and the relative communication with the server.
### MainController
This class serves as a controller for the main application view.

Handles product management operations and the interaction with the GUI.
### NetworkManager
This class manages network communication with the server.

Handles socket connection, message sending, and receiving.
### ProductClient
This class is the main client application for the online product sales system.

Manages the JavaFX application lifecycle and view navigation.
### ProductServer
This class is a multithreaded server for handling online product sales.

Manages user authentication, product inventory, client requests and server responses.

I has a nested class called **ClientHandler** which handles individual client connections in a separate thread, manages the socket connection for every user, the input and output streams, their authentication status and their username.

###### For a more in-depth look at the functionality of the class read the Javadoc.

### STEP 4: EXECUTION

1. Run 'ProductServer.main()'
2. Wait for: "Server started on port 5000"
3. Run 'ProductClient.main()'
4. A login window should appear
5. Login with the default users defined in the ProductServer constructor:
    - user1 / password1
    - user2 / password2
    - user3 / password3
6. If the login is successful the login window should disappear and the application should start
7. If everything is fine you should be greeted by the user interface with the username written on top

### Buy a product
1. Focus on the list on the left, that is the list of available products on the server
2. Select one of the product
3. Click on the "Purchase Selected" button
4. If everything is fine a pop-up dialog should appear saying what product you purchased

### Return a product
1. Focus on the list on the right, that is the list of the products that you already purchased
2. Select one of the product (if not empty)
3. Click on the "Return Selected" button
4. If everything is fine a pop-up dialog should appear saying that you returned a product successfully

### Add a new product
1. Click on the "Add New Product" button at the top left of the interface
2. A pop-up dialog should appear asking a product name and a product price as input
3. After filling up both of the fields you can click on the "Add" button
4. If everything is done correctly a pop-up dialog saying that the product was added should appear
5. If one of the field is not filled or the price is less than 0 an error dialog pops up

### Logout & Exit
1. Click on the "Logout and Exit" button at the top right of the interface and the program should close

###### Every action of the program is logged in the execution terminal on both server side and client side.

## TROUBLESHOOTING

### Error: "package javafx.application does not exist"
Solution: Ensure JavaFX library is added to project
- File → Project Structure → Libraries → Add JavaFX

### Error: "FXML load error"
Solution: Check that:
1. FXML files are in src/main/resources/fxml/
2. Resources folder is marked as "Resources Root"
3. Controller paths match package structure

### Error: "Cannot connect to server"
Solution: Start the server **BEFORE** starting the client

### Error: "module java.base does not 'opens java.lang' to module javafx.graphics"
Solution: Add VM options to run configuration:
--add-opens java.base/java.lang=ALL-UNNAMED