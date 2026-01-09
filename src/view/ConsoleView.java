package view;

import model.*;
import model.extension.BMCard;
import model.rules.*;
import strategy.*;
import observer.GameObserver;
import view.gui.JestGUI;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Vue console pour l interface utilisateur du jeu Jest.
 * 
 * <p>Cette classe implemente l interface textuelle du jeu dans le terminal.
 * Elle fait partie de l architecture MVC comme l une des deux vues concurrentes.</p>
 * 
 * <h2>Fonctionnalites :</h2>
 * <ul>
 *   <li>Affichage des menus et messages</li>
 *   <li>Saisie securisee des choix utilisateur</li>
 *   <li>Affichage des offres et des scores</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 */
public class ConsoleView implements GameObserver {
    
    private JestGUI guiHelper;

    /**
     * Constructeur de la vue console.
     */
    public ConsoleView() {}

    /**
     * Configure la vue graphique pour synchroniser les interactions.
     */
    public void setGuiHelper(JestGUI gui) {
        this.guiHelper = gui;
    }

    /**
     * Affiche le message de bienvenue.
     */
    public void displayWelcome() {
        System.out.println("========================================");
        System.out.println("        Bienvenue dans JEST!");
        System.out.println("========================================");
        System.out.println();
    }

    /**
     * Demande le nombre de joueurs.
     * 
     * @return le nombre de joueurs (3 ou 4)
     */
    public int askNumberOfPlayers() {
        if (guiHelper != null) {
            guiHelper.promptNumberOfPlayers();
        }
        return SafeInput.readIntInRange("Nombre de joueurs (3-4) : ", 3, 4);
    }

    /**
     * Demande les informations d un joueur.
     * 
     * @param playerNumber le numero du joueur
     * @return le joueur cree
     */
    public Player askPlayerType(int playerNumber) {
        System.out.println("Joueur " + playerNumber + " :");

        if (guiHelper != null) {
            guiHelper.promptPlayerName(playerNumber);
        }
        String name = SafeInput.readString("Nom : ");

        if (guiHelper != null) {
            guiHelper.promptPlayerType(name);
        }
        int type = SafeInput.readIntInRange("Type (1: Humain, 2: IA Simple, 3: IA Bluff) : ", 1, 3);

        switch (type) {
            case 1:
                return new Player(name, new HumanStrategy());
            case 2:
                return new Player(name, new Strategy1());
            case 3:
                return new Player(name, new Strategy2());
            default:
                throw new IllegalStateException("Type invalide");
        }
    }

    /**
     * Demande les regles a utiliser.
     * 
     * @return le RuleSet choisi
     */
    public RuleSet askRuleSet() {
        System.out.println();
        System.out.println("Choisissez les regles :");
        System.out.println("1. Regles de base");
        System.out.println("2. Variante : Trophees inverses");
        System.out.println("3. Variante : Couleurs inversees");

        if (guiHelper != null) {
            guiHelper.promptRuleSet();
        }
        int choice = SafeInput.readIntInRange("Votre choix : ", 1, 3);

        switch (choice) {
            case 2: return new Rule2();
            case 3: return new Rule3();
            default: return new Rule1();
        }
    }

    /**
     * Demande si les extensions doivent etre activees.
     * Version console detaillee :
     *  - question O/N pour la carte BM (Bonus/Malus)
     *  - question O/N pour les cartes supplementaires 5-8.
     *
     * @return tableau [extensionBM, extensionCards]
     */
    public boolean[] askExtensions() {
        boolean[] extensions = new boolean[2];

        // Extension Carte BM (Bonus/Malus)
        System.out.println();
        System.out.println("==== Extension Carte BM (Bonus/Malus) ====");
        System.out.println("Cette carte est attribuée au joueur ayant le plus petit Jest en fin de partie.");
        System.out.println("Elle lui permet soit : ");
        System.out.println("    - De s'ajouter un bonus : +1 ou +2 points");
        System.out.println("    - De donner un malus : -1, -2 ou -3 points");

        String bmResponse = SafeInput.readChoice(
                "Voulez-vous activer l'extension Carte BM (o/n) : ",
                "o", "n"
        );
        extensions[0] = bmResponse.equals("o");

        // Extension cartes supplementaires 5-8
        System.out.println();
        System.out.println("==== Extension Cartes supplementaires 5-8 ====");
        System.out.println("Ajoute les cartes 5, 6, 7 et 8 dans chaque couleur (Le deck sera plus grand).");

        String cardsResponse = SafeInput.readChoice(
                "Voulez-vous activer l'extension cartes 5-8 ? (o/n) : ",
                "o", "n"
        );
        extensions[1] = cardsResponse.equals("o");

        return extensions;
    }

