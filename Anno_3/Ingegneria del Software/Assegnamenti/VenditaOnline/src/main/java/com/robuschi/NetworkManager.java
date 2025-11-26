package com.robuschi;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Manages network communication with the server.
 * Handles socket connection, message sending, and receiving.
 */
public class NetworkManager {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5500;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Thread listenerThread;
    private Consumer<Protocol.Message> messageHandler;

    /**
     * Connects to the server.
     *
     * @return true if connection successful, false otherwise
     */
    public boolean connect() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            startListening();
            return true;
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage() + "at port: " + SERVER_PORT);
            return false;
        }
    }

    /**
     * Sets the message handler for incoming messages.
     *
     * @param handler consumer that processes incoming messages
     */
    public void setMessageHandler(Consumer<Protocol.Message> handler) {
        this.messageHandler = handler;
    }

    /**
     * Sends a message to the server.
     *
     * @param message the message to send
     */
    public void sendMessage(Protocol.Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    /**
     * Starts listening for incoming messages from the server.
     */
    private void startListening() {
        listenerThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Protocol.Message message = (Protocol.Message) in.readObject();
                    if (messageHandler != null) {
                        Platform.runLater(() -> messageHandler.accept(message));
                    }
                }
            } catch (EOFException | java.net.SocketException e) {
                System.out.println("\033[0;33m" + "Connection closed" + "\033[0m");
            } catch (Exception e) {
                System.err.println("Error in listener: " + e.getMessage());
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /**
     * Closes the connection to the server.
     */
    public void disconnect() {
        try {
            if (listenerThread != null) {
                listenerThread.interrupt();
            }
            if (out != null) {
                sendMessage(new Protocol.Message(Protocol.MessageType.CLOSE));
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
}