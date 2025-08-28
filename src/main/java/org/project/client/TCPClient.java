package org.project.client;

import org.project.server.TCPServerMain;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    private Socket socket; // Client socket

    TCPClient() throws IOException {

        // Connect to the server at localhost on port 2020
        socket = new Socket("localhost", 2020);

        System.out.println("Successfully connected to the server.");

        // Set up input and output streams for communication
        BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        Scanner keyboard = new Scanner(System.in);

        // Receive and display the server's welcome message
        String message = in_socket.readLine();
        System.out.println("Server says: " + message);

        // Send a message to the server
        message = keyboard.nextLine();
        out_socket.println(message);

        // Close the connection
        this.logout();
    }

    public void logout() {
        try {
            // Close the connection
            socket.close();
            System.out.println("Port 2020 closed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {
            new TCPClient(); // Start the client

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}