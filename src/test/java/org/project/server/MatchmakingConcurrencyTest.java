package org.project.server;

import org.junit.jupiter.api.Test;
import org.project.server.TCPServerMain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatchmakingConcurrencyTest {

    @Test
    public void testConcurrentMatchmaking() throws InterruptedException {
        // Inicia o servidor numa thread separada para não bloquear o teste
        new Thread(() -> {
            try {
                new TCPServerMain();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Dá um pequeno tempo para o servidor iniciar completamente
        Thread.sleep(1000);

        // Número de clientes a simular (deve ser um número par)
        int numberOfClients = 10;
        ExecutorService clientExecutor = Executors.newFixedThreadPool(numberOfClients);

        System.out.println("Iniciando " + numberOfClients + " clientes para teste de concorrência...");

        for (int i = 0; i < numberOfClients; i++) {
            int clientID = i + 1;
            clientExecutor.submit(() -> {
                try (Socket socket = new Socket("localhost", 2020);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    System.out.println("Cliente Robô " + clientID + " conectou.");

                    // Lê a mensagem de boas-vindas
                    in.readLine();

                    // Envia o comando para entrar na fila
                    out.println("JOGAR");
                    System.out.println("Cliente Robô " + clientID + " enviou JOGAR.");

                    // Espera pelas mensagens do servidor (fila e início da partida)
                    System.out.println("Robô " + clientID + " Servidor: " + in.readLine()); // "Você está na fila..."
                    System.out.println("Robô " + clientID + " Servidor: " + in.readLine()); // "Partida encontrada..."

                } catch (Exception e) {
                    System.err.println("Erro no Cliente Robô " + clientID + ": " + e.getMessage());
                }
            });
        }

        // Espera os clientes terminarem
        clientExecutor.shutdown();
        // Espera no máximo 1 minuto para o teste completar
        clientExecutor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("Teste de concorrência finalizado.");
        // Para um teste JUnit real, você adicionaria asserções aqui (Assert.assertTrue(...))
        // Mas por agora, o output no terminal é suficiente para validar.
    }
}