package org.project.server;

import java.io.*;
import java.net.Socket;

public class TCPServerThread implements Runnable {
    private Socket socket; // Client socket
    private TCPServerMain tcpServerMain; // Reference to the main server

    public TCPServerThread(Socket socket, TCPServerMain tcpServerMain) {
        this.socket = socket;
        this.tcpServerMain = tcpServerMain;
    }

    @Override
    public void run() {
        try {
            int clientNumber = tcpServerMain.getClientNumber();
            System.out.println("Client " + clientNumber + " at " + socket.getInetAddress() + " has connected.");

            // Set up input and output streams for client communication
            BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // Initial message to the client
            out_socket.println("Welcome to TCP Server!");

            // Read and print the client's message
            String message = in_socket.readLine();
            System.out.println("Client says: " + message);

            // Close the connection
            socket.close();
            System.out.println("Client " + clientNumber + " " + socket.getInetAddress() + " has disconnected.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
