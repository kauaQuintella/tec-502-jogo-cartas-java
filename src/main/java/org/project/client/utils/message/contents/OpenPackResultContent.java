package org.project.client.utils.message.contents;// (no package ...utils.message.contents)

import org.project.client.gameClient.classes.Skin;
import org.project.client.utils.message.contents.Content;

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