    /**
     * Demande la carte Bonus/Malus a appliquer.
     * Le joueur avec le plus petit Jest choisit à qui l'appliquer.
     * 
     * @param smallestJestPlayer le joueur avec le plus petit Jest
     * @param allPlayers tous les joueurs de la partie
     * @return la carte BM selectionnee
     */
    public BMCard askBmCard(Player smallestJestPlayer, List<Player> allPlayers) {
        System.out.println("\n==== Extension Bonus/Malus ====");
        System.out.println(smallestJestPlayer.getName() + ", vous avez le plus petit Jest !");
        System.out.println("Choisissez à qui appliquer la carte BM :");
        
        // Affiche les joueurs disponibles
        for (int i = 0; i < allPlayers.size(); i++) {
            System.out.println((i + 1) + ". " + allPlayers.get(i).getName());
        }
        
        if (guiHelper != null) {
            guiHelper.promptBmTarget(smallestJestPlayer, allPlayers);
        }
        int targetChoice = SafeInput.readIntInRange("Joueur (1-" + allPlayers.size() + ") : ", 1, allPlayers.size());
        Player target = allPlayers.get(targetChoice - 1);
        
        System.out.println("\nSelectionnez le type de carte BM :");
        System.out.println("1. Bonus (1-2 points)");
        System.out.println("2. Malus (-1 à -3 points)");

        if (guiHelper != null) {
            guiHelper.promptBmType();
        }
        int typeChoice = SafeInput.readIntInRange("Type (1-2) : ", 1, 2);
        
        BMCard.BMType type = (typeChoice == 1) ? BMCard.BMType.BONUS : BMCard.BMType.MALUS;
        
        if (guiHelper != null) {
            guiHelper.promptBmValue(type);
        }
        
        int value;
        if (type == BMCard.BMType.BONUS) {
            value = SafeInput.readIntInRange("Valeur du Bonus (1-2) : ", 1, 2);
        } else {
            value = SafeInput.readIntInRange("Valeur du Malus (1-3) : ", 1, 3);
        }

        BMCard card = new BMCard(type, value);
        card.setTarget(target);  // Enregistre le destinataire
        return card;
    }
    
    /**
     * Demande l extension simple (retrocompatibilite).
     * 
     * @return true si extension activee
     */
    public boolean askExtension() {
        String response = SafeInput.readChoice("Voulez-vous activer l'extension cartes 5-8 ? (o/n) : ", "o", "n");
        return response.equals("o");
    }

    /**
     * Demande au joueur humain de creer son offre.
     * 
     * @param player le joueur humain
     * @return l offre creee
     */
    public Offer askHumanOffer(Player player) {
        System.out.println();
        System.out.println("=== Offre de " + player.getName() + " ===");
        System.out.println("Main actuelle :");

        List<Card> hand = player.getHand();

        for (int i = 0; i < hand.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + hand.get(i));
        }

        System.out.println();

        if (guiHelper != null) {
            guiHelper.promptHumanOffer(player);
        }
        int faceDownNumber = SafeInput.readIntInRange("Quelle carte souhaites-tu cacher ? (1-2) : ", 1, 2);
        int faceDownIndex = faceDownNumber - 1;
        int faceUpIndex = (faceDownIndex == 0 ? 1 : 0);

        Offer offer = new Offer(player);
        offer.setFaceDown(hand.get(faceDownIndex));
        offer.setFaceUp(hand.get(faceUpIndex));

