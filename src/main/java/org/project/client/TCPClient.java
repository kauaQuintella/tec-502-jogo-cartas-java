package org.project.client;

import org.project.client.gameClient.classes.User;
import org.project.client.utils.GsonSingleton;
import org.project.client.utils.message.MessageClient;
import org.project.client.utils.message.contents.*;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    public TCPClient() throws IOException {
        Socket socket = new Socket("localhost", 2020);
        System.out.println("Conectado ao servidor.");

        // A thread principal envia os inputs do utilizador
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // --- NOVO FLUXO DE LOGIN ---
        // 1. Ouve a mensagem de boas-vindas do servidor
        String welcomeJson = in.readLine();
        // (Opcional: Desserializar e mostrar a mensagem)
        System.out.println("Servidor: " + welcomeJson);

        // 2. Pede o nickname ao utilizador
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Digite o seu nickname para o login: ");
        String nickname = keyboard.nextLine();

        // 3. Cria e envia a mensagem de Login
        User user = new User(nickname, null); // O inventário pode ser nulo por agora
        LoginContent loginContent = new LoginContent(user);
        MessageClient loginMessage = new MessageClient(nickname, loginContent);
        String loginJson = GsonSingleton.INSTANCE.getGson().toJson(loginMessage);
        out.println(loginJson);

        // --- FIM DO FLUXO DE LOGIN ---

        // Agora, inicia a thread para ouvir o servidor em segundo plano
        new Thread(new ServerListener(socket)).start();

        // Loop para enviar comandos pós-login
        while (true) {
            String commandText = keyboard.nextLine().toUpperCase(); // Converte para maiúsculas para facilitar

            // Se o comando for uma das jogadas válidas, envia como PlayerActionContent
            if (commandText.equals("FOGO") || commandText.equals("AGUA") || commandText.equals("NATUREZA")) {
                PlayerActionContent actionContent = new PlayerActionContent(commandText);
                MessageClient actionMessage = new MessageClient(nickname, actionContent);
                String actionJson = GsonSingleton.INSTANCE.getGson().toJson(actionMessage);
                out.println(actionJson);
            } else {
                // Para outros comandos como "JOGAR", envia como CommandContent
                CommandContent commandContent = new CommandContent(commandText);
                MessageClient commandMessage = new MessageClient(nickname, commandContent);
                String commandJson = GsonSingleton.INSTANCE.getGson().toJson(commandMessage);
                out.println(commandJson);
            }
        }
    }

    private static class ServerListener implements Runnable {
        private final Socket socket;

        public ServerListener(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String serverJson;
                while ((serverJson = in.readLine()) != null) {
                    // ETAPA 1: Desserializar o JSON para um objeto MessageClient
                    MessageClient receivedMessage = GsonSingleton.INSTANCE.getGson().fromJson(serverJson, MessageClient.class);

                    // ETAPA 2: Interpretar o conteúdo da mensagem
                    Content content = receivedMessage.getContent();
                    String sender = receivedMessage.getSender();

                    // ETAPA 3: Formatar e exibir a mensagem de forma amigável
                    if (content instanceof CommandContent) {
                        CommandContent command = (CommandContent) content;
                        System.out.println("[SISTEMA]: " + command.getCommand());

                    } else if (content instanceof PlayerActionContent) {
                        PlayerActionContent action = (PlayerActionContent) content;
                        System.out.println("[" + sender + "]: jogou " + action.getAction());

                    } else if (content instanceof LoginContent) {
                        // Embora o cliente não deva receber um LoginContent, é uma boa prática estar preparado
                        System.out.println("[SISTEMA]: Informação de login recebida.");

                    } else if (content instanceof OpenPackResultContent) {
                        OpenPackResultContent resultado = (OpenPackResultContent) content;
                        if (resultado.getSkinAdquirida() != null) {
                            System.out.println("[LOJA]: " + resultado.getMensagem() + " -> " + resultado.getSkinAdquirida().getNome() + " (" + resultado.getSkinAdquirida().getRaridade() + ")");
                        } else {
                            System.out.println("[LOJA]: " + resultado.getMensagem());
                        }
                    } else {
                        // Se for um tipo de conteúdo desconhecido, imprima o JSON para depuração
                        System.out.println("[DEBUG]: " + serverJson);
                    }
                }
            } catch (IOException e) {
                System.out.println("Desconectado do servidor.");
            }
        }
    }

    public static void main(String[] args) {
        try {
            new TCPClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}