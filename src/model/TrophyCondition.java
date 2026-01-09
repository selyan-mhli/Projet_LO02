package model;

import java.io.Serializable;

/**
 * Interface fonctionnelle definissant la condition d obtention d un trophee.
 * 
 * <p>Cette interface utilise le concept de lambda Java 8+ pour permettre
 * une definition flexible des conditions de trophee.</p>
 * 
 * <h2>Exemples de conditions :</h2>
 * <ul>
 *   <li>Majorite d une couleur</li>
 *   <li>Plus haute/basse valeur d une couleur</li>
 *   <li>Possession du Joker</li>
 *   <li>Meilleur Jest total</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see TrophyCard
 * @see TrophyEffect
 */
@FunctionalInterface
public interface TrophyCondition extends Serializable {
    
    /**
     * Teste si un joueur remplit la condition pour obtenir le trophee.
     * 
     * @param player le joueur a tester
     * @param game   la partie en cours (pour comparer avec les autres joueurs)
     * @return true si le joueur remplit la condition, false sinon
     */
    boolean test(Player player, Game game);
}