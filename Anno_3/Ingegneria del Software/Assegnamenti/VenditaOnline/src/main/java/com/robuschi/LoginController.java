package com.robuschi;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the login view.
 * Handles user authentication.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private ProductClient application;

    /**
     * Sets the main application reference.
     *
     * @param application the main application
     */
    public void setApplication(ProductClient application) {
        this.application = application;
    }

    /**
     * Handles login button action.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        // Send authentication request
        Protocol.AuthCredentials credentials = new Protocol.AuthCredentials(username, password);
        Protocol.Message message = new Protocol.Message(Protocol.MessageType.AUTH_REQUEST, credentials);
        application.getNetworkManager().sendMessage(message);
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
     * Initializes the controller.
     * Called automatically by JavaFX.
     */
    @FXML
    private void initialize() {
        // Set enter key to trigger login
        passwordField.setOnAction(e -> handleLogin());
    }
}