package org.project.server.game;

import org.project.server.utils.MessageServer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RoomsManager {
    private static final RoomsManager instance = new RoomsManager();
    private final BlockingQueue<PlayerThread> waitingQueue = new LinkedBlockingQueue<>(2); // Fila para 2 jogadores
    private MessageServer message;

    private RoomsManager() {}

    public static RoomsManager getInstance() {
        return instance;
    }

    // Metodo para um jogador entrar na fila
    public void joinWaitingQueue(PlayerThread player) {
        try {
            System.out.println("Jogador " + player.getClientNumber() + " entrou na fila.");

            // O metodo 'put' irá esperar se a fila estiver cheia, o que é bom para evitar problemas.
            waitingQueue.put(player);

            if (waitingQueue.size() == 2) {
                PlayerThread player1 = waitingQueue.take();
                PlayerThread player2 = waitingQueue.take();

                System.out.println("Pareando " + player1.getClientNumber() + " com " + player2.getClientNumber());

                // Iniciar uma nova thread de partida com esses dois jogadores
                GameSession gameSession = new GameSession(player1, player2);
                new Thread(gameSession).start(); // A mágica acontece aqui!
            }
        } catch (InterruptedException e) {
            // Boa prática: se a thread for interrompida, restaura o estado de interrupção
            Thread.currentThread().interrupt();
            System.err.println("A fila de espera foi interrompida.");
        }
    }
}
