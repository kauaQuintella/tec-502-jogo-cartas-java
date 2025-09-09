package org.project.server;

import org.project.server.game.PlayerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerMain {

    private ServerSocket serverSocket;

    public TCPServerMain() throws IOException {

        // Create a server socket listening on port 2020
        serverSocket = new ServerSocket(2020, 50, java.net.InetAddress.getByName("0.0.0.0"));
        System.out.println("Servidor iniciado em 0.0.0.0 na porta 2020. Aguardando conexões...");

        // Loop to continuously listen for new client connections
        while (true) {
            Socket socket = serverSocket.accept(); // Accept a new client connection

            // Create a new thread to handle the connected client
            PlayerThread playerThread = new PlayerThread(socket);
            Thread thread = new Thread(playerThread);
            thread.start();
        }

    }

    public void stop() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("ServerSocket encerrado.");
        }
    }

    public static void main(String[] args) {

        try {
            new TCPServerMain(); // Start the server

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
