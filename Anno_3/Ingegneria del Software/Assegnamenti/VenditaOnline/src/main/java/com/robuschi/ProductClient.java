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
            showErrorAndExit("Cannot connect to server!\n\n" +
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
            showError("Error loading login view: " + e.getMessage());
        }
    }

    /**
     * Shows the main application view.
     */
    private void showMainView(String usrLbl) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/robuschi/main.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("mainStyle.css").toExternalForm());

            String userLabel = String.format("(" + usrLbl + ")");

            mainController = loader.getController();
            mainController.setApplication(this);
            mainController.setUserLabel(userLabel);

            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading main view: " + e.getMessage());
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
                //System.out.print("!!!User: " + message.getPayload() + "!!!");
                showMainView(message.getPayload().toString());
                break;

            case AUTH_FAILED:
                showError("Authentication failed. Invalid credentials.");
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
                    showInfo("Product purchased: " + product.getName());
                    // Request updated list
                    networkManager.sendMessage(new Protocol.Message(Protocol.MessageType.GET_PRODUCTS));
                }
                break;

            case RETURN_ACCEPTED:
                showInfo("Product returned successfully");
                // Request updated list
                networkManager.sendMessage(new Protocol.Message(Protocol.MessageType.GET_PRODUCTS));
                break;

            case SERVER_CLOSE:
                showInfo("Server is closing");
                Platform.exit();
                break;

            case ERROR:
                showError((String) message.getPayload());
                break;
        }
    }

    /**
     * Logs out and closes the application.
     */
    public void logout() {
        networkManager.disconnect();
        Platform.exit();
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
     * Displays an error alert.
     *
     * @param message the error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an information alert.
     *
     * @param message the information message
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an error and exits the application.
     *
     * @param message the error message
     */
    private void showErrorAndExit(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        System.out.print("Connection Error");
        alert.setTitle("Connection Error");
        alert.setHeaderText("Cannot Start Application");
        alert.setContentText(message);
        alert.showAndWait();
        Platform.exit();
    }

    /**
     * Stops the application.
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