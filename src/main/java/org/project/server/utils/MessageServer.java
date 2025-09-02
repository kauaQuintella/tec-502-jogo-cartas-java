package org.project.server.utils;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;

import org.project.client.utils.contents.Content;
import org.project.client.libs.RuntimeTypeAdapterFactory;


public class MessageServer {
    private String infoMessage;
    private String sender;
    private Content content;
    private long timestamp;

    // Constructor, getters, and setters
    public MessageServer(String infoMessage, String sender, Content content) {
        this.infoMessage = "";
        this.sender = sender;
        this.content = content;
        this.timestamp = 0;
    }

    public String getSender() { return sender; }
    public Content getContent() { return content; }
    public long getTimestamp() { return timestamp; }

    public void setSender(String sender) { this.sender = sender; }
    public void setContent(Content content) { this.content = content; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public void sendMessage(Content content, PrintWriter out){
        timestamp = System.currentTimeMillis();
        this.content = content;
        //Message message = new Message(sender, command, System.currentTimeMillis());
        String messageJson = GsonSingleton.INSTANCE.getGson().toJson(this);
        out.println(messageJson);
    }

    public MessageServer reciveMessage(BufferedReader in) throws IOException {
        timestamp = System.currentTimeMillis();

        String answerJson = in.readLine();
        return GsonSingleton.INSTANCE.getGson().fromJson(answerJson, MessageServer.class);


    }
}