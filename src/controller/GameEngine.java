package controller;

import model.*;
import model.extension.BMCard;
import observer.GameObserver;
import view.ConsoleView;
import view.gui.JestGUI;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controleur principal du jeu Jest (MVC).
 * 
 * <p>Cette classe orchestre le deroulement de la partie en coordonnant
 * le modele (Game) et les vues (ConsoleView, JestGUI).</p>
 * 
 * <h2>Responsabilites :</h2>
 * <ul>
 *   <li>Initialisation de la partie</li>
 *   <li>Gestion des tours de jeu</li>
 *   <li>Coordination des deux vues concurrentes</li>
 *   <li>Sauvegarde et chargement de parties</li>
 * </ul>
 * 
 * <h2>Architecture MVC :</h2>
 * <p>Le GameEngine notifie les deux vues (Console et GUI) simultanement
 * via le patron Observer. Les deux vues restent synchronisees.</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 */
public class GameEngine {

    private Game game;
    private ConsoleView consoleView;
    private JestGUI guiView;
    private List<Offer> currentOffers;
    private List<GameObserver> observers;
    private boolean useGui;

    /**
     * Constructeur du moteur de jeu.
     */
    public GameEngine() {
        this.consoleView = new ConsoleView();
        this.currentOffers = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.useGui = false;
        
        observers.add(consoleView);
    }

    /**
     * Active l interface graphique.
     */
    public void enableGUI() {
        this.guiView = new JestGUI();
        this.useGui = true;
        observers.add(guiView);

        // Connecte la vue console a la GUI pour des prompts synchronises
        consoleView.setGuiHelper(guiView);
    }

    /**
     * Initialise une nouvelle partie.
     */
    public void initializeGame() {
        
        game = new Game();

        consoleView.displayWelcome();

        int numPlayers = (useGui && guiView != null)
                ? guiView.askNumberOfPlayers()
                : consoleView.askNumberOfPlayers();

        for (int i = 1; i <= numPlayers; i++) {
            Player player = (useGui && guiView != null)
                    ? guiView.askPlayerType(i)
                    : consoleView.askPlayerType(i);
            game.addPlayer(player);
        }

        RuleSet ruleSet = (useGui && guiView != null)
                ? guiView.askRuleSet()
                : consoleView.askRuleSet();
        game.setRuleSet(ruleSet);

        // Extensions
        boolean[] extensions = (useGui && guiView != null)
                ? guiView.askExtensions()
                : consoleView.askExtensions();
        game.setExtensionBM(extensions[0]);
        game.setExtensionCards(extensions[1]);
        
        game.getDeck().initialize(extensions[1]);

        game.start();
        game.displayAvailableTrophies();
        
        // Afficher les trophées dans l'historique
        notifyMessage("=== Trophées pour cette partie ===");
        if (game.getTrophies() == null || game.getTrophies().isEmpty()) {
            notifyMessage("Aucun trophée pour cette variante.");
        } else {
            for (TrophyCard trophy : game.getTrophies()) {
                notifyMessage("Trophée " + trophy.getName());
            }
        }
        
        if (guiView != null) {
            guiView.setGame(game);
            guiView.displayTrophies(game.getTrophies());
            guiView.displayTrophiesInState(game.getTrophies());
        }
    }

    /**
     * Lance la partie.
     */
    public void playGame() {
        while (!game.isGameOver()) {
            playRound();
        }
        endGame();
    }

    /**
     * Joue un tour.
     */
    private void playRound() {
        notifyRoundStart(game.getCurrentRound());
        
        game.dealRound();

        currentOffers.clear();
        for (Player player : game.getPlayers()) {
            Offer offer = createOffer(player);
            currentOffers.add(offer);
        }

        notifyOffersCreated(currentOffers);

        List<Player> turnOrder = determineTurnOrder();

        for (Player player : turnOrder) {
            Card chosenCard = playerChooseCard(player);
            player.getJest().addCard(chosenCard);
            notifyCardTaken(player, chosenCard);
        }

        for (Player player : game.getPlayers()) {
            player.clearHand();
        }

        game.endTurn();
    }

    /**
     * Cree l offre d un joueur.
     */
    private Offer createOffer(Player player) {
        Offer offer = player.makeOffer(game);
        
        if (offer == null) {
            if (useGui && guiView != null) {
                return guiView.askHumanOffer(player);
            }
            return consoleView.askHumanOffer(player);
        }
        
        return offer;
    }

    /**
     * Gere le choix de carte d un joueur.
     */
    private Card playerChooseCard(Player player) {
        List<Offer> availableOffers = currentOffers.stream()
                .filter(Offer::isComplete)
                .filter(o -> o.getOwner() != player)
                .toList();

        if (availableOffers.isEmpty()) {
            Offer ownOffer = currentOffers.stream()
                    .filter(o -> o.getOwner() == player)
                    .findFirst()
                    .orElse(null);

            if (ownOffer != null && ownOffer.isComplete()) {
                if (player.isHuman() && useGui && guiView != null) {
                    return guiView.askTakeFromOwnOffer(player, ownOffer, game);
                }
                return consoleView.askTakeFromOwnOffer(player, ownOffer, game);
            }
        }

        Card chosen = player.chooseFrom(availableOffers, game);
        
        if (chosen == null) {
            if (useGui && guiView != null) {
                return guiView.askHumanTake(player, availableOffers, game);
            }
            return consoleView.askHumanTake(player, availableOffers, game);
        }
        
        return chosen;
    }

