package org.project.server.game;

import org.project.server.utils.message.contents.PingContent;
import org.project.server.game.classes.Skin;
import org.project.server.game.classes.User;
import org.project.server.utils.GsonSingleton;
import org.project.server.utils.message.MessageServer;
import org.project.server.utils.message.contents.CommandContent;
import org.project.server.utils.message.contents.Content;
import org.project.server.utils.message.contents.LoginContent;
import org.project.server.utils.message.contents.OpenPackResultContent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;

public class PlayerThread implements Runnable {
    private final Socket socket;
    private User user; // O User será definido após o login
    private final BufferedReader in;
    private final PrintWriter out;
    private int clientNumber;

    public PlayerThread(Socket socket) throws IOException {
        this.socket = socket;
        // IMPORTANTE: Nenhum I/O de rede aqui. Apenas inicialização.
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        UUID uuid = UUID.randomUUID();
        clientNumber = uuid.toString().hashCode();
    }

    public String getClientNumber () {return clientNumber+"";}
    // Getters para a GameSession usar
    public User getUser() { return user; }

    @Override
    public void run() {
        try {
            // ETAPA 1: Boas-vindas e Pedido de Login
            sendMessage(new CommandContent("Bem-vindo! Por favor, envie os seus dados de login."));

            // ETAPA 2: Aguardar e Processar a Mensagem de Login
            MessageServer loginMsg = receiveMessage();
            Content receivedContent = loginMsg.getContent();

            if (receivedContent instanceof LoginContent) {
                LoginContent loginData = (LoginContent) receivedContent;
                this.user = loginData.getUser(); // Associa o User enviado pelo cliente a esta Thread
                System.out.println("Jogador '" + user.getNickname() + "' fez login com sucesso.");
                sendMessage(new CommandContent("Login bem-sucedido! Digite 'JOGAR' para encontrar uma partida, 'ABRIR' para obter uma skin ou 'PING' para verificar a latência do servidor."));
            } else {
                // Se a primeira mensagem não for de login, é um erro de protocolo.
                sendMessage(new CommandContent("ERRO: A primeira mensagem deve ser de login."));
                stop();
                return; // Termina a thread
            }

            // ETAPA 3: Loop de Comandos Pós-Login
            while (socket.isConnected() && !socket.isClosed()) {
                MessageServer clientMsg = receiveMessage();
                Content commandContent = clientMsg.getContent();

                if (commandContent instanceof CommandContent) {
                    CommandContent command = (CommandContent) commandContent;
                    if ("JOGAR".equalsIgnoreCase(command.getCommand())) {
                        sendMessage(new CommandContent("Você está na fila. Aguardando oponente..."));
                        RoomsManager.getInstance().joinWaitingQueue(this);
                        // A thread vai agora ser controlada pela GameSession.
                        // Podemos quebrar o loop aqui, pois a GameSession assume o controlo.
                        break;
                    } else if ("ABRIR".equalsIgnoreCase(command.getCommand())) {
                        Optional<Skin> resultado = SkinsManager.getInstance().abrirPacote(this.user);

                        if (resultado.isPresent()) {
                            sendMessage(new OpenPackResultContent(resultado.get(), "Parabéns! Você adquiriu uma nova skin!"));
                        } else {
                            sendMessage(new OpenPackResultContent(null, "Que pena! Não há mais skins disponíveis no estoque."));
                        }
                    }
                } else if (commandContent instanceof PingContent) {
                // Se recebermos um Ping, simplesmente o devolvemos como um Pong.
                sendMessage(commandContent);
            }
            }

        } catch (IOException e) {
            String nickname = (user != null) ? user.getNickname() : "desconhecido (" + getClientNumber() + ")";
            System.out.println("Jogador " + nickname + " desconectou-se durante o lobby.");
            // NOTA: Se o jogador se desconectar aqui, precisamos de uma forma de o remover da fila
            // se ele já tiver entrado. Para já, esta mensagem é suficiente.
        }
    }

    // --- MÉTODOS DE AJUDA PARA COMUNICAÇÃO ---
    public void sendMessage(Content content) {
        MessageServer message = new MessageServer(this.user != null ? this.user.getNickname() : "Servidor", content);
        String jsonMessage = GsonSingleton.INSTANCE.getGson().toJson(message);
        out.println(jsonMessage);
    }

    public MessageServer receiveMessage() throws IOException {
        String jsonMessage = in.readLine();
        if (jsonMessage == null) {
            throw new IOException("O cliente fechou a conexão.");
        }
        return GsonSingleton.INSTANCE.getGson().fromJson(jsonMessage, MessageServer.class);
    }

    public void stop() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // Ignorar erros ao fechar, pois já estamos a encerrar
        }
    }
}