package view.gui;

import model.Card;
import model.Rank;
import model.Suits;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitaire charge de fournir les icones des cartes.
 */
public final class CardImages {

    private static final Map<String, ImageIcon> CACHE = new HashMap<>();
    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 120;

    private CardImages() {}

    /**
     * Retourne l'icone correspondant a la carte.
     *
     * @param card   carte concernee (peut etre null)
     * @param faceUp true si la carte doit etre visible, false pour le dos
     */
    public static ImageIcon get(Card card, boolean faceUp) {
        String key;
        if (!faceUp) {
            key = "BACK_CARD";
        } else if (card == null) {
            key = "BACK_CARD";
        } else if (card.getSuit() == Suits.JOKER || card.getRank() == Rank.JOKER || card.isJoker()) {
            key = "JOKER";
        } else {
            key = card.getSuit().name() + "_" + card.getRank().name();
        }
        return CACHE.computeIfAbsent(key, CardImages::loadIcon);
    }

    private static ImageIcon loadIcon(String key) {
        // D'abord, essayer dans le dossier principal des cartes
        URL url = CardImages.class.getResource("/CARD_IMAGES/" + key + ".png");

        // Si non trouvé, tenter dans le dossier des cartes d'extension (6-8)
        if (url == null) {
            url = CardImages.class.getResource("/CARD_EXTENSION/" + key + ".png");
        }

        if (url == null) {
            return null;
        }

        ImageIcon base = new ImageIcon(url);
        Image scaled = base.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
