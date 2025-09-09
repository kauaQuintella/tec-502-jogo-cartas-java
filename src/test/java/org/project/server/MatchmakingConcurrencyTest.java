package org.project.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.project.client.gameClient.classes.User;
import org.project.client.utils.GsonSingleton;
import org.project.client.utils.message.MessageClient;
import org.project.client.utils.message.contents.CommandContent;
import org.project.client.utils.message.contents.LoginContent;
import org.project.server.game.RoomsManager;
import org.project.server.game.SkinsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class MatchmakingConcurrencyTest {

    private static Thread serverThread;
    private static TCPServerMain serverInstance;

    @BeforeAll
    public static void startServer() throws InterruptedException {
        // Garante um estado limpo para todos os Singletons antes de cada execução de teste
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
    public void testConcurrentMatchmaking() throws InterruptedException {
        // Número de clientes a simular (deve ser um número par)
        int numberOfClients = 50;
        ExecutorService clientExecutor = Executors.newFixedThreadPool(numberOfClients);
        // Latch para garantir que o teste só termine depois de todos os robôs terem sido pareados
        CountDownLatch matchmakingLatch = new CountDownLatch(numberOfClients);

        System.out.println("Iniciando " + numberOfClients + " clientes para teste de matchmaking...");

        for (int i = 0; i < numberOfClients; i++) {
            final String nickname = "MatcherBot_" + i;
            clientExecutor.submit(() -> {
                try (Socket socket = new Socket("localhost", 2020);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    // 1. LOGIN (usando o protocolo JSON correto)
                    in.readLine(); // Ignora a mensagem de boas-vindas
                    User user = new User(nickname, null);
                    MessageClient loginMessage = new MessageClient(nickname, new LoginContent(user));
                    out.println(GsonSingleton.INSTANCE.getGson().toJson(loginMessage));
                    in.readLine(); // Ignora a confirmação de login

                    // 2. ENVIA O COMANDO "JOGAR" (usando o protocolo JSON correto)
                    MessageClient playCommand = new MessageClient(nickname, new CommandContent("JOGAR"));
                    out.println(GsonSingleton.INSTANCE.getGson().toJson(playCommand));

                    // 3. ESPERA PELAS MENSAGENS DO SERVIDOR
                    in.readLine(); // Espera pela mensagem "Você está na fila..."
                    in.readLine(); // Espera pela mensagem "Partida encontrada..."

                    // 4. SINALIZA QUE ESTE ROBÔ FOI PAREADO COM SUCESSO
                    matchmakingLatch.countDown();

                } catch (Exception e) {
                    System.err.println("Erro no Cliente Robô " + nickname + ": " + e.getMessage());
                }
            });
        }

        // Aguarda que todos os clientes sejam pareados, com um tempo limite.
        boolean allMatched = matchmakingLatch.await(30, TimeUnit.SECONDS);
        clientExecutor.shutdownNow(); // Força o encerramento de todas as threads

        assertTrue(allMatched, "O teste de matchmaking excedeu o tempo limite. Nem todos os clientes foram pareados.");
        System.out.println("SUCESSO: Todos os " + numberOfClients + " clientes foram pareados com sucesso.");
    }
}