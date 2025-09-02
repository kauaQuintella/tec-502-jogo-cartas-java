package org.project.client;

import org.project.server.TCPServerMain;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    public TCPClient() throws IOException {
        Socket socket = new Socket("localhost", 2020);
        System.out.println("Conectado ao servidor.");

        // Thread para ouvir o servidor
        new Thread(new ServerListener(socket)).start();

        // A thread principal envia os inputs do utilizador
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner keyboard = new Scanner(System.in);
        while (true) {
            String command = keyboard.nextLine();
            out.println(command);
        }
    }

    // Classe interna para ouvir as mensagens do servidor
    private static class ServerListener implements Runnable {
        private final Socket socket;
        public ServerListener(Socket socket) { this.socket = socket; }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println("Servidor: " + serverMessage);
                }
            } catch (IOException e) {
                System.out.println("Desconectado do servidor.");
            }
        }
    }

    public static void main(String[] args) {
        try {
            new TCPClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}