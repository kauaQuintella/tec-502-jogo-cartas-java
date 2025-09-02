package org.project.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.project.client.libs.RuntimeTypeAdapterFactory;
import org.project.client.utils.contents.*;
// Se for usar a abordagem de polimorfismo, importe o adapter aqui

public enum GsonSingleton {
    INSTANCE; // A única instância da nossa enum

    private final Gson gson;

    GsonSingleton() {
        // O construtor da enum é chamado apenas uma vez pela JVM, garantindo o Singleton.

        // Se precisar de configuração especial, use o GsonBuilder:
        // Crie a "fábrica" que diferencia as subclasses de Content
        RuntimeTypeAdapterFactory<Content> adapter = RuntimeTypeAdapterFactory
                .of(Content.class, "type") // O campo "type" no JSON dirá qual é a classe
                .registerSubtype(PlayerActionContent.class);

        // Crie o Gson com este adaptador
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(adapter)
                .create();

        this.gson = new GsonBuilder()
                // .registerTypeAdapterFactory(adapter) // Adicione a sua configuração aqui
                .create();
    }

    public Gson getGson() {
        return gson;
    }
}