package org.project.server.game;

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
            System.out.println("SESSÃO DE JOGO INICIADA: " + player1.getClientNumber() + " vs " + player2.getClientNumber());

            // Envia uma mensagem para cada jogador a informar que a partida começou
            player1.sendMessageToClient("MSG,Partida encontrada! Você é o Jogador 1 contra " + player2.getClientNumber());
            player2.sendMessageToClient("MSG,Partida encontrada! Você é o Jogador 2 contra " + player1.getClientNumber());

            // ** A LÓGICA DO JOGO (PEDRA, PAPEL, TESOURA) VIRÁ AQUI **
            // Por enquanto, vamos apenas terminar a sessão para teste.
            Thread.sleep(5000); // Simula uma partida de 5 segundos

            player1.sendMessageToClient("MSG,A partida terminou.");
            player2.sendMessageToClient("MSG,A partida terminou.");

        } catch (IOException | InterruptedException e) {
            System.out.println("Erro na sessão de jogo: " + e.getMessage());
        } finally {
            // Garante que os jogadores são desconectados no fim da partida
            player1.stop();
            player2.stop();
        }
    }
}