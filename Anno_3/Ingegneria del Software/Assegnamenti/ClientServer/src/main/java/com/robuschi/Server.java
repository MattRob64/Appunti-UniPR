package com.robuschi;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Server application that manages product sales to multiple clients.
 * Generates random prices and handles purchase requests from clients.
 */
public class Server {
    private static final int PORT = 5555;
    private static final int MIN_PRICE = 10;
    private static final int MAX_PRICE = 100;
    private static final int PRICE_UPDATE_INTERVAL = 2000; // milliseconds

    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();
    private volatile int currentPrice;
    private volatile boolean running = true;
    private final AtomicInteger activeClients = new AtomicInteger(0);
    private final Random random = new Random();

    /**
     * Main method to start the server
     */
    public static void main(String[] args) {
        new Server().start();
    }

    /**
     * Starts the server and initializes all components
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            System.out.println("Waiting for clients to connect...");

            // Start price generator thread
            Thread priceGenerator = new Thread(this::generatePrices);
            priceGenerator.start();

            // Accept client connections
            Thread connectionAcceptor = new Thread(this::acceptConnections);
            connectionAcceptor.start();

            // Wait for all clients to finish
            while (running) {
                Thread.sleep(1000);

                // Check if all clients have finished
                synchronized (clients) {
                    if (activeClients.get() > 0) {
                        boolean allFinished = true;
                        for (ClientHandler client : clients) {
                            if (!client.hasFinished()) {
                                allFinished = false;
                                break;
                            }
                        }

                        if (allFinished) {
                            System.out.println("\nAll clients have completed their purchases.");
                            System.out.println("\033[0;31m" + "Server shutting down..." + "\033[0m");
                            running = false;
                        }
                    }
                }
            }

            // Clean shutdown
            serverSocket.close();
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Accepts incoming client connections
     */
    private void acceptConnections() {
        try {
            // Wait for at least 3 clients
            while (activeClients.get() < 3 || running) {
                if (!running && activeClients.get() >= 3) break;

                serverSocket.setSoTimeout(1000);
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(clientSocket);
                    synchronized (clients) {
                        clients.add(handler);
                        activeClients.incrementAndGet();
                    }
                    new Thread(handler).start();
                    System.out.println("\033[0;32m" + "Client connected. Total clients: " + activeClients.get() + "\033[0m");
                } catch (SocketTimeoutException e) {
                    // Timeout is normal, continue loop
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Error accepting connections: " + e.getMessage());
            }
        }
    }

    /**
     * Generates random prices periodically and broadcasts to all clients
     */
    private void generatePrices() {
        while (running) {
            try {
                Thread.sleep(PRICE_UPDATE_INTERVAL);

                if (activeClients.get() >= 3) {
                    currentPrice = MIN_PRICE + random.nextInt(MAX_PRICE - MIN_PRICE + 1);
                    System.out.println("\n[SERVER] " + "\033[1;33m" + "New selling price: €" + currentPrice + "\033[0m");

                    // Broadcast price to all connected clients
                    synchronized (clients) {
                        for (ClientHandler client : clients) {
                            if (!client.hasFinished()) {
                                client.sendPrice(currentPrice);
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Handles individual client connections
     */
    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private boolean finished = false;
        private int clientId;
        private static int nextId = 1;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientId = nextId++;
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null && !finished) {
                    handleMessage(message);
                }
            } catch (IOException e) {
                System.err.println("[CLIENT " + clientId + "] disconnected unexpectedly");
            } finally {
                cleanup();
            }
        }

        /**
         * Handles messages received from the client
         */
        private void handleMessage(String message) {
            if (message.startsWith("PURCHASE:")) {
                int requestedPrice = Integer.parseInt(message.substring(9));
                System.out.println("[CLIENT " + clientId + "] Purchase request at €" + requestedPrice);

                if (requestedPrice == currentPrice) {
                    out.println("PURCHASE_CONFIRMED:" + currentPrice);
                    System.out.println("[CLIENT " + clientId + "] " + "\033[1;36m" + "Purchase confirmed at €" + currentPrice + "\033[0m");
                } else {
                    out.println("PURCHASE_REJECTED");
                    System.out.println("[CLIENT " + clientId + "] Purchase rejected (price changed)");
                }
            } else if (message.equals("FINISHED")) {
                System.out.println("\033[0;92m" + "[CLIENT " + clientId + "] Has completed all purchases" + "\033[0m");
                finished = true;
                out.println("GOODBYE");
            }
        }

        /**
         * Sends the current price to the client
         */
        public void sendPrice(int price) {
            if (!finished && out != null) {
                out.println("PRICE:" + price);
            }
        }

        /**
         * Checks if the client has finished purchasing
         */
        public boolean hasFinished() {
            return finished;
        }

        /**
         * Cleans up resources when client disconnects
         */
        private void cleanup() {
            try {
                if (socket != null) socket.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                System.err.println("Error closing client resources: " + e.getMessage());
            }
        }
    }
}
