package model;

import java.io.Serializable;

/**
 * Interface Visitor pour le calcul du score des cartes et des Jests.
 * 
 * <p>Cette interface implemente le patron de conception Visitor permettant
 * de separer l algorithme de calcul de score des classes de cartes.</p>
 * 
 * <h2>Avantages du patron Visitor :</h2>
 * <ul>
 *   <li>Ajout facile de nouvelles regles de calcul</li>
 *   <li>Separation des responsabilites</li>
 *   <li>Possibilite de variantes (Rule1, Rule2, Rule3)</li>
 * </ul>
 * 
 * <h2>Implementations disponibles :</h2>
 * <ul>
 *   <li>{@link model.rules.Rule1} - Regles officielles</li>
 *   <li>{@link model.rules.Rule2} - Variante trophees inverses</li>
 *   <li>{@link model.rules.Rule3} - Variante couleurs inversees</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Card
 * @see Jest
 */
public interface ScoreVisitor extends Serializable {
    
    /**
     * Calcule le score total d un Jest.
     * 
     * <p>Cette methode prend en compte toutes les cartes du Jest
     * ainsi que les bonus/malus speciaux (paires noires, Joker, As isole).</p>
     * 
     * @param jest le Jest a evaluer
     * @return le score total du Jest
     */
    int score(Jest jest);
    
    /**
     * Calcule le score d une carte classique.
     * 
     * @param card la carte a evaluer
     * @return le score de la carte selon sa couleur
     */
    int score(SuitCard card);
    
    /**
     * Calcule le score du Joker.
     * 
     * <p>Note : Le score reel du Joker depend du contexte (nombre de Coeurs)
     * et est generalement calcule dans la methode score(Jest).</p>
     * 
     * @param card le Joker
     * @return 0 (le score est calcule au niveau du Jest)
     */
    int score(JokerCard card);
}