package org.project.server.utils.message.contents;// (no package ...utils.message.contents)

import org.project.server.game.classes.Skin; // ou do cliente, dependendo do lado

public class OpenPackResultContent extends Content {
    private final Skin skinAdquirida;
    private final String mensagem;

    public OpenPackResultContent(Skin skin, String mensagem) {
        this.skinAdquirida = skin;
        this.mensagem = mensagem;
    }

    public Skin getSkinAdquirida() { return skinAdquirida; }
    public String getMensagem() { return mensagem; }
}