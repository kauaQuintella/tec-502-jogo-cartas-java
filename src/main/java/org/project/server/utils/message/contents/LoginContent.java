package org.project.server.utils.message.contents;

import org.project.server.game.classes.User;

public class LoginContent extends Content {
    private User user;
    public LoginContent(User user) { this.user = user; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}