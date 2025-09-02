package org.project.server.game;

import org.project.server.TCPServerMain;
import org.project.server.game.classes.User;
import org.project.server.utils.GsonSingleton;
import org.project.server.utils.MessageServer;
import org.project.server.utils.contents.CommandContent;
import org.project.server.utils.contents.Content;
import org.project.server.utils.contents.PlayerActionContent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerThread implements Runnable {
    private final Socket socket;
    private final int clientNumber;
    private final User user;
    private final BufferedReader in;
    private final PrintWriter out;

    private Content content;

    public PlayerThread(Socket socket, TCPServerMain tcpServerMain, User user) throws IOException {
        this.socket = socket;
        this.clientNumber = tcpServerMain.getClientNumber();
        this.user = user; // Agora o User é parte fundamental da Thread

        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public int getClientNumber() {return clientNumber;}

    public void sendMessageToClient(String message) throws IOException {
        if (out != null && !socket.isClosed()) {
            out.println(message);
        }
    }

    @Override
    public void run() {
        try {
            // ... configuração de in_socket e out_socket ...

            out.println("Bem-vindo! Digite 'JOGAR' para entrar na fila.");

            String command = in.readLine();
            if ("JOGAR".equalsIgnoreCase(command)) {
                // A thread se registra no GameManager
                RoomsManager.getInstance().joinWaitingQueue(this);
                out.println("Você está na fila. Aguardando oponente...");

                // A thread agora precisa esperar o jogo começar.
                // (A lógica de jogo será movida para outra classe)

            } else {
                out.println("Comando inválido.");
            }

        } catch (Exception e) {
            // Lógica para remover o jogador da fila se ele desconectar
            System.out.println("Cliente " + clientNumber + " desconectou.");
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
