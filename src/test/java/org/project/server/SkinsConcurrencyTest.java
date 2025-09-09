package org.project.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
// Imports do Cliente para simular um cliente real
import org.project.client.gameClient.classes.User;
import org.project.client.utils.GsonSingleton;
import org.project.client.utils.message.MessageClient;
import org.project.client.utils.message.contents.CommandContent;
import org.project.client.utils.message.contents.LoginContent;
import org.project.client.utils.message.contents.OpenPackResultContent;
import org.project.server.game.RoomsManager;
import org.project.server.game.SkinsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class SkinsConcurrencyTest {

    private static Thread serverThread;
    private static TCPServerMain serverInstance;

    @BeforeAll
    public static void startServer() throws InterruptedException {
        SkinsManager.resetInstanceForTesting();
        RoomsManager.resetInstanceForTesting();

        serverThread = new Thread(() -> {
            try {
                serverInstance = new TCPServerMain();
            } catch (Exception e) {
                if (!(e instanceof java.net.SocketException)) {
                    fail("O servidor falhou ao iniciar: " + e.getMessage());
                }
            }
        });
        serverThread.start();
        Thread.sleep(1500);
    }

    @AfterAll
    public static void stopServer() throws IOException {
        if (serverInstance != null) {
            serverInstance.stop();
        }
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    @Test
    public void testConcurrentSkinAcquisition() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        // --- AJUSTE CRÍTICO ---
        // Reduzimos o número de clientes para um valor mais razoável para um teste de unidade.
        // 100 clientes simultâneos ainda é um excelente teste de estresse para a lógica de concorrência.
        int numberOfClients = 200;
        assertTrue(SkinsManager.getInstance().getSkinsRestantes() >= numberOfClients, "O estoque de skins é insuficiente para o teste.");
        // --- FIM DO AJUSTE ---

        ExecutorService clientExecutor = Executors.newFixedThreadPool(numberOfClients);
        Set<String> skinsAdquiridas = ConcurrentHashMap.newKeySet();

        CountDownLatch readyLatch = new CountDownLatch(numberOfClients);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfClients);

        for (int i = 0; i < numberOfClients; i++) {
            final String nickname = "RoboTest_" + i;
            clientExecutor.submit(() -> {
                try (Socket socket = new Socket("localhost", 2020);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    // 1. LOGIN
                    in.readLine(); // Ignora boas-vindas
                    User user = new User(nickname, null);
                    MessageClient loginMessage = new MessageClient(nickname, new LoginContent(user));
                    out.println(GsonSingleton.INSTANCE.getGson().toJson(loginMessage));
                    in.readLine(); // Ignora confirmação de login

                    // 2. SINCRONIZAÇÃO
                    readyLatch.countDown();
                    startLatch.await();

                    // 3. COMANDO "ABRIR"
                    MessageClient openCommand = new MessageClient(nickname, new CommandContent("ABRIR"));
                    out.println(GsonSingleton.INSTANCE.getGson().toJson(openCommand));

                    // 4. LÊ E VALIDA A RESPOSTA
                    String responseJson = in.readLine();
                    assertNotNull(responseJson, "O servidor não respondeu ao comando ABRIR.");

                    MessageClient responseMessage = GsonSingleton.INSTANCE.getGson().fromJson(responseJson, MessageClient.class);
                    assertTrue(responseMessage.getContent() instanceof OpenPackResultContent, "A resposta não foi do tipo OpenPackResultContent.");

                    OpenPackResultContent resultContent = (OpenPackResultContent) responseMessage.getContent();
                    assertNotNull(resultContent.getSkinAdquirida(), "A skin recebida é nula.");

                    skinsAdquiridas.add(resultContent.getSkinAdquirida().getId());

                } catch (Exception e) {
                    fail("O Robô " + nickname + " falhou com uma exceção: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // --- AJUSTE CRÍTICO ---
        // Aumentamos o tempo de espera para dar tempo a todos os clientes de se prepararem.
        assertTrue(readyLatch.await(15, TimeUnit.SECONDS), "Nem todos os clientes ficaram prontos a tempo.");
        // --- FIM DO AJUSTE ---

        startLatch.countDown();
        assertTrue(doneLatch.await(1, TimeUnit.MINUTES), "O teste excedeu o tempo limite de execução.");
        clientExecutor.shutdown();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double throughput = (double) numberOfClients / (totalTime / 1000.0);

        // VALIDAÇÃO E RESULTADOS
        System.out.println("--- VALIDAÇÃO DE CORREÇÃO ---");
        System.out.println("Total de skins únicas adquiridas: " + skinsAdquiridas.size());
        assertEquals(numberOfClients, skinsAdquiridas.size(), "ERRO DE CONCORRÊNCIA: O número de skins únicas não é igual ao número de clientes. Skins foram duplicadas ou perdidas!");
        System.out.println("SUCESSO: Nenhuma skin foi duplicada.");

        System.out.println("\n--- RESULTADOS DE DESEMPENHO ---");
        System.out.printf("Vazão (Throughput): %.2f aberturas de pacote por segundo%n", throughput);
        System.out.println("---------------------------------");
    }
}