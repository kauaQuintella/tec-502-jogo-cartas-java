package org.project.server.game;

import org.project.server.game.classes.Skin;
import org.project.server.game.classes.User;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SkinsManager {
    private static final SkinsManager instance = new SkinsManager();
    private final List<Skin> skinsDisponiveis = new LinkedList<>();

    private SkinsManager() {
        // Inicializa o "estoque" com algumas skins.
        // Skins Raras
        for (int i = 0; i < 50; i++) {
            skinsDisponiveis.add(new Skin("FOGO_R", "Fogo Infernal", "Raro"));
            skinsDisponiveis.add(new Skin("AGUA_R", "Tsunami", "Raro"));
            skinsDisponiveis.add(new Skin("NATUREZA_R", "Avatar da Floresta", "Raro"));
        }
        // Skins Comuns
        for (int i = 0; i < 140; i++) {
            skinsDisponiveis.add(new Skin("FOGO_C" + i, "Chama Simples", "Comum"));
            skinsDisponiveis.add(new Skin("AGUA_C" + i, "Gota de Orvalho", "Comum"));
            skinsDisponiveis.add(new Skin("NATUREZA_C" + i, "Folha Verdejante", "Comum"));
        }
        for (int i = 0; i < 10; i++) {
            skinsDisponiveis.add(new Skin("FOGO_L", "Hades", "Lendário"));
            skinsDisponiveis.add(new Skin("AGUA_L", "Neptuno", "Lendário"));
            skinsDisponiveis.add(new Skin("NATUREZA_L", "Gaia", "Lendário"));
        }

        // Embaralha o estoque para que a ordem seja aleatória
        Collections.shuffle(skinsDisponiveis);
        System.out.println("SkinsManager inicializado com " + skinsDisponiveis.size() + " skins.");
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
}