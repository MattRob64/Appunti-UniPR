package com.robuschi;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Multi-threaded server for handling online product sales.
 * Manages user authentication, product inventory, and client requests.
 */
public class ProductServer {
    private static final int PORT = 5500;
    private final Map<String, String> users;
    private final List<Product> products;
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;
    private final List<ClientHandler> connectedClients;
    private volatile boolean running;


    /**
     * Creates a new ProductServer with initial users and products.
     */
    public ProductServer() {
        this.users = new ConcurrentHashMap<>();
        this.products = Collections.synchronizedList(new ArrayList<>());
        this.threadPool = Executors.newCachedThreadPool();
        this.connectedClients = Collections.synchronizedList(new ArrayList<>());
        this.running = false;

        // Initialize default users
        users.put("admin", "admin123");
        users.put("user1", "password1");
        users.put("user2", "password2");

        // Initialize products
        products.add(new Product("Laptop", 999.99, 1));
        products.add(new Product("Mouse", 29.99, 2));
        products.add(new Product("Keyboard", 79.99, 3));
        products.add(new Product("Monitor", 299.99, 4));
        products.add(new Product("Headphones", 149.99, 5));
    }

    /**
     * Starts the server and begins accepting client connections.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("===========================================");
            System.out.println("Server started on port " + PORT);
            System.out.println("Waiting for clients...");
            System.out.println("===========================================");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());
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
     * Stops the server and cleans up resources.
     */
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
     * Broadcasts product list update to all connected clients.
     */
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
     * Gets a comma-separated list of connected usernames.
     *
     * @return string with all connected usernames
     */
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
     * Handles individual client connections in a separate thread.
     */
    private class ClientHandler implements Runnable {
        private final Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private boolean authenticated;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            this.authenticated = false;
            this.username = null;
        }

        public String getUsername() {
            return username;
        }

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
                System.out.println("Client disconnected: " + socket.getInetAddress());
            } catch (Exception e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                // Remove this client from connected clients list
                synchronized (connectedClients) {
                    connectedClients.remove(this);
                    if (username != null) {
                        System.out.println("User removed from active clients: " + username);
                        System.out.println("Active clients: " + getConnectedUsernames());
                    }
                }
                closeConnection();
            }
        }

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
                    if (authenticated) handleUserLogout(message);
                    break;
                case CLOSE:
                    handleClose();
                    break;
            }
        }

        private void handleAuthentication(Protocol.Message message) throws IOException {
            Protocol.AuthCredentials creds = (Protocol.AuthCredentials) message.getPayload();
            String storedPassword = users.get(creds.getUsername());

            if (storedPassword != null && storedPassword.equals(creds.getPassword())) {
                authenticated = true;
                username = creds.getUsername();
                sendMessage(new Protocol.Message(Protocol.MessageType.AUTH_SUCCESS, creds.getUsername()));
                System.out.println("User authenticated: " + creds.getUsername());
            } else {
                sendMessage(new Protocol.Message(Protocol.MessageType.AUTH_FAILED));
                System.out.println("Authentication failed for: " + creds.getUsername());
            }
        }

        private void handleGetProducts() throws IOException {
            synchronized (products) {
                Protocol.ProductList productList = new Protocol.ProductList(new ArrayList<>(products));
                sendMessage(new Protocol.Message(Protocol.MessageType.PRODUCT_LIST, productList));
            }
        }

        private void handlePurchase(Protocol.Message message) throws IOException {
            Product product = (Product) message.getPayload();

            synchronized (products) {
                if (products.remove(product)) {
                    sendMessage(new Protocol.Message(Protocol.MessageType.PRODUCT_PURCHASED, product));
                    System.out.println("Product: "+ product.getName() + " purchased by user: " + username);
                    broadcastProductList();
                } else {
                    sendMessage(new Protocol.Message(Protocol.MessageType.ERROR, "Product not available, press the refresh button please"));
                }
            }
        }

        private void handleReturn(Protocol.Message message) throws IOException {
            Product product = (Product) message.getPayload();
            synchronized (products) {
                products.add(product);
                sendMessage(new Protocol.Message(Protocol.MessageType.RETURN_ACCEPTED));
                System.out.println("Product: "+ product.getName() + " returned by user: " + username);
                broadcastProductList();
            }
        }

        private void handleAddProduct(Protocol.Message message) throws IOException {
            Product product = (Product) message.getPayload();
            synchronized (products) {
                int newId = products.stream()
                        .mapToInt(Product::getIdentifier)
                        .max()
                        .orElse(0) + 1;
                Product newProduct = new Product(product.getName(), product.getPrice(), newId);
                products.add(newProduct);
                System.out.println("user: " + username + " added a product successfully: " + product.getName() + " - " + String.format("%.2f", product.getPrice()) + "€");
                broadcastProductList();
            }
        }

        private void handleUserLogout(Protocol.Message message) {
            String username = (String) message.getPayload();
            System.out.println("User disconnected: " + username);
        }

        private void handleClose() throws IOException {
            sendMessage(new Protocol.Message(Protocol.MessageType.SERVER_CLOSE));
            closeConnection();
        }

        private void sendMessage(Protocol.Message message) throws IOException {
            out.writeObject(message);
            out.flush();
        }

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
     * Main method to start the server.
     */
    public static void main(String[] args) {
        ProductServer server = new ProductServer();
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.start();
    }
}
