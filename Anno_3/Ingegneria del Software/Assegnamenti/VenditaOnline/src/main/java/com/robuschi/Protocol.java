package com.robuschi;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

import java.io.Serializable;
import java.util.List;

/**
 * Defines the communication protocol between client and server.
 * All messages are serializable for transmission over sockets.
 */
public class Protocol {

    /**
     * Message types for client-server communication.
     */
    public enum MessageType {
        // Client to Server
        AUTH_REQUEST,
        GET_PRODUCTS,
        PURCHASE_PRODUCT,
        RETURN_PRODUCT,
        ADD_NEW_PRODUCT,
        USER_LOGOUT,
        CLOSE,

        // Server to Client
        AUTH_SUCCESS,
        AUTH_FAILED,
        PRODUCT_LIST,
        PRODUCT_PURCHASED,
        RETURN_ACCEPTED,
        SERVER_CLOSE,
        ERROR
    }

    /**
     * Generic message class for client-server communication.
     */
    public static class Message implements Serializable {
        private static final long serialVersionUID = 1L;

        private final MessageType type;
        private final Object payload;
        private final String username;

        public Message(MessageType type, Object payload, String username) {
            this.type = type;
            this.payload = payload;
            this.username = username;
        }

        public Message(MessageType type, Object payload) {
            this(type, payload, null);
        }

        public Message(MessageType type) {
            this(type, null, null);
        }

        public MessageType getType() {
            return type;
        }

        public Object getPayload() {
            return payload;
        }

        public String getUsername() {
            return username;
        }
    }

    /**
     * Authentication credentials container.
     */
    public static class AuthCredentials implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String username;
        private final String password;

        public AuthCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    /**
     * Container for product list responses.
     */
    public static class ProductList implements Serializable {
        private static final long serialVersionUID = 1L;

        private final List<Product> products;

        public ProductList(List<Product> products) {
            this.products = products;
        }

        public List<Product> getProducts() {
            return products;
        }
    }

    public static class InfoDialog {
        /**
         * Displays an error and exits the application.
         *
         * @param message the error message
         */
        public static void showErrorAndExit(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            System.out.print("Connection Error");
            alert.setTitle("Connection Error");
            alert.setHeaderText("Cannot Start Application");
            alert.setContentText(message);

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(InfoDialog.class.getResource("alertStyle.css").toExternalForm());

            alert.showAndWait();
            Platform.exit();
        }

        /**
         * Displays an error alert.
         *
         * @param message the error message
         */
        public static void showError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(InfoDialog.class.getResource("alertStyle.css").toExternalForm());

            alert.showAndWait();
        }

        /**
         * Displays an information alert.
         *
         * @param message the information message
         */
        public static void showInfo(String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(message);

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(InfoDialog.class.getResource("alertStyle.css").toExternalForm());

            alert.showAndWait();
        }
    }

}
