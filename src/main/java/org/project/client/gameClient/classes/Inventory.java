package org.project.client.gameClient.classes;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final List<Skin> skins; // Alterado para List<Skin>

    public Inventory() {
        this.skins = new ArrayList<>();
    }

    public List<Skin> getSkins() { return skins; }

    public void addSkin (Skin skin) { // Alterado para aceitar Skin
        this.skins.add(skin);
    }
}