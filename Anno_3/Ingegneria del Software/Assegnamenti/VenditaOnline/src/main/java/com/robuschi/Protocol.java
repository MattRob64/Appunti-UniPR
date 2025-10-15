package com.robuschi;

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

        public Message(MessageType type, Object payload) {
            this.type = type;
            this.payload = payload;
        }

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
}
