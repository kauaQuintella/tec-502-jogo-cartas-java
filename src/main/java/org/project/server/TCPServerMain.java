package org.project.server;

import org.project.server.game.PlayersThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerMain {

    public TCPServerMain() throws IOException {

        // Create a server socket listening on port 2020
        ServerSocket serverSocket = new ServerSocket(2020);
        System.out.println("Port 2020 is open");

        // Loop to continuously listen for new client connections
        while (true) {
            Socket socket = serverSocket.accept(); // Accept a new client connection

            // Create a new thread to handle the connected client
            PlayersThread playersThread = new PlayersThread(socket, this);
            Thread thread = new Thread(playersThread);
            thread.start();
        }
    }

    private int clientNumber = 1;

    // Increment and return the unique client number
    public int getClientNumber() {
        return clientNumber++;
    }
}
