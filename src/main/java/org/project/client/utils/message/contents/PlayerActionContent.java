package org.project.client.utils.message.contents;

public class PlayerActionContent extends Content {
    private String action; // "FOGO", "AGUA", "NATUREZA"
    public PlayerActionContent(String action) { this.action = action; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}