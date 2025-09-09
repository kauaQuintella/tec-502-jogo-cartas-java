package org.project.client.gameClient.classes;

public class User {

    private String nickname;
    private Inventory inventory;

    public User(String nickname, Inventory inventory) {
        this.nickname = nickname;

        if (inventory == null) {
            this.inventory = new Inventory(); // Cria um inventário vazio
        } else {
            this.inventory = inventory;
        }
    }

    public String getNickname() {return nickname;}
    public void setNickname(String nickname) {this.nickname = nickname;}

    public Inventory getInventory() {return inventory;}
    public void setInventory(Inventory inventory) {this.inventory = inventory;}
}