        return offer;
    }

    /**
     * Demande au joueur humain de choisir une carte.
     * 
     * @param player le joueur
     * @param offers les offres disponibles
     * @param game   la partie
     * @return la carte choisie
     */
    public Card askHumanTake(Player player, List<Offer> offers, Game game) {
        System.out.println();
        System.out.println("=== Prise de carte pour " + player.getName() + " ===");

        if (offers == null || offers.isEmpty()) {
            System.out.println("Aucune offre disponible !");
            return null;
        }

        System.out.println("Offres disponibles :");

        if (guiHelper != null) {
            guiHelper.promptOfferSelection(player, offers);
        }
        for (int i = 0; i < offers.size(); i++) {
            Offer offer = offers.get(i);
            System.out.println("  Offre " + (i + 1) + " - " + offer.getOwner().getName());
            System.out.println("    (v) Carte visible : " + offer.getFaceUp());
            System.out.println("    (c) Carte cachee : X");
        }

        System.out.println();
        int offerIndex;
        if (offers.size() == 1) {
            System.out.println("Vous devez prendre dans l'unique offre disponible (offre 1).");
            offerIndex = 0;
        } else {
            offerIndex = SafeInput.readIntInRange("Numero de l'offre choisie (1-" + offers.size() + ") : ", 1, offers.size()) - 1;
        }

        Offer chosenOffer = offers.get(offerIndex);

        if (guiHelper != null) {
            guiHelper.promptVisibleChoice(chosenOffer);
        }

        System.out.println();
        System.out.println("Vous devez prendre dans l'offre de " + chosenOffer.getOwner().getName() + ".");
        String choice = SafeInput.readChoice("Voulez-vous prendre la carte (v)isible ou (c)achee ? (v/c) : ", "v", "c");
        Card chosen;
        Card result;

        if (choice.equalsIgnoreCase("v")) {
            chosen = chosenOffer.getFaceUp();
            Card unchosen = chosenOffer.getUnchosen(chosen);
            result = chosenOffer.takeFaceUp();
            game.addCarryOverCard(unchosen);
        } else {
            chosen = chosenOffer.getFaceDown();
            Card unchosen = chosenOffer.getUnchosen(chosen);
            result = chosenOffer.takeFaceDown();
            game.addCarryOverCard(unchosen);
        }

        return result;
    }

    /**
     * Demande au joueur de prendre dans sa propre offre.
     */
    public Card askTakeFromOwnOffer(Player player, Offer offer, Game game) {
        if (player.isHuman()) {
            System.out.println(player.getName() + ", vous devez prendre dans votre propre offre :");
            System.out.println("1. Visible : " + offer.getFaceUp());
            System.out.println("2. Cachee : X");

            if (guiHelper != null) {
                guiHelper.promptOwnOfferChoice(player, offer);
            }
            int choice = SafeInput.readIntInRange("Choix (1-2) : ", 1, 2);

            Card chosen;
            Card unchosen;
            Card result;

            if (choice == 1) {
                chosen = offer.getFaceUp();
                unchosen = offer.getUnchosen(chosen);
                result = offer.takeFaceUp();
            } else {
                chosen = offer.getFaceDown();
                unchosen = offer.getUnchosen(chosen);
                result = offer.takeFaceDown();
            }

            game.addCarryOverCard(unchosen);
            return result;

        } else {
            boolean takeVisible = new Random().nextBoolean();
            Card chosen;
            Card unchosen;
            Card result;

            if (takeVisible) {
                chosen = offer.getFaceUp();
                unchosen = offer.getUnchosen(chosen);
                result = offer.takeFaceUp();
            } else {
                chosen = offer.getFaceDown();
                unchosen = offer.getUnchosen(chosen);
                result = offer.takeFaceDown();
            }

            game.addCarryOverCard(unchosen);
            return result;
        }
    }

    /**
     * Affiche qu une carte a ete prise.
     */
    public void displayCardTaken(Player player, Card card) {
        if (card == null) {
            System.out.println("    -> " + player.getName() + " a pris une carte.");
            return;
        }
        Player offerOwner = card.getLastOfferOwner();
        if (offerOwner != null) {
            boolean hidden = card.wasTakenHidden();
            String ownerText = offerOwner == player ? "sa propre offre" : "l'offre de " + offerOwner.getName();
            String cardType = hidden ? "la carte cachée" : "la carte visible";
            System.out.println("-> " + player.getName() + " a pris " + cardType + " de " + ownerText + ".");
        } else {
            System.out.println("-> " + player.getName() + " a pris : " + card);
        }
        System.out.println();
    }

    /**
     * Affiche l effet de la carte BM.
     */
    public void displayBmApplication(Player target, BMCard card, int delta) {
        if (target == null || card == null) {
            return;
        }

        int absolute = Math.abs(delta);
        String effect = delta >= 0
                ? "recoit un bonus de +" + absolute
                : "subit un malus de -" + absolute;

        System.out.println("Extension BM : " + target.getName() + " " + effect + " points.");
    }

    /**
     * Affiche la fin de partie.
     */
    public void displayGameEnd(Game game, Player winner) {
        System.out.println();
        System.out.println("==== FIN DE PARTIE ====");

        System.out.println("\n==== RÉSULTATS ====");

        for (Player player : game.getPlayers()) {
            Jest jest = player.getJest();

            List<Card> cartesNormales = new ArrayList<>();
            List<TrophyCard> trophees = new ArrayList<>();

            for (Card c : jest.getCards()) {
                if (c instanceof TrophyCard) {
                    trophees.add((TrophyCard) c);
                } else {
                    cartesNormales.add(c);
                }
            }

            System.out.println("\n" + player.getName() + " :");
            System.out.println("  Cartes : " + cartesNormales);
            System.out.println("  Trophees : " + (trophees.isEmpty() ? "aucun" : trophees));
            System.out.println("  Score base : " + player.getBaseScore());
            System.out.println("  Bonus trophees : " + player.getTrophyBonus());
            System.out.println("  SCORE FINAL : " + player.getFinalScore());
        }

        System.out.println("\n==== ANNONCE DES RÉSULTATS ====");

        if (winner != null) {
            System.out.println("GAGNANT : " + winner.getName() + " avec " + winner.getFinalScore() + " points !");
        } else {
            System.out.println("Egalite parfaite !");
        }
    }

    /**
     * Affiche un message.
     */
    public void displayMessage(String message) {
        System.out.println(message);
    }

    // Implementation GameObserver
    @Override
    public void onRoundStart(int roundNumber) {
        System.out.println("\n==== TOUR " + roundNumber + " ====");
    }

    @Override
    public void onOffersCreated(List<Offer> offers) {
        System.out.println();
        System.out.println("Offres créees");
        if (offers == null || offers.isEmpty()) {
            System.out.println();
            return;
        }
        
        System.out.println();
        System.out.println("==== Récapitulatif des offres =====");
        for (Offer offer : offers) {
            if (offer == null) continue;
            String owner = offer.getOwner() != null ? offer.getOwner().getName() : "Joueur inconnu";
            String visible = formatOfferCard(offer.getFaceUp());
            System.out.println("  - " + owner + " : Carte visible : " + visible);
        }

        Offer startingOffer = computeStartingOffer(offers);
        if (startingOffer != null && startingOffer.getOwner() != null) {
            System.out.println(">> " + startingOffer.getOwner().getName() + " commence ce tour.");
        } else {
            System.out.println("Impossible de déterminer qui commence (offres incomplètes).");
        }
        System.out.println();
    }

    @Override
    public void onCardTaken(Player player, Card card) {
        displayCardTaken(player, card);
    }

    @Override
    public void onGameEnd(Player winner) {
        System.out.println("Partie terminee !");
    }

    @Override
    public void onMessage(String message) {
        displayMessage(message);
    }

    private Offer computeStartingOffer(List<Offer> offers) {
        return offers.stream()
                .filter(Objects::nonNull)
                .filter(Offer::isComplete)
                .max(Comparator.comparingInt(this::computeOfferPriority))
                .orElse(null);
    }

    private int computeOfferPriority(Offer offer) {
        Card faceUp = offer.getFaceUp();
        if (faceUp == null || faceUp.getRank() == null) {
            return Integer.MIN_VALUE;
        }
        int value = faceUp.getRank().getValue() * 10;
        Suits suit = faceUp.getSuit();
        if (suit != null) {
            switch (suit) {
                case SPADES: value += 4; break;
                case CLUBS: value += 3; break;
                case DIAMONDS: value += 2; break;
                case HEARTS: value += 1; break;
                default: break;
            }
        }
        return value;
    }

    private String formatOfferCard(Card card) {
        if (card == null) {
            return "aucune";
        }
        Rank rank = card.getRank();
        Suits suit = card.getSuit();
        String rankName = rank != null ? rank.getDisplayName() : card.toString();
        String suitSymbol = suit != null ? suit.getSymbol() : "";
        if (suitSymbol.isEmpty()) {
            return rankName;
        }
        return rankName + " " + suitSymbol;
    }

    /**
     * Classe utilitaire pour les saisies securisees.
     */
    public static class SafeInput {
        private static final BlockingQueue<String> INPUT_QUEUE = new LinkedBlockingQueue<>();
        private static final Scanner CONSOLE_SCANNER = new Scanner(System.in);

        static {
            Thread consoleReader = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    String line = CONSOLE_SCANNER.nextLine();
                    try {
                        INPUT_QUEUE.put(line.trim());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "ConsoleInputReader");
            consoleReader.setDaemon(true);
            consoleReader.start();
        }

        public static void submitExternalInput(String value) {
            if (value != null) {
                INPUT_QUEUE.offer(value.trim());
            }
        }

        private static String nextLine(String message) {
            System.out.print(message);
            try {
                return INPUT_QUEUE.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "";
            }
        }

        public static int readIntInRange(String message, int min, int max) {
            while (true) {
                String input = nextLine(message);
                try {
                    int value = Integer.parseInt(input);
                    if (value >= min && value <= max) {
                        return value;
                    }
                    System.out.println("Choix invalide (entre " + min + " et " + max + ").");
                } catch (NumberFormatException e) {
                    System.out.println("Saisie invalide.");
                }
            }
        }

        public static String readChoice(String message, String... validChoices) {
            while (true) {
                String input = nextLine(message).toLowerCase();
                for (String v : validChoices) {
                    if (input.equals(v.toLowerCase())) {
                        return input;
                    }
                }
                System.out.print("Choix invalide (options : ");
                System.out.println(String.join(", ", validChoices) + ").");
            }
        }

        public static String readString(String message) {
            return nextLine(message);
        }
    }
}
