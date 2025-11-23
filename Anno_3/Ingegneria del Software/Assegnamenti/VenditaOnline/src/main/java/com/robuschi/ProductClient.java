package com.robuschi;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main client application for the online product sales system.
 * Manages the JavaFX application lifecycle and view navigation.
 */
public class ProductClient extends Application {

    private Stage primaryStage;
    private NetworkManager networkManager;
    private MainController mainController;

    private String username;

    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage the primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.networkManager = new NetworkManager();

        primaryStage.setTitle("Product Sales System");

        // Connect to server
        if (!networkManager.connect()) {
            Protocol.InfoDialog.showErrorAndExit("Cannot connect to server!\n\n" +
                    "Please ensure the server is running first.\n" +
                    "Start the server with: java com.productsales.server.ProductServer");
            return;
        }

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
     * Shows the login view.
     */
    private void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/robuschi/login.fxml"));
            Scene scene = new Scene(loader.load(), 500, 400);
            scene.getStylesheets().add(getClass().getResource("loginStyle.css").toExternalForm());

            LoginController controller = loader.getController();
            controller.setApplication(this);

            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            Protocol.InfoDialog.showError("Error loading login view: " + e.getMessage());
        }
    }

    /**
     * Shows the main application view.
     */
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
     * Handles incoming messages from the server.
     *
     * @param message the received message
     */
    private void handleServerMessage(Protocol.Message message) {
        switch (message.getType()) {
            case AUTH_SUCCESS:
                setUsername(message.getPayload().toString());
                showMainView();
                break;

            case AUTH_FAILED:
                Protocol.InfoDialog.showError("Authentication failed. Invalid credentials.");
                break;

            case PRODUCT_LIST:
                if (mainController != null) {
                    Protocol.ProductList productList = (Protocol.ProductList) message.getPayload();
                    mainController.updateAvailableProducts(productList.getProducts());
                }
                break;

            case PRODUCT_PURCHASED:
                if (mainController != null) {
                    Product product = (Product) message.getPayload();
                    mainController.addPurchasedProduct(product);
                    Protocol.InfoDialog.showInfo("Product purchased: " + product.getName());
                    // Request updated list
                    networkManager.sendMessage(new Protocol.Message(Protocol.MessageType.GET_PRODUCTS));
                }
                break;

            case RETURN_ACCEPTED:
                Protocol.InfoDialog.showInfo("Product returned successfully");
                // Request updated list
                networkManager.sendMessage(new Protocol.Message(Protocol.MessageType.GET_PRODUCTS));
                break;

            case SERVER_CLOSE:
                Protocol.InfoDialog.showInfo("Server is closing");
                Platform.exit();
                break;

            case ERROR:
                Protocol.InfoDialog.showError((String) message.getPayload());
                break;
        }
    }

    /**
     * Logs out and closes the application.
     */
    public void logout() {
        networkManager.sendMessage(new Protocol.Message(Protocol.MessageType.USER_LOGOUT, getUsername()));
        Platform.exit(); // Calls the stop() method
    }

    /**
     * Gets the network manager instance.
     *
     * @return the network manager
     */
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    /**
     * Stops the application.
     * Is called if the method Platform.exit() is called
     */
    @Override
    public void stop() {
        if (networkManager != null) {
            networkManager.disconnect();
        }
    }

    /**
     * Main method to launch the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}