package org.project.client.utils.message.contents;

public class CommandContent extends Content {
    private String command;
    public CommandContent(String command) { this.command = command; }
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
}