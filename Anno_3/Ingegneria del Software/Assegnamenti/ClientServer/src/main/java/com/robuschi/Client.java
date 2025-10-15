package com.robuschi;

import java.io.*;
import java.net.*;
import java.util.Random;

/**
 * Client application that connects to the product server and makes purchase decisions
 * based on randomly generated maximum purchase prices.
 */
public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5555;
    private static final int MIN_MAX_PRICE = 10;
    private static final int MAX_MAX_PRICE = 75;
    private static final int TARGET_PURCHASES = 10;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int purchaseCount = 0;
    private final Random random = new Random();
    private volatile boolean running = true;
    private final int clientId;

    /**
     * Constructor for Client
     * @param clientId Unique identifier for this client instance
     */
    public Client(int clientId) {
        this.clientId = clientId;
    }

    /**
     * Main method to start multiple clients
     * @param args Command line arguments (optional: number of clients)
     */
    public static void main(String[] args) {
        int numClients = 3; // Default number of clients

        if (args.length > 0) {
            try {
                numClients = Integer.parseInt(args[0]);
                if (numClients < 3) {
                    System.out.println("Minimum 3 clients required. Setting to 3.");
                    numClients = 3;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number of clients. Using default: 3");
            }
        }

        System.out.println("Starting " + numClients + " clients...");

        // Start multiple client threads
        for (int i = 1; i <= numClients; i++) {
            final int clientId = i;
            Thread clientThread = new Thread(() -> {
                new Client(clientId).start();
            });
            clientThread.start();

            // Small delay between client starts
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts the client and connects to the server
     */
    public void start() {
        try {
            // Connect to server
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("\033[0;32m" + "[CLIENT " + clientId + "] Connected to server at " +
                    SERVER_HOST + ":" + SERVER_PORT + "\033[0m");

            // Listen for server messages
            String message;
            while (running && (message = in.readLine()) != null) {
                handleServerMessage(message);

                // Check if we've reached our purchase target
                if (purchaseCount >= TARGET_PURCHASES) {
                    finishPurchasing();
                }
            }

        } catch (IOException e) {
            System.err.println("[CLIENT " + clientId + "] Connection error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Handles messages received from the server
     * @param message The message received from the server
     */
    private void handleServerMessage(String message) {
        if (message.startsWith("PRICE:")) {
            int sellingPrice = Integer.parseInt(message.substring(6));
            handlePriceUpdate(sellingPrice);
        } else if (message.startsWith("PURCHASE_CONFIRMED:")) {
            int confirmedPrice = Integer.parseInt(message.substring(19));
            purchaseCount++;
            System.out.println("[CLIENT " + clientId + "] " + "\033[1;36m" + "Purchase #" + purchaseCount +
                    " confirmed at €" + confirmedPrice + "\033[0m" + "\033[1;33m" +
                    " (Total: " + purchaseCount + "/" + TARGET_PURCHASES + ")" + "\033[0m");
        } else if (message.equals("PURCHASE_REJECTED")) {
            System.out.println("[CLIENT " + clientId + "] Purchase rejected (price changed)");
        } else if (message.equals("GOODBYE")) {
            System.out.println("[CLIENT " + clientId + "] Server acknowledged completion. Disconnecting.");
            running = false;
        }
    }

    /**
     * Handles price updates from the server
     * @param sellingPrice The current selling price from the server
     */
    private void handlePriceUpdate(int sellingPrice) {
        if (purchaseCount >= TARGET_PURCHASES) {
            return; // Already completed purchases
        }

        // Generate random maximum purchase price
        int maxPurchasePrice = MIN_MAX_PRICE + random.nextInt(MAX_MAX_PRICE - MIN_MAX_PRICE + 1);

        System.out.println("[CLIENT " + clientId + "] Received price: €" + sellingPrice +
                ", Max willing to pay: €" + maxPurchasePrice);

        // Decide whether to purchase
        if (sellingPrice <= maxPurchasePrice) {
            System.out.println("[CLIENT " + clientId + "] Attempting to purchase at €" + sellingPrice);
            out.println("PURCHASE:" + sellingPrice);
        } else {
            System.out.println("[CLIENT " + clientId + "] " + "\033[1;35m" + "Price too high, not purchasing" + "\033[0m");
        }
    }

    /**
     * Informs the server that this client has finished purchasing
     */
    private void finishPurchasing() {
        System.out.println("[CLIENT " + clientId + "] " + "\033[0;92m" + "Completed all " + TARGET_PURCHASES +
                " purchases. Notifying server." + "\033[0m");
        out.println("FINISHED");
        running = false;
    }

    /**
     * Cleans up resources when client disconnects
     */
    private void cleanup() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            System.out.println("\033[0;31m" + "[CLIENT " + clientId + "] Disconnected from server" + "\033[0m");
        } catch (IOException e) {
            System.err.println("[CLIENT " + clientId + "] Error closing resources: " + e.getMessage());
        }
    }
}
