package observer;

/**
 * Interface Observable pour le modele du jeu Jest.
 * 
 * <p>Cette interface definit les methodes pour gerer les observateurs
 * (vues) qui seront notifies des changements du modele.</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 2.0
 */
public interface GameObservable {
    
    /**
     * Ajoute un observateur.
     * 
     * @param observer l observateur a ajouter
     */
    void addObserver(GameObserver observer);
    
    /**
     * Retire un observateur.
     * 
     * @param observer l observateur a retirer
     */
    void removeObserver(GameObserver observer);
    
    /**
     * Notifie tous les observateurs.
     */
    void notifyObservers();
}