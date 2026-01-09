package model;

import java.io.Serializable;

/**
 * Interface fonctionnelle definissant l effet d un trophee sur un joueur.
 * 
 * <p>Cette interface utilise le concept de lambda Java 8+ pour permettre
 * une definition flexible des effets de trophee.</p>
 * 
 * <h2>Exemples d effets :</h2>
 * <ul>
 *   <li>Ajouter des points bonus</li>
 *   <li>Modifier le score final</li>
 *   <li>Attribution d une carte supplementaire</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see TrophyCard
 * @see TrophyCondition
 */
@FunctionalInterface
public interface TrophyEffect extends Serializable {
    
    /**
     * Applique l effet du trophee au joueur gagnant.
     * 
     * @param player le joueur ayant gagne le trophee
     * @param game   la partie en cours
     */
    void apply(Player player, Game game);
}