    /**
     * Determine l ordre de jeu selon les cartes visibles.
     */
    private List<Player> determineTurnOrder() {
        List<Player> order = new ArrayList<>();
        List<Offer> remaining = new ArrayList<>(currentOffers);

        while (!remaining.isEmpty()) {
            Offer highest = remaining.stream()
                    .filter(Offer::isComplete)
                    .max(Comparator.comparingInt(this::getOfferPriority))
                    .orElse(remaining.get(0));

            order.add(highest.getOwner());
            remaining.remove(highest);
        }

        return order;
    }

    /**
     * Calcule la priorite d une offre.
     */
    private int getOfferPriority(Offer offer) {
        Card faceUp = offer.getFaceUp();
        int value = faceUp.getRank().getValue() * 10;

        switch (faceUp.getSuit()) {
            case SPADES: value += 4; break;
            case CLUBS: value += 3; break;
            case DIAMONDS: value += 2; break;
            case HEARTS: value += 1; break;
        }
        return value;
    }

    /**
     * Termine la partie.
     */
    private void endGame() {
        for (Player player : game.getPlayers()) {
            Offer offer = currentOffers.stream()
                    .filter(o -> o.getOwner() == player)
                    .findFirst()
                    .orElse(null);

            if (offer != null) {
                if (offer.getFaceUp() != null) player.getJest().addCard(offer.getFaceUp());
                if (offer.getFaceDown() != null) player.getJest().addCard(offer.getFaceDown());
            }
        }

        if (game.isExtensionBM()) {
            prepareBmExtension();
        }

        Player winner = game.winner();

        if (game.isExtensionBM()) {
            announceBmExtension();
        }

        consoleView.displayGameEnd(game, winner);
        notifyGameEnd(winner);
    }

    /**
     * Demande la carte BM a appliquer au plus petit Jest.
     * Le joueur avec le plus petit Jest choisit à qui l'appliquer.
     * Si ce joueur est une IA, l'extension BM n'est pas appliquee.
     */
    private void prepareBmExtension() {
        // Trouve le joueur avec le plus petit Jest
        Player smallestJestPlayer = game.getPlayers().stream()
                .min(Comparator.comparingInt(Player::getFinalScore))
                .orElse(null);
        
        // Si aucun joueur ou si le plus petit Jest appartient a une IA, on ignore l'extension BM
        if (smallestJestPlayer == null || !smallestJestPlayer.isHuman()) {
            return;
        }
        
        // Demande au joueur humain de choisir la carte BM et le destinataire
        BMCard card = (useGui && guiView != null)
                ? guiView.askBmCard(smallestJestPlayer, game.getPlayers())
                : consoleView.askBmCard(smallestJestPlayer, game.getPlayers());
        
        game.setPendingBmCard(card);
    }

    /**
     * Informe les vues de l effet de la carte BM.
     */
    private void announceBmExtension() {
        Player target = game.getLastBmTarget();
        BMCard card = game.getLastAppliedBmCard();
        int delta = game.getLastBmDelta();

        consoleView.displayBmApplication(target, card, delta);
        if (useGui && guiView != null) {
            guiView.displayBmApplication(target, card, delta);
        }

        if (target != null && card != null) {
            String suffix = delta >= 0 ? " gagne " : " perd ";
            notifyMessage("Extension BM : " + target.getName() + suffix + Math.abs(delta) + " points.");
        }
    }

    // Notifications aux observateurs
    
    private void notifyRoundStart(int round) {
        for (GameObserver obs : observers) {
            obs.onRoundStart(round);
        }
    }

    private void notifyOffersCreated(List<Offer> offers) {
        for (GameObserver obs : observers) {
            obs.onOffersCreated(offers);
        }
    }

    private void notifyCardTaken(Player player, Card card) {
        for (GameObserver obs : observers) {
            obs.onCardTaken(player, card);
        }
    }

    private void notifyGameEnd(Player winner) {
        for (GameObserver obs : observers) {
            obs.onGameEnd(winner);
        }
    }

    private void notifyMessage(String message) {
        for (GameObserver obs : observers) {
            obs.onMessage(message);
        }
    }

    /**
     * Sauvegarde la partie.
     */
    public void saveGame(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(game);
            consoleView.displayMessage("Partie sauvegardee !");
        } catch (IOException e) {
            consoleView.displayMessage("Erreur sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Charge une partie.
     */
    public void loadGame(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            game = (Game) ois.readObject();
            consoleView.displayMessage("Partie chargee !");
        } catch (IOException | ClassNotFoundException e) {
            consoleView.displayMessage("Erreur chargement : " + e.getMessage());
        }
    }

    /**
     * Point d entree de l application.
     */
    public static void main(String[] args) {
        GameEngine engine = new GameEngine();

        // Activer la GUI par defaut
        engine.enableGUI();
        engine.guiView.display();

        // Afficher le dialogue de demarrage
        int choice = engine.guiView.showStartupDialog();

        if (choice == 2) {
            String filename = engine.guiView.askLoadFilename();
            if (!filename.isEmpty()) {
                engine.loadGame(filename);
                if (engine.game != null) {
                    engine.guiView.setGame(engine.game);
                }
            } else {
                // Si l'utilisateur annule, relancer le dialogue
                main(args);
                return;
            }
        } else {
            engine.initializeGame();
        }

        engine.playGame();

        String save = ConsoleView.SafeInput.readChoice("Sauvegarder ? (o/n) : ", "o", "n");

        if (save.equals("o")) {
            String filename = ConsoleView.SafeInput.readString("Nom du fichier : ");
            engine.saveGame(filename);
        }
    }
}
