package org.project.server.utils.message;

import org.project.server.utils.message.contents.Content;

// Esta classe é apenas um contentor de dados. Nada de sockets ou streams aqui!
public class MessageServer {
    private String sender;
    private Content content;
    private long timestamp;

    public MessageServer(String sender, Content content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters para que o Gson possa ler os campos
    public String getSender() { return sender; }
    public Content getContent() { return content; }
    public long getTimestamp() { return timestamp; }
}