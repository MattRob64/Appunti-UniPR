package com.robuschi;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * <p>The {@code LoginController} class serves as the controller for the login view.
 * <p>Handles user authentication and the relative communication with the server.
**/
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private ProductClient application;

    /**
     * <p>Sets the main application reference.
     * <p>It is called in the {@code ProductClient} class when the login view is shown through the {@code loginController.setApplication(this)} method.
     * @param application the main application
    **/
    public void setApplication(ProductClient application) {
        this.application = application;
    }

    /**
     * <p>Handles login button action.
     * <p>Gets the strings from the username and passwords fields and checks if the fields are empty.
     * <p>If everything is fine it sends a message fo type {@code AUTH_REQUEST} with username and password inside the payload to the server.
     * @see Protocol.AuthCredentials
     * @see Protocol.Message
    **/
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Protocol.InfoDialog.showError("Please enter both username and password");
            return;
        }

        // Send authentication request
        Protocol.AuthCredentials credentials = new Protocol.AuthCredentials(username, password);
        Protocol.Message message = new Protocol.Message(Protocol.MessageType.AUTH_REQUEST, credentials);
        application.getNetworkManager().sendMessage(message);
    }

    /**
     * <p>Initializes the controller.
     * <p>Called automatically by JavaFX.
    **/
    @FXML
    private void initialize() {
        // Set enter key to trigger login
        passwordField.setOnAction(e -> handleLogin());
    }
}