package org.project.server.game;

import org.project.server.game.classes.Skin;
import org.project.server.game.classes.User;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SkinsManager {
    private static SkinsManager instance = new SkinsManager();
    private final List<Skin> skinsDisponiveis = new LinkedList<>();

    public SkinsManager() {
        // Inicializa o "estoque" com skins de ID único.

        // Skins Raras - 50 de cada
        for (int i = 0; i < 50; i++) {
            skinsDisponiveis.add(new Skin("FOGO_R_" + i, "Fogo Infernal", "Raro"));
            skinsDisponiveis.add(new Skin("AGUA_R_" + i, "Tsunami", "Raro"));
            skinsDisponiveis.add(new Skin("NATUREZA_R_" + i, "Avatar da Floresta", "Raro"));
        }

        // Skins Comuns - 140 de cada
        for (int i = 0; i < 140; i++) {
            skinsDisponiveis.add(new Skin("FOGO_C_" + i, "Chama Simples", "Comum"));
            skinsDisponiveis.add(new Skin("AGUA_C_" + i, "Gota de Orvalho", "Comum"));
            skinsDisponiveis.add(new Skin("NATUREZA_C_" + i, "Folha Verdejante", "Comum"));
        }

        // Skins Lendárias - 10 de cada
        for (int i = 0; i < 10; i++) {
            skinsDisponiveis.add(new Skin("FOGO_L_" + i, "Hades", "Lendário"));
            skinsDisponiveis.add(new Skin("AGUA_L_" + i, "Neptuno", "Lendário"));
            skinsDisponiveis.add(new Skin("NATUREZA_L_" + i, "Gaia", "Lendário"));
        }

        // Embaralha o estoque para que a ordem seja aleatória
        Collections.shuffle(skinsDisponiveis);
        System.out.println("SkinsManager inicializado com " + skinsDisponiveis.size() + " skins únicas.");
    }

    public static SkinsManager getInstance() {
        return instance;
    }

    // ESTE É O MÉTODO MAIS IMPORTANTE DO PROJETO
    // A palavra-chave 'synchronized' garante que apenas UMA thread (jogador)
    // possa executar este código de cada vez, prevenindo a duplicação ou perda de skins.
    public synchronized Optional<Skin> abrirPacote(User user) {
        if (skinsDisponiveis.isEmpty()) {
            return Optional.empty(); // Retorna um "vazio" se não houver mais skins
        }

        // Remove a primeira skin da lista (que foi previamente embaralhada)
        Skin skinAdquirida = skinsDisponiveis.removeFirst();

        // Adiciona a skin ao inventário do jogador
        user.getInventory().addSkin(skinAdquirida);

        System.out.println("O jogador " + user.getNickname() + " adquiriu a skin: " + skinAdquirida.getNome());
        return Optional.of(skinAdquirida);
    }

    public synchronized int getSkinsRestantes() {
        return skinsDisponiveis.size();
    }

    public static void resetInstanceForTesting() {
        System.out.println("!!! RESETANDO O SKINS MANAGER PARA TESTES !!!");
        instance = new SkinsManager();
    }
}