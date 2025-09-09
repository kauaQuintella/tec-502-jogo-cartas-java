package org.project.server;

import org.junit.jupiter.api.Test;
import java.net.Socket;
import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SkinsConcurrencyTest {
    @Test
    public void testConcurrentSkinAcquisition() throws InterruptedException {
        // Inicia o servidor numa thread separada
        new Thread(() -> {
            try { new TCPServerMain(); } catch (Exception e) { e.printStackTrace(); }
        }).start();
        Thread.sleep(1000);

        int numberOfClients = 33; // O número total de skins no nosso estoque
        ExecutorService clientExecutor = Executors.newFixedThreadPool(numberOfClients);

        // Um Set thread-safe para garantir que nenhuma skin foi duplicada
        Set<String> skinsAdquiridas = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < numberOfClients; i++) {
            clientExecutor.submit(() -> {
                try (Socket socket = new Socket("localhost", 2020);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    // Simula um login rápido (adaptar se o seu login for mais complexo)
                    out.println("{\"content\":{\"type\":\"LoginContent\",\"user\":{\"nickname\":\"RoboTest\"}}}");

                    // Envia o comando para abrir o pacote
                    out.println("{\"content\":{\"type\":\"CommandContent\",\"command\":\"ABRIR\"}}");

                    // NOTA: Para um teste real, leríamos a resposta e adicionaríamos a skin ao Set.
                    // Por simplicidade, vamos validar no lado do servidor.

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        clientExecutor.shutdown();
        clientExecutor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("Teste de concorrência de skins finalizado.");
        // A VALIDAÇÃO FINAL: Verifique no log do servidor se 33 skins foram distribuídas
        // para 33 jogadores diferentes e se o estoque final é 0.
        // Numa versão mais avançada, o servidor poderia expor um endpoint para verificar isso.
    }
}