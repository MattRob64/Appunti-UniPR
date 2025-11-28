package com.robuschi;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * The {@code Protocol} class defines the communication protocol between client and server.
 * All messages are serializable for transmission over sockets.
 * Inside of it are defined all the various message types, the message class,
 * the authentication credential class, the product list class and the dialog window manager class
 * @see MessageType
 * @see Message
 * @see AuthCredentials
 * @see ProductList
 * @see InfoDialog
 * @author Mattia Robuschi Caprara
 */
public class Protocol {

    /**
     * Message types for client-server communication.
     */
    public enum MessageType {
        /*
        Types of messages from Client to Server
        */
        /**
         * Identifies an authentication request from the login page
         **/
        AUTH_REQUEST,
        /**
         * Identifies a message that is used when the javafx lists need to be updated
         **/
        GET_PRODUCTS,
        /**
         * Identifies a purchase request from the user
         **/
        PURCHASE_PRODUCT,
        /**
         * Identifies a return request from the user
         **/
        RETURN_PRODUCT,
        /**
         * Identifies that the user wants to add a new product
         **/
        ADD_NEW_PRODUCT,
        /**
         * Identifies that the user has logged out
         **/
        USER_LOGOUT,
        /**
         * Identifies a specific message that is sent by the client when the connection needs to be closed
         **/
        CLOSE,

        /*
        Types of messages from Server to Client
        */
        /**
         * Identifies that the login attempt by the user was successful
         **/
        AUTH_SUCCESS,
        /**
         * Identifies that the login attempt by the user was not successful
         **/
        AUTH_FAILED,
        /**
         * Identifies that the list update request was successful
         **/
        PRODUCT_LIST,
        /**
         * Identifies that the purchase attempt by the user was successful
         **/
        PRODUCT_PURCHASED,
        /**
         * Identifies that the return attempt by the user was successful
         **/
        RETURN_ACCEPTED,
        /**
         * Identifies the response that the server gives to the client when the connection is closing
         **/
        SERVER_CLOSE,
        /**
         * Identifies an error that usually occurs if the lists are not up to date
         **/
        ERROR
    }

    /**
     * The {@code Message} class is a generic message class for client-server communication.
     * It is defined by a serial ID, a message type and a payload
     * @see Serializable
     * @see MessageType
     */
    public static class Message implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private final MessageType type;
        private final Object payload;

        /**
         * Creates a new {@code Message} instance.
         *
         * @param type defines the type fo the message
         * @param payload serves as the "body" or "content" of the message
         */
        public Message(MessageType type, Object payload) {
            this.type = type;
            this.payload = payload;
        }

        /**
         * Creates a new {@code Message} instance with a blank payload.
         *
         * @param type defines the type fo the message
         */
        public Message(MessageType type) {
            this(type, null);
        }

        public MessageType getType() {
            return type;
        }

        public Object getPayload() {
            return payload;
        }

    }

    /**
     * The {@code AuthCredential} class serves as the authentication credentials container.
     * It is defined by a serial ID, a username and a password
     * @see Serializable
     */
    public static class AuthCredentials implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private final String username;
        private final String password;

        /**
         * Creates a new {@code AuthCredential} instance.
         *
         * @param username defines the user's username
         * @param password defines the user's password
         */
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
     * The {@code ProductList} class is a container for product list responses.
     * It serves as the container for all the products that are stored in the program.
     * It is defined by a list of {@code Product}
     * @see Serializable
     */
    public static class ProductList implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private final List<Product> products;

        /**
         * Creates a new {@code ProductList} instance
         *
         * @param products is the list of products of the system
         */
        public ProductList(List<Product> products) {
            this.products = products;
        }

        public List<Product> getProducts() {
            return products;
        }
    }

    /**
     * The {@code InfoDialog} class is used as a manager for all the dialog windows that pop up during
     * the execution of the program, except for the one that is used to add a product.
     * The dialog windows are customizable using the parameter {@code message} which helps to show the message dynamically.
     * @see Alert
     */
    public static class InfoDialog {
        /**
         * Displays an error alert with a custom message and style and exits the application when clicking "ok".
         * Often used when there's a server error or the application can't start properly.
         * @param message the error message
         */
        public static void showErrorAndExit(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            System.err.print("Connection Error");
            alert.setTitle("Connection Error");
            alert.setHeaderText("Cannot Start Application");
            alert.setContentText(message);

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(InfoDialog.class.getResource("alertStyle.css").toExternalForm());

            alert.showAndWait();
            Platform.exit();
        }

        /**
         * Displays an error alert with a custom message and style.
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
         * Displays an information alert with a custom message and style.
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
