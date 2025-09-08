package org.project.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.project.client.libs.RuntimeTypeAdapterFactory;
import org.project.client.utils.message.contents.CommandContent;
import org.project.client.utils.message.contents.Content;
import org.project.client.utils.message.contents.LoginContent;
import org.project.client.utils.message.contents.PlayerActionContent;

public enum GsonSingleton {
    INSTANCE;

    private final Gson gson;

    GsonSingleton() {
        // Cria a "fábrica" que diferencia as subclasses de Content
        RuntimeTypeAdapterFactory<Content> adapter = RuntimeTypeAdapterFactory
                .of(Content.class, "type") // O campo "type" no JSON dirá qual é a classe
                .registerSubtype(CommandContent.class)
                .registerSubtype(PlayerActionContent.class)
                .registerSubtype(LoginContent.class); // Não se esqueça de registar TODOS os subtipos

        // CORREÇÃO: Atribua a instância configurada diretamente à variável da classe.
        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(adapter)
                .create();
    }

    public Gson getGson() {
        return gson;
    }
}