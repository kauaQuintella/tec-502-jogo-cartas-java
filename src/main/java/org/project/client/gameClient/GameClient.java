package org.project.client.gameClient;

import org.project.client.gameClient.classes.User;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.*;
import com.google.gson.Gson;
import org.project.client.utils.Message;

public class GameClient {

    private final User user;
    private final Gson gson;
    private final BufferedReader in;
    private final PrintWriter out;

    public GameClient(User user, BufferedReader in_socket, PrintWriter out_socket) {
        this.user = user;
        this.gson = new Gson();
        this.in = in_socket;
        this.out = out_socket;
    }

    public void sendMessage(String command){
        Message message = new Message(user.getNickname(), command, System.currentTimeMillis());
        String messageJson = gson.toJson(message);
        out.println(messageJson);
    }

    public Message reciveMessage() throws IOException {
        String answerJson = in.readLine();
        return gson.fromJson(answerJson, Message.class);
    }

    public String queryMessage () throws IOException {
        Message answer;

        this.sendMessage("jogar");
        answer = this.reciveMessage();

        return answer.getContent();
    }

    public void login(String nickname) {
        user.setNickname(nickname);
    }


}
