package org.project.server.game.classes;
import java.util.UUID;

public class User {

    private final String idUser;
    private String nickname;
    private Inventory inventory;

    public User(String nickname, Inventory inventory) {
        this.nickname = nickname;
        this.inventory = inventory;
        this.idUser = nickname+UUID.randomUUID().toString();
    }

    //Será que precisa?
    public void setNickname(String nickname) {this.nickname = nickname;}
    public void setInventory(Inventory inventory) {this.inventory = inventory;}

    public String getIdUser() {return this.idUser;}
    public Inventory getInventory() {return inventory;}
    public String getNickname() {return nickname;}

}
