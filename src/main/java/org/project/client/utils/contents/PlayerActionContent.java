package org.project.client.utils.contents;

public class PlayerActionContent extends Content {
    private String action; // "FOGO", "AGUA", "NATUREZA"
    public PlayerActionContent(String action) { this.action = action; }
    public String getAction() { return action; }
}