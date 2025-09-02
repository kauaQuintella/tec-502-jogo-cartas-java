package org.project.server.game.classes;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private List<String> skins;

    public Inventory(String[] skins) {
        this.skins = new ArrayList<>();
    }

    public List<String> getSkins() {return skins;}
    public void setSkins(List<String> skins) {this.skins = skins;}

    public void addSkin (String skin){
        this.skins.add(skin);
    }



}
