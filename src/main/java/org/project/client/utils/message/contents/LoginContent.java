package org.project.client.utils.message.contents;

import org.project.client.gameClient.classes.User;

public class LoginContent extends Content {
    private User user;
    public LoginContent(User user) { this.user = user; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}