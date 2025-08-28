package org.project.server.game;

import org.project.server.TCPServerMain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayersThread implements Runnable {
    private Socket socket; // Client socket
    private TCPServerMain tcpServerMain; // Reference to the main server
    private int clientNumber;

    public PlayersThread(Socket socket, TCPServerMain tcpServerMain) {
        this.socket = socket;
        this.tcpServerMain = tcpServerMain;
        this.clientNumber = tcpServerMain.getClientNumber();
    }

    @Override
    public void run() {
        try {
            outerLoop:
            while (true) {
                int clientNumber = tcpServerMain.getClientNumber();
                System.out.println("Client " + clientNumber + " at " + socket.getInetAddress() + " has connected.");

                // Set up input and output streams for client communication
                BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                // Initial message to the client
                out_socket.println("Type your card!: ");

                // Read and print the client's message
                String message = in_socket.readLine();
                System.out.println("Client says: " + message);
                Thread.sleep(2000);

                if (message.equals("Yes")) {
                    this.stop();
                    break outerLoop;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            // Close the connection
            socket.close();
            System.out.println("Client " + clientNumber + " " + socket.getInetAddress() + " has disconnected.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void waitingList() {
        try {
            int clientNumber = tcpServerMain.getClientNumber();
            System.out.println("Client " + clientNumber + " at " + socket.getInetAddress() + " has connected.");

            // Set up input and output streams for client communication
            BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // Initial message to the client
            out_socket.println("Type your card!: ");

            // Read and print the client's message
            String message = in_socket.readLine();
            System.out.println("Client says: " + message);
            Thread.sleep(2000);

            // Close the connection
            socket.close();
            System.out.println("Client " + clientNumber + " " + socket.getInetAddress() + " has disconnected.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
