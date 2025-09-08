package org.project.server.game;

import org.project.server.utils.message.MessageServer;
import org.project.server.utils.message.contents.CommandContent;
import org.project.server.utils.message.contents.PlayerActionContent;

import java.io.IOException;

public class GameSession implements Runnable {
    private final PlayerThread player1;
    private final PlayerThread player2;

    // --- CONSTANTES DO JOGO ---
    private static final String FOGO = "FOGO";
    private static final String AGUA = "AGUA";
    private static final String NATUREZA = "NATUREZA";
    // --- FIM DAS CONSTANTES ---

    public GameSession(PlayerThread player1, PlayerThread player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    public void run() {
        try {
            String p1_nickname = player1.getUser().getNickname();
            String p2_nickname = player2.getUser().getNickname();

            System.out.println("SESSÃO DE JOGO INICIADA: " + p1_nickname + " vs " + p2_nickname);
            player1.sendMessage(new CommandContent("Partida encontrada! O seu oponente é: " + p2_nickname));
            player2.sendMessage(new CommandContent("Partida encontrada! O seu oponente é: " + p1_nickname));

            int scoreP1 = 0;
            int scoreP2 = 0;
            int roundsToWin = 2; // Melhor de 3 (precisa de 2 vitórias)

            for (int i = 1; scoreP1 < roundsToWin && scoreP2 < roundsToWin; i++) {
                // 1. ANUNCIA A RONDA E PEDE A JOGADA
                CommandContent prompt = new CommandContent("RONDA " + i + " - Placar: " + p1_nickname + " " + scoreP1 + " X " + scoreP2 + " " + p2_nickname + ". Escolha FOGO, AGUA, ou NATUREZA.");
                player1.sendMessage(prompt);
                player2.sendMessage(prompt);

                // 2. RECEBE AS RESPOSTAS DOS DOIS JOGADORES
                MessageServer msgP1 = player1.receiveMessage();
                MessageServer msgP2 = player2.receiveMessage();

                // Validação básica para garantir que a mensagem é uma ação de jogo
                if (!(msgP1.getContent() instanceof PlayerActionContent) || !(msgP2.getContent() instanceof PlayerActionContent)) {
                    player1.sendMessage(new CommandContent("Erro de comunicação. A partida será encerrada."));
                    player2.sendMessage(new CommandContent("Erro de comunicação. A partida será encerrada."));
                    break; // Encerra o loop do jogo
                }

                PlayerActionContent actionP1 = (PlayerActionContent) msgP1.getContent();
                PlayerActionContent actionP2 = (PlayerActionContent) msgP2.getContent();
                String jogadaP1 = actionP1.getAction();
                String jogadaP2 = actionP2.getAction();

                // 3. DETERMINA O VENCEDOR DA RONDA
                PlayerThread vencedorRonda = determinarVencedor(jogadaP1, jogadaP2);

                // 4. ANUNCIA O RESULTADO DA RONDA E ATUALIZA O PLACAR
                String resultadoMsg;
                if (vencedorRonda == null) {
                    resultadoMsg = "RONDA " + i + ": Empate! Ambos escolheram " + jogadaP1 + ".";
                } else if (vencedorRonda == player1) {
                    scoreP1++;
                    resultadoMsg = "RONDA " + i + ": " + p1_nickname + " venceu! (" + jogadaP1 + " ganha de " + jogadaP2 + ").";
                } else {
                    scoreP2++;
                    resultadoMsg = "RONDA " + i + ": " + p2_nickname + " venceu! (" + jogadaP2 + " ganha de " + jogadaP1 + ").";
                }
                player1.sendMessage(new CommandContent(resultadoMsg));
                player2.sendMessage(new CommandContent(resultadoMsg));
                Thread.sleep(1000); // Pequena pausa para os jogadores lerem
            }

            // 5. ANUNCIA O VENCEDOR FINAL DA PARTIDA
            String msgFinal;
            if (scoreP1 > scoreP2) {
                msgFinal = "Fim de jogo! " + p1_nickname + " venceu a partida por " + scoreP1 + " a " + scoreP2 + "!";
            } else {
                msgFinal = "Fim de jogo! " + p2_nickname + " venceu a partida por " + scoreP2 + " a " + scoreP1 + "!";
            }
            player1.sendMessage(new CommandContent(msgFinal));
            player2.sendMessage(new CommandContent(msgFinal));

        } catch (IOException | InterruptedException e) {
            System.out.println("Erro na sessão de jogo: " + e.getMessage());
            // Informar o outro jogador em caso de desconexão
        } finally {
            // Encerra a conexão de ambos os jogadores no final da partida
            System.out.println("Encerrando sessão de jogo.");
            player1.stop();
            player2.stop();
        }
    }

    private PlayerThread determinarVencedor(String jogadaP1, String jogadaP2) {
        if (jogadaP1.equalsIgnoreCase(jogadaP2)) {
            return null; // Empate
        }

        switch (jogadaP1.toUpperCase()) {
            case FOGO:
                return jogadaP2.equalsIgnoreCase(NATUREZA) ? player1 : player2;
            case AGUA:
                return jogadaP2.equalsIgnoreCase(FOGO) ? player1 : player2;
            case NATUREZA:
                return jogadaP2.equalsIgnoreCase(AGUA) ? player1 : player2;
            default:
                return null; // Caso alguma jogada seja inválida
        }
    }
}