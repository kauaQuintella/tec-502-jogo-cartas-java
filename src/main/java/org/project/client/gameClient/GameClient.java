package org.project.client.gameClient;

import org.project.client.gameClient.classes.User;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.google.gson.Gson;
import org.project.client.utils.contents.Content;
import org.project.client.utils.MessageClient;

public class GameClient {

    private final User user;
    private final Gson gson;
    private final BufferedReader in;
    private final PrintWriter out;
    private MessageClient messageClient;
    private Content content;

    public GameClient(User user, BufferedReader in_socket, PrintWriter out_socket) {
        this.user = user;
        this.gson = new Gson();
        this.in = in_socket;
        this.out = out_socket;
        this.messageClient = new MessageClient(user.getNickname(), "", content);
    }

    public void login(String nickname) {
        user.setNickname(nickname);
        messageClient.sendMessage("", out);
    }


}
