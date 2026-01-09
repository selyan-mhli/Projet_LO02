package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe representant le Jest d un joueur (collection de cartes gagnees).
 * 
 * <p>Le Jest est l ensemble des cartes qu un joueur a collectees pendant la partie.
 * C est sur cette collection que le score final est calcule.</p>
 * 
 * <h2>Contenu d un Jest :</h2>
 * <ul>
 *   <li>Cartes classiques (SuitCard) prises des offres</li>
 *   <li>Le Joker (si le joueur l a obtenu)</li>
 *   <li>Cartes trophees gagnees en fin de partie</li>
 * </ul>
 * 
 * <h2>Methodes de comptage :</h2>
 * <ul>
 *   <li>{@link #countSuit(Suits)} - Compte les cartes d une couleur</li>
 *   <li>{@link #countRank(Rank)} - Compte les cartes d un rang</li>
 *   <li>{@link #hasJoker()} - Verifie la presence du Joker</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Player
 * @see Card
 */
public class Jest implements Serializable {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /** Liste des cartes dans le Jest. */
    private List<Card> cards = new ArrayList<>();

    /**
     * Ajoute une carte au Jest.
     * 
     * @param card la carte a ajouter
     */
    public void addCard(Card card) { 
        cards.add(card); 
    }
    
    /**
     * Ajoute un trophee au Jest.
     * 
     * @param trophy le trophee a ajouter
     */
    public void addTrophy(TrophyCard trophy) { 
        cards.add(trophy); 
    }

    /**
     * Retourne une copie de la liste des cartes.
     * 
     * @return une nouvelle liste contenant toutes les cartes du Jest
     */
    public List<Card> getCards() { 
        return new ArrayList<>(cards); 
    }

    /**
     * Accepte un visiteur pour le calcul du score.
     * 
     * @param visitor le visiteur calculant le score
     * @return le score total du Jest
     */
    public int accept(ScoreVisitor visitor) {
        return visitor.score(this);
    }

    /**
     * Compte le nombre de cartes d une couleur donnee.
     * 
     * @param s la couleur a compter
     * @return le nombre de cartes de cette couleur (hors trophees)
     */
    public int countSuit(Suits s) {
        return (int) cards.stream()
                .filter(c -> !(c instanceof TrophyCard))
                .filter(c -> c.getSuit() == s)
                .count();
    }

    /**
     * Compte le nombre de cartes d un rang donne.
     * 
     * @param r le rang a compter
     * @return le nombre de cartes de ce rang (hors trophees)
     */
    public int countRank(Rank r) {
        return (int) cards.stream()
                .filter(c -> !(c instanceof TrophyCard))
                .filter(c -> c.getRank() == r)
                .count();
    }

    /**
     * Verifie si le Jest contient le Joker.
     * 
     * @return true si le Joker est present, false sinon
     */
    public boolean hasJoker() {
        return cards.stream().anyMatch(Card::isJoker);
    }
    
    /**
     * Calcule la somme des valeurs des cartes d une couleur.
     * 
     * @param suit la couleur a calculer
     * @return la somme des valeurs faciales
     */
    public int getSuitValue(Suits suit) {
        return cards.stream()
                .filter(c -> !(c instanceof TrophyCard))
                .filter(c -> c.getSuit() == suit)
                .mapToInt(c -> c.getRank().getValue())
                .sum();
    }

    /**
     * Retourne une representation textuelle du Jest.
     * 
     * @return la liste des cartes sous forme de chaine
     */
    @Override
    public String toString() {
        return cards.toString();
    }
}