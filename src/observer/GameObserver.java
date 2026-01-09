package observer;

import model.*;
import java.util.List;

/**
 * Interface Observer pour les vues du jeu Jest.
 * 
 * <p>Cette interface definit les methodes de notification que les vues
 * doivent implementer pour etre informees des changements du modele.</p>
 * 
 * <h2>Patron MVC :</h2>
 * <p>Cette interface fait partie de l implementation du patron MVC
 * avec deux vues concurrentes (Console et GUI).</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 2.0
 */
public interface GameObserver {
    
    /**
     * Notifie le debut d un nouveau tour.
     * 
     * @param roundNumber le numero du tour
     */
    void onRoundStart(int roundNumber);
    
    /**
     * Notifie que les offres ont ete creees.
     * 
     * @param offers la liste des offres
     */
    void onOffersCreated(List<Offer> offers);
    
    /**
     * Notifie qu une carte a ete prise.
     * 
     * @param player le joueur
     * @param card   la carte prise
     */
    void onCardTaken(Player player, Card card);
    
    /**
     * Notifie la fin de la partie.
     * 
     * @param winner le gagnant
     */
    void onGameEnd(Player winner);
    
    /**
     * Notifie un message general.
     * 
     * @param message le message
     */
    void onMessage(String message);
}