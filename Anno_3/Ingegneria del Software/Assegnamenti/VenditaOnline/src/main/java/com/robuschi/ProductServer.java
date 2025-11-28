package com.robuschi;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * <p>The {@code ProductServer} class is a multithreaded server for handling online product sales.
 * <p>Manages user authentication, product inventory, client requests and server responses.
 * @see ClientHandler
 * @author Mattia Robuschi Caprara
**/
public class ProductServer {
    private static final int PORT = 5500;
    private final Map<String, String> users;
    private final List<Product> products;
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;
    private final List<ClientHandler> connectedClients;
    private volatile boolean running;


    /**
     * <p>Creates a new {@code ProductServer} instance with initial users and products.
     * <p>It initializes a thread-safe map for storing users.
     * <p>Creates a thread-safe list for products by wrapping an {@code ArrayList} with a synchronized wrapper.
     * <p>Initializes a thread pool that dynamically creates new threads as needed and reuses idle threads.
     * <p>Creates a thread-safe list for tracking connected clients.
     * <p>It also sets the default users and populate the product list with some items.
    **/
    public ProductServer() {
        this.users = new ConcurrentHashMap<>();
        this.products = Collections.synchronizedList(new ArrayList<>());
        this.threadPool = Executors.newCachedThreadPool();
        this.connectedClients = Collections.synchronizedList(new ArrayList<>());
        this.running = false;

        // Initialize default users
        users.put("user1", "password1");
        users.put("user2", "password2");
        users.put("user3", "password3");

        // Initialize products
        products.add(new Product("Laptop", 999.99, 1));
        products.add(new Product("Mouse", 29.99, 2));
        products.add(new Product("Keyboard", 79.99, 3));
        products.add(new Product("Monitor", 299.99, 4));
        products.add(new Product("Headphones", 149.99, 5));
    }

