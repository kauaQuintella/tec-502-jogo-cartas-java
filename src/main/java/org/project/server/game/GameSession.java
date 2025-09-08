package org.project.server.game;

import org.project.server.utils.message.MessageServer;
import org.project.server.utils.message.contents.CommandContent;
import org.project.server.utils.message.contents.PlayerActionContent;

import java.io.IOException;

public class GameSession implements Runnable {
    private final PlayerThread player1;
    private final PlayerThread player2;

    public GameSession(PlayerThread player1, PlayerThread player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    public void run() {
        try {
            // ... (mensagem de início de partida)

            // --- LÓGICA DO JOGO "CONFLITO ELEMENTAL" ---
            int scoreP1 = 0;
            int scoreP2 = 0;
            int rounds = 3; // Melhor de 3

            for (int i = 1; i <= rounds; i++) {
                // 1. Pede a jogada a ambos os jogadores
                player1.sendMessage(new CommandContent("RONDA " + i + ": Escolha FOGO, AGUA, ou NATUREZA."));
                player2.sendMessage(new CommandContent("RONDA " + i + ": Escolha FOGO, AGUA, ou NATUREZA."));

                // 2. Recebe as respostas
                MessageServer msgP1 = player1.receiveMessage();
                MessageServer msgP2 = player2.receiveMessage();

                PlayerActionContent actionP1 = (PlayerActionContent) msgP1.getContent();
                PlayerActionContent actionP2 = (PlayerActionContent) msgP2.getContent();

                String jogadaP1 = actionP1.getAction();
                String jogadaP2 = actionP2.getAction();

                // 3. Determina o vencedor da ronda (implementar a lógica Fogo > Natureza > Agua)
                String resultadoRonda = determinarVencedor(jogadaP1, jogadaP2);

                // 4. Anuncia o resultado e atualiza o placar
                // (implementar envio de mensagens com o resultado)
            }

            // ... (lógica de fim de partida)

        } catch (IOException e) {
            // ...
        } finally {
            // ...
        }
    }

    private String determinarVencedor(String p1, String p2) {
        // Implementar a lógica de Pedra, Papel e Tesoura aqui
        return "Resultado...";
    }
}