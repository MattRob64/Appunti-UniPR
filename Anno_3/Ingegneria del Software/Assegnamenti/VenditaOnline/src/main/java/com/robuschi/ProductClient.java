package com.robuschi;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * <p>The {@code ProductClient} class is the main client application for the online product sales system.
 * <p>Manages the JavaFX application lifecycle and view navigation.
 * @see Application
 * @author Mattia Robuschi Caprara
**/
public class ProductClient extends Application {

    private Stage primaryStage;
    private NetworkManager networkManager;
    private MainController mainController;

    private String username;

    /**
     * {@inheritDoc}
     * <p>This method starts the JavaFX application.
     * <p>It is called by the {@code init()} method which is inherited from the {@code Application} class, which sets the {@code primaryStage}.
     * <p>The method sets the title for the window, it tries to connect to the server,
     * and if the connection is successful it sets the message handler and shows the login view.
     * @param primaryStage is the primary stage of the application
     * @see NetworkManager
    **/
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.networkManager = new NetworkManager();

        primaryStage.setTitle("Product Sales System");

        // Connect to server
        if (!networkManager.connect()) {
            Protocol.InfoDialog.showErrorAndExit("Cannot connect to server!\n\n" +
                    "Please ensure the server is running first.\n" +
                    "Start the server with: java com.robuschi.ProductServer");
            return;
        }

        System.out.println("\033[0;32m" + "Connection started" + "\033[0m");

        // Set message handler
        networkManager.setMessageHandler(this::handleServerMessage);

        // Show login view
        showLoginView();

        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            logout();
        });

        primaryStage.show();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    /**
     * <p>This method shows the login view.
     * <p>It loads the {@code login.fxml} FXML file from the resources, it sets the scene and sets the style of the view.
     * <p>Gets called in the {@code start()} method.
    **/
    private void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/robuschi/login.fxml"));
            Scene scene = new Scene(loader.load(), 500, 400);
            scene.getStylesheets().add(getClass().getResource("loginStyle.css").toExternalForm());

            LoginController loginController = loader.getController();
            loginController.setApplication(this);

            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            Protocol.InfoDialog.showError("Error loading login view: " + e.getMessage());
        }
    }

    /**
     * <p>This method shows the main application view.
     * <p>It loads the {@code main.fxml} FXML file from the resources, it sets the scene, sets the username
     * visible on the main view and sets the style of the view.
     * <p>Gets called if the authentication attempt is successful.
     * @see MainController
    **/
    private void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/robuschi/main.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("mainStyle.css").toExternalForm());

            String userLabel = String.format("(" + getUsername() + ")");

            mainController = loader.getController();
            mainController.setApplication(this);
            mainController.setUserLabel(userLabel);

            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            Protocol.InfoDialog.showError("Error loading main view: " + e.getMessage());
        }
    }

    /**
     * <p>This method handles all the incoming messages from the server.
     * <ul>
     *      <li>If the received message has type {@code AUTH_SUCCESS} the method sets the username and then launches the main view using {@code showMainView()}</li>
     *      <li>If the received message has type {@code AUTH_FAILED} the method shows a dialog displaying the error "Authentication failed. Invalid credentials."</li>
     *      <li>If the received message has type {@code PRODUCT_LIST} the method launches the function {@code mainController.updateAvailableProducts(productList.getProducts())} to update the product list</li>
     *      <li>If the received message has type {@code PRODUCT_PURCHASED} the method add the purchased product into the user's product list and then sends a message of type {@code GET_PRODUCTS} to the server</li>
     *      <li>If the received message has type {@code RETURN_ACCEPTED} the method removes the purchased product from the user's product list and then sends a message of type {@code GET_PRODUCTS} to the server</li>
     *      <li>If the received message has type {@code SERVER_CLOSE} the method displays a dialog stating "Server is closing" and then launches the {@code Platform.exit()} method</li>
     *      <li>If the received message has type {@code ERROR} the method shows an error dialog that displays the given error</li>
     * </ul>
     * @param message the received message
     * @see Protocol.MessageType
     * @see MainController
     * @see NetworkManager
    **/
    private void handleServerMessage(Protocol.Message message) {
        switch (message.getType()) {
            case AUTH_SUCCESS:
                setUsername(message.getPayload().toString());
                System.out.println("\033[0;32m" + "Welcome: "+ getUsername() + "\033[0m");
                showMainView();
                break;

            case AUTH_FAILED:
                Protocol.InfoDialog.showError("Authentication failed. Invalid credentials.");
                System.err.println("Authentication failed. Invalid credentials.");
                break;

            case PRODUCT_LIST:
                if (mainController != null) {
                    Protocol.ProductList productList = (Protocol.ProductList) message.getPayload();
                    mainController.updateAvailableProducts(productList.getProducts());
                }
                System.out.println("Product list updated");
                break;

            case PRODUCT_PURCHASED:
                if (mainController != null) {
                    Product product = (Product) message.getPayload();
                    mainController.addPurchasedProduct(product);
                    Protocol.InfoDialog.showInfo("Product purchased: " + product.getName());
                    System.out.println("You purchased: " + product.getName());
                    // Request updated list
                    networkManager.sendMessage(new Protocol.Message(Protocol.MessageType.GET_PRODUCTS));
                }
                break;

            case RETURN_ACCEPTED:
                Protocol.InfoDialog.showInfo("Product returned successfully");
                System.out.println("You returned a product");
                // Request updated list
                networkManager.sendMessage(new Protocol.Message(Protocol.MessageType.GET_PRODUCTS));
                break;

            case SERVER_CLOSE:
                Protocol.InfoDialog.showInfo("Server is closing");
                System.out.println("Server is closing");
                Platform.exit();
                break;

            case ERROR:
                Protocol.InfoDialog.showError((String) message.getPayload());
                break;
        }
    }

    /**
     * <p>This method logs out and closes the application.
     * <p>It sends a message of type {@code USER_LOGOUT} to the server, prints a goodbye message and then calls the {@code Platform.exit()} method.
    **/
    public void logout() {
        networkManager.sendMessage(new Protocol.Message(Protocol.MessageType.USER_LOGOUT));
        System.out.println("\033[0;33m" + "Goodbye: "+ getUsername() + "\033[0m");
        Platform.exit(); // Calls the stop() method
    }

    /**
     * <p>This method gets the network manager instance.
     * @return the network manager
    **/
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    /**
     * {@inheritDoc}
     * <p>This method stops the application.
     * <p>It is called when the method Platform.exit() is called.
     * <p>It checks if the network manager is {@code null}, if not the method {@code networkManager.disconnect()} is called.
     * @see NetworkManager
    **/
    @Override
    public void stop() {
        if (networkManager != null) {
            networkManager.disconnect();
        }
    }

    /**
     * <p>Main method to launch the application.
     * <p>It uses the {@code launch(args)} method to launch the default constructor of the class (which in this case is implicit).
     * <p>Then, the default constructor launches the {@code init()} method which is inherited from the {@code Application} class.
     * <p>The {@code init()} method launches the {@code start()} method which then actually starts the application.
     * @param args command line arguments
    **/
    public static void main(String[] args) {
        launch(args);
    }
}