    /**
     * <p>Starts the server and begins accepting client connections.
    **/
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("\033[0;36m" + "===========================================");
            System.out.println("Server started on port " + PORT);
            System.out.println("Waiting for clients...");
            System.out.println("===========================================" + "\033[0m");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("\033[0;32m" + "New client connected: " + clientSocket.getInetAddress() + "\033[0m");
                    ClientHandler handler = new ClientHandler(clientSocket);
                    connectedClients.add(handler);
                    threadPool.execute(handler);
                } catch (SocketException e) {
                    if (!running) break;
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    /**
     * <p>Stops the server and cleans up resources.
    **/
    public void shutdown() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("Server stopped");
        } catch (Exception e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }

    /**
     * <p>Broadcasts product list update to all connected clients.
     * <p>Used especially for the ListView refresh in the client, so when there is a change in
     * the product inventory it gets immediately displayed.
    **/
    private void broadcastProductList() {
        synchronized (products) {
            Protocol.ProductList productList = new Protocol.ProductList(new ArrayList<>(products));
            Protocol.Message message = new Protocol.Message(Protocol.MessageType.PRODUCT_LIST, productList);

            synchronized (connectedClients) {
                for (ClientHandler client : connectedClients) {
                    if (client.authenticated) {
                        try {
                            client.sendMessage(message);
                        } catch (IOException e) {
                            System.err.println("Error broadcasting to client: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    /**
     * <p>Gets a comma-separated list of connected user's usernames.
     * @return string with all connected usernames or the string: {@code No active clients} if there are no active clients.
    **/
    private String getConnectedUsernames() {
        synchronized (connectedClients) {
            if (connectedClients.isEmpty()) {
                return "No active clients";
            }
            else {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                boolean first = true;
                for (ClientHandler client : connectedClients) {
                    if (client.authenticated && client.username != null) {
                        if (!first) sb.append(", ");
                        sb.append(client.username);
                        first = false;
                    }
                }
                sb.append("]");
                return sb.toString();
            }
        }
    }

    /**
     * <p>The {@code ClientHandler} class handles individual client connections in a separate thread.
     * <p>Manages the socket connection for every user, the input and output streams, their authentication status and their username.
     * <p>All the messages that have to be sent to the clients are managed using this class.
     * @see Runnable
    **/
    private class ClientHandler implements Runnable {
        private final Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private boolean authenticated;
        private String username;

        /**
         * <p>Creates a new {@code ClientHandler} instance and sets the username and the authentication status to {@code null}.
         * @param socket is the socket that the client uses to connect to the server
        **/
        public ClientHandler(Socket socket) {
            this.socket = socket;
            this.authenticated = false;
            this.username = null;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        /**
         * {@inheritDoc}
         * <p>This method manages the input and output stream of the various messages and checks for errors.
         * <p>When a client is disconnected it removes it from the {@code connectedClients} list.
        **/
        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                while (running) {
                    Protocol.Message message = (Protocol.Message) in.readObject();
                    handleMessage(message);
                }
            } catch (EOFException | SocketException e) {
                System.out.println("\033[0;33m" + "Client disconnected: " + socket.getInetAddress() + "\033[0m");
            } catch (Exception e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                // Remove this client from connected clients list
                synchronized (connectedClients) {
                    connectedClients.remove(this);
                    if (getUsername() != null) {
                        System.out.println("\033[0;33m" + "User removed from active clients: " + getUsername() + "\033[0m");
                        System.out.println("\033[0;32m" + "Active clients: " + getConnectedUsernames() + "\033[0m");
                    }
                }
                closeConnection();
            }
        }

        /**
         * <p>Handles all the messages that receives from the login form and the user client.
         * <ul>
         *      <li>If the received message has type {@code AUTH_REQUEST} the {@code handleAuthentication()} is called</li>
         *      <li>If the received message has type {@code GET_PRODUCTS} the {@code handleGetProducts()} is called</li>
         *      <li>If the received message has type {@code PURCHASE_PRODUCT} the {@code handlePurchase()} is called</li>
         *      <li>If the received message has type {@code RETURN_PRODUCT} the {@code handleReturn()} is called</li>
         *      <li>If the received message has type {@code ADD_NEW_PRODUCT} the {@code handleAddProduct()} is called</li>
         *      <li>If the received message has type {@code USER_LOGOUT} the {@code handleUserLogout()} is called</li>
         *      <li>If the received message has type {@code CLOSE} the {@code handleClose()} is called</li>
         * </ul>
         * @param message based on this parameters it uses a switch-case to determinate which method has to use based on the request
         * @throws IOException based on the exceptions that the methods used inside this method throws in return
         * @see Protocol.MessageType
        **/
        private void handleMessage(Protocol.Message message) throws IOException {
            switch (message.getType()) {
                case AUTH_REQUEST:
                    handleAuthentication(message);
                    break;
                case GET_PRODUCTS:
                    if (authenticated) handleGetProducts();
                    break;
                case PURCHASE_PRODUCT:
                    if (authenticated) handlePurchase(message);
                    break;
                case RETURN_PRODUCT:
                    if (authenticated) handleReturn(message);
                    break;
                case ADD_NEW_PRODUCT:
                    if (authenticated) handleAddProduct(message);
                    break;
                case USER_LOGOUT:
                    if (authenticated) handleUserLogout();
                    break;
                case CLOSE:
                    handleClose();
                    break;
            }
        }

        /**
         * <p>This method is called when the client makes an authentication attempt.
         * <p>After pressing on the login button in the dedicated form, it sends this message,
         * and if the authentication is successful the user can access the main page of the client.
         * <p>If the credentials used by the user are incorrect it sends a message to the clients which states that the authentication attempt failed.
         * <p>It sends out a message of type {@code AUTH_SUCCESS} to the client if the authentication is successful, otherwise it sends out a message of type {@code AUTH_FAILED}.
         * @param message contains the type and payload which in this case is the couple username and password
         * @throws IOException
        +*/
        private void handleAuthentication(Protocol.Message message) throws IOException {
            Protocol.AuthCredentials creds = (Protocol.AuthCredentials) message.getPayload();
            String storedPassword = users.get(creds.getUsername());

            if (storedPassword != null && storedPassword.equals(creds.getPassword())) {
                authenticated = true;
                setUsername(creds.getUsername());
                sendMessage(new Protocol.Message(Protocol.MessageType.AUTH_SUCCESS, creds.getUsername()));
                System.out.println("\033[0;32m" + "User authenticated: " + creds.getUsername() + "\033[0m");
            } else {
                sendMessage(new Protocol.Message(Protocol.MessageType.AUTH_FAILED));
                System.err.println("Authentication failed for: " + creds.getUsername());
            }
        }

        /**
         * <p>This method is called when the product list of the client needs to be updated.
         * <p>Usually called when we want to broadcast the changes in the list.
         * <p>It sends out a message of type {@code PRODUCT_LIST} to the client stating that it has to update its local list.
         * @throws IOException
        **/
        private void handleGetProducts() throws IOException {
            synchronized (products) {
                Protocol.ProductList productList = new Protocol.ProductList(new ArrayList<>(products));
                sendMessage(new Protocol.Message(Protocol.MessageType.PRODUCT_LIST, productList));
            }
        }

        /**
         * <p>This method is called when a user tries to buy a product.
         * <p>Using the synchronized products list, it removes the product from the general product list
         * and sends out a message to the client with the product as the payload so that the client can add it to its personal product list.
         * <p>If the general product list is not updated in the client and the user tries to buy a product that is not in the list anymore,
         * this method sends out a message of type {@code ERROR} telling that the product is not available anymore.
         * <p>If the removal is successful it launches the {@code broadcastProductList()} method so that every client can update their server's product list.
         * <p>It sends out a message of type {@code PRODUCT_PURCHASED} to the client indicating that it can update its product list with the product that has bought.
         * @param message contains the message type and the payload, which in this case is the product that the user wants to buy
         * @throws IOException
        **/
        private void handlePurchase(Protocol.Message message) throws IOException {
            Product product = (Product) message.getPayload();

            synchronized (products) {
                if (products.remove(product)) {
                    sendMessage(new Protocol.Message(Protocol.MessageType.PRODUCT_PURCHASED, product));
                    System.out.println("Product: "+ product.getName() + " purchased by user: " + getUsername());
                    broadcastProductList();
                } else {
                    sendMessage(new Protocol.Message(Protocol.MessageType.ERROR, "Product not available, press the refresh button please"));
                }
            }
        }

        /**
         * <p>This method is called when a user tries to return a product.
         * <p>It gets the product that the user wants to return from the message payload and adds it back to the server product list.
         * <p>It then launches the {@code broadcastProductList()} method so that every client can update their server's product list.
         * <p>It sends out a message of type {@code RETURN_ACCEPTED} to the client stating that return process was successful and the client product list can be updated.
         * @param message contains the message type and the payload, which in this case is the product that the user wants to return
         * @throws IOException
        **/
        private void handleReturn(Protocol.Message message) throws IOException {
            Product product = (Product) message.getPayload();
            synchronized (products) {
                products.add(product);
                sendMessage(new Protocol.Message(Protocol.MessageType.RETURN_ACCEPTED));
                System.out.println("Product: "+ product.getName() + " returned by user: " + getUsername());
                broadcastProductList();
            }
        }

        /**
         * <p>This method is called when the user wants to add a product to the server's product list.
         * <p>It gets the product (ID, Name, Price) from the message payload, then it adds it to the server's product list.
         * <p>It then launches the {@code broadcastProductList()} method so that every client can update their server's product list.
         * @param message contains the message type and the payload, which in this case is the product that the user wants to add to the product list
         * @throws IOException
        **/
        private void handleAddProduct(Protocol.Message message) throws IOException {
            Product product = (Product) message.getPayload();
            synchronized (products) {
                int newId = products.stream()
                        .mapToInt(Product::getIdentifier)
                        .max()
                        .orElse(0) + 1;
                Product newProduct = new Product(product.getName(), product.getPrice(), newId);
                products.add(newProduct);
                System.out.println("user: " + getUsername() + " added a product successfully: " + product.getName() + " - " + String.format("%.2f", product.getPrice()) + "€");
                broadcastProductList();
            }
        }

        /**
         * <p>This method is called when the user wats to logout (and exit) from the client.
         * <p>It simply prints a message showing which user has disconnected.
        **/
        private void handleUserLogout() {
            System.out.println("\033[0;33m" + "User disconnected: " + getUsername() + "\033[0m");
        }

        /**
         * <p>This method is called when the user wants to disconnect from the server.
         * <p>This message originates from the NetworkManager class.
         * @throws IOException
         * @see NetworkManager
        **/
        private void handleClose() throws IOException {
            sendMessage(new Protocol.Message(Protocol.MessageType.SERVER_CLOSE));
            closeConnection();
        }

        /**
         * <p>This method handles the message sending process by sending on the output stream and then flushing the output stream.
         * @param message is the message that a method wants to send out.
         * @throws IOException
        **/
        private void sendMessage(Protocol.Message message) throws IOException {
            out.writeObject(message);
            out.flush();
        }

        /**
         * <p>This method closes the input stream, the output stream and the socket.
         * <p>It throws an error if the connection can not be closed.
        +*/
        private void closeConnection() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * <p>Main method to start the server.
    **/
    public static void main(String[] args) {
        ProductServer server = new ProductServer();
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.start();
    }
}
