package org.project.client.gameClient;

import org.project.client.gameClient.classes.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import org.project.client.utils.message.MessageClient;
import org.project.server.utils.GsonSingleton;
import org.project.server.utils.message.MessageServer;
import org.project.server.utils.message.contents.CommandContent;
import org.project.server.utils.message.contents.Content;

public class GameClient {

    private final User user;
    private final Gson gson;
    private final BufferedReader in;
    private final PrintWriter out;
    private MessageClient messageClient;
    private CommandContent content;

    public GameClient(User user, BufferedReader in_socket, PrintWriter out_socket) {
        this.user = user;
        this.gson = new Gson();
        this.in = in_socket;
        this.out = out_socket;
        this.content = new CommandContent("Bem-vindo! Por favor, envie os seus dados de login.");
    }

    // --- MÉTODOS DE AJUDA PARA COMUNICAÇÃO ---
    public void sendMessage(Content content) {
        MessageServer message = new MessageServer(this.user != null ? this.user.getNickname() : "Servidor", content);
        String jsonMessage = GsonSingleton.INSTANCE.getGson().toJson(message);
        out.println(jsonMessage);
    }

    public MessageServer receiveMessage() throws IOException {
        String jsonMessage = in.readLine();
        if (jsonMessage == null) {
            throw new IOException("O cliente fechou a conexão.");
        }
        return GsonSingleton.INSTANCE.getGson().fromJson(jsonMessage, MessageServer.class);
    }
}
