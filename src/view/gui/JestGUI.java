package view.gui;

import model.*;
import model.extension.BMCard;
import model.rules.*;
import observer.GameObserver;
import strategy.*;
import view.ConsoleView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ArrayList;

/**
 * Interface graphique Swing pour le jeu Jest.
 * 
 * <p>Cette classe implemente une interface graphique basique utilisant
 * Swing. Elle fait partie de l architecture MVC comme l une des deux
 * vues concurrentes (avec ConsoleView).</p>
 * 
 * <h2>Architecture :</h2>
 * <ul>
 *   <li>JFrame principale avec plusieurs panneaux</li>
 *   <li>Panneau d information en haut</li>
 *   <li>Panneau des offres au centre</li>
 *   <li>Panneau des actions en bas</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 2.0
 */
public class JestGUI extends JFrame implements GameObserver {
    
    private static final long serialVersionUID = 1L;
    
    private JPanel mainPanel;
    private JPanel infoPanel;
    private JPanel promptPanel;
    private JPanel gameStatePanel;
    private JLabel promptLabel;
    private JPanel promptOptions;
    private JTextArea logArea;
    private JLabel statusLabel;
    private JPanel trophiesPanel;
    private JButton saveButton;
    
    private Game game;
    private List<Offer> currentOffers;
    private List<TrophyCard> currentTrophies;

    /**
     * Constructeur de l interface graphique.
     */
    public JestGUI() {
        super("Jest - Jeu de Cartes");
        initComponents();
        currentOffers = new ArrayList<>();
    }

    // Palette de couleurs - Orange Jest symbolique
    private static final Color BG_PRIMARY = new Color(245, 245, 250);    // Fond principal (bleu très clair)
    private static final Color BG_SECONDARY = new Color(255, 255, 255); // Fond cartes (blanc pur)
    private static final Color ACCENT = new Color(255, 140, 0);          // Orange Jest
    private static final Color ACCENT_DARK = new Color(230, 110, 0);     // Orange foncé
    private static final Color TEXT_PRIMARY = new Color(30, 30, 40);     // Texte principal (noir bleuté)
    private static final Color TEXT_SECONDARY = new Color(110, 120, 140); // Texte secondaire
    private static final Color BORDER = new Color(220, 225, 235);        // Bordures (bleu très clair)

    /**
     * Initialise les composants de l interface.
     */
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panneau d information (Header)
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(BG_SECONDARY);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        statusLabel = new JLabel("JEST - Jeu de Cartes");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        statusLabel.setForeground(TEXT_PRIMARY);
        infoPanel.add(statusLabel, BorderLayout.WEST);
        
        // Bouton de sauvegarde
        saveButton = createStyledButton("Sauvegarder");
        saveButton.addActionListener(e -> saveGame());
        infoPanel.add(saveButton, BorderLayout.EAST);
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Panneau des interactions (Centre) - Réduit
        promptPanel = new JPanel(new BorderLayout(5, 5));
        promptPanel.setBackground(BG_SECONDARY);
        promptPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        promptLabel = new JLabel("Choisissez une action pour commencer");
        promptLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        promptLabel.setForeground(TEXT_PRIMARY);
        promptOptions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        promptOptions.setBackground(BG_SECONDARY);
        promptPanel.add(promptLabel, BorderLayout.NORTH);
        promptPanel.add(promptOptions, BorderLayout.CENTER);

        // Zone de log - Réduite
        logArea = new JTextArea(8, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logArea.setBackground(BG_SECONDARY);
        logArea.setForeground(TEXT_PRIMARY);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.getViewport().setBackground(BG_SECONDARY);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT, 1));

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(BG_PRIMARY);
        JLabel logTitle = new JLabel("Historique");
        logTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        logTitle.setForeground(ACCENT);
        logTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        logPanel.add(logTitle, BorderLayout.NORTH);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        logPanel.setPreferredSize(new Dimension(0, 150));

        // Regroupe historique (en haut) + zone de choix (en bas)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_PRIMARY);
        centerPanel.add(logPanel, BorderLayout.CENTER);
        centerPanel.add(promptPanel, BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Panneau d etat de partie (Droite) - Agrandi
        gameStatePanel = new JPanel();
        gameStatePanel.setLayout(new BoxLayout(gameStatePanel, BoxLayout.Y_AXIS));
        gameStatePanel.setBackground(BG_SECONDARY);
        gameStatePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Créer le panneau des trophées dès le départ
        trophiesPanel = new JPanel();
        trophiesPanel.setLayout(new BoxLayout(trophiesPanel, BoxLayout.Y_AXIS));
        trophiesPanel.setBackground(BG_SECONDARY);
        trophiesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel trophyTitle = new JLabel("Trophées");
        trophyTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        trophyTitle.setForeground(ACCENT);
        trophyTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        trophiesPanel.add(trophyTitle);
        trophiesPanel.add(Box.createVerticalStrut(8));
        
        gameStatePanel.add(trophiesPanel);
        gameStatePanel.add(Box.createVerticalStrut(10));
        
        JScrollPane stateScroll = new JScrollPane(gameStatePanel);
        stateScroll.setPreferredSize(new Dimension(600, 0));
        stateScroll.getViewport().setBackground(BG_SECONDARY);
        stateScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        stateScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        stateScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 2, 0, 0, ACCENT),
            BorderFactory.createEmptyBorder(10, 15, 10, 10)
        ));
        mainPanel.add(stateScroll, BorderLayout.EAST);

        add(mainPanel);
    }

    /**
     * Definit la partie en cours.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    private void preparePrompt(String message) {
        runSync(() -> {
            promptLabel.setText(message);
            promptOptions.removeAll();
            promptPanel.revalidate();
            promptPanel.repaint();
        });
    }

    private void showButtons(String message, List<ButtonSpec> buttons) {
        preparePrompt(message);
        runSync(() -> {
            promptOptions.removeAll();
            
            for (ButtonSpec spec : buttons) {
                JButton btn = createStyledButton(spec.label);
                
                if (spec.card != null) {
                    ImageIcon icon = CardImages.get(spec.card, spec.faceUp);
                    if (icon != null) {
                        btn.setIcon(icon);
                        btn.setHorizontalTextPosition(SwingConstants.CENTER);
                        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                    }
                }
                btn.addActionListener(e -> submitValue(spec.value));
                promptOptions.add(btn);
            }
            promptPanel.revalidate();
            promptPanel.repaint();
        });
    }
    
    /**
     * Cree un bouton avec le style uniforme et arrondi.
     */
    private JButton createStyledButton(String label) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isArmed()) {
                    g2.setColor(ACCENT_DARK);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 160, 20));
                } else {
                    g2.setColor(ACCENT);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                super.paintComponent(g);
            }
            
            @Override
            public void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.repaint();
            }
            public void mouseExited(MouseEvent evt) {
                btn.repaint();
            }
        });
        
        return btn;
    }

    private void showTextPrompt(String message, String buttonLabel) {
        preparePrompt(message);
        JTextField field = new JTextField(20);
        JButton validate = createStyledButton(buttonLabel);
        
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBackground(BG_SECONDARY);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setCaretColor(TEXT_PRIMARY);
        
        validate.addActionListener(e -> {
            String text = field.getText().trim();
            if (!text.isEmpty()) {
                submitValue(text);
            }
        });
        runSync(() -> {
            promptOptions.removeAll();
            promptOptions.add(field);
            promptOptions.add(validate);
            promptPanel.revalidate();
            promptPanel.repaint();
            field.requestFocusInWindow();
        });
    }

    private void showSpinnerPrompt(String message, int min, int max, int initial) {
        preparePrompt(message);
        SpinnerNumberModel model = new SpinnerNumberModel(initial, min, max, 1);
        JSpinner spinner = new JSpinner(model);
        JButton validate = createStyledButton("Valider");
        
        spinner.setFont(new Font("SansSerif", Font.PLAIN, 14));
        spinner.setBackground(BG_SECONDARY);
        spinner.setForeground(TEXT_PRIMARY);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(BG_SECONDARY);
            tf.setForeground(TEXT_PRIMARY);
            tf.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        }
        
        validate.addActionListener(e -> submitValue(String.valueOf(model.getNumber().intValue())));
        runSync(() -> {
            promptOptions.removeAll();
            promptOptions.add(spinner);
            promptOptions.add(validate);
            promptPanel.revalidate();
            promptPanel.repaint();
        });
    }

    private void submitValue(String value) {
        ConsoleView.SafeInput.submitExternalInput(value);
    }

    // ----- Prompts utilises par la Console -----

    public void promptNumberOfPlayers() {
        List<ButtonSpec> buttons = List.of(
                new ButtonSpec("3 joueurs", "3"),
                new ButtonSpec("4 joueurs", "4")
        );
        showButtons("Nombre de joueurs ?", buttons);
    }

    public void promptPlayerName(int playerNumber) {
        showTextPrompt("Nom du joueur " + playerNumber + " :", "Valider");
    }

    public void promptPlayerType(String playerName) {
        List<ButtonSpec> buttons = List.of(
                new ButtonSpec("Humain", "1"),
                new ButtonSpec("IA Simple", "2"),
                new ButtonSpec("IA Bluff", "3")
        );
        showButtons("Type pour " + playerName + " :", buttons);
    }

    public void promptRuleSet() {
        List<ButtonSpec> buttons = List.of(
                new ButtonSpec("Regles de base", "1"),
                new ButtonSpec("Trophees inverses", "2"),
                new ButtonSpec("Couleurs inversees", "3")
        );
        showButtons("Choisissez les regles :", buttons);
    }

    public void promptExtensionsQuestion() {
        List<ButtonSpec> buttons = List.of(
                new ButtonSpec("Oui", "o"),
                new ButtonSpec("Non", "n")
        );
        // Explication + question pour l'extension Carte BM (Bonus/Malus)
        log("=== Extension Carte BM (Bonus/Malus) ===");
        log("Cette carte est donnee au joueur avec le plus petit Jest en fin de partie.");
        log("Elle permet soit de s'ajouter un bonus, soit de donner un malus à un autre joueur.");
        showButtons("Activer l'extension Carte BM (Bonus/Malus) ?", buttons);
    }

    public void promptExtensionsSelection() {
        // Explication + question pour l'extension cartes supplementaires 5-8
        List<ButtonSpec> buttons = List.of(
                new ButtonSpec("Oui", "o"),
                new ButtonSpec("Non", "n")
        );
        log("=== Extension Cartes supplementaires 5-8 ===");
        log("Ajoute les cartes 5, 6, 7 et 8 dans chaque couleur (deck plus grand).");
        showButtons("Activer l'extension cartes 5-8 ?", buttons);
    }

    // ----- Méthodes ask* pour intégration complète avec GameEngine -----

    /**
     * Demande le nombre de joueurs (3 ou 4) via l'interface graphique.
     */
    public int askNumberOfPlayers() {
        promptNumberOfPlayers();
        
        JDialog dialog = new JDialog(this, "Nombre de joueurs", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Combien de joueurs ?");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonsPanel.setBackground(BG_PRIMARY);
        
        final int[] choice = {3};
        
        JButton btn3 = createStyledButton("3 joueurs");
        btn3.addActionListener(e -> {
            choice[0] = 3;
            dialog.dispose();
        });
        
        JButton btn4 = createStyledButton("4 joueurs");
        btn4.addActionListener(e -> {
            choice[0] = 4;
            dialog.dispose();
        });
        
        buttonsPanel.add(btn3);
        buttonsPanel.add(btn4);
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        dialog.add(panel);
        dialog.setVisible(true);
        
        return choice[0];
    }

    /**
     * Demande les informations d'un joueur (nom + type) via la GUI.
     */
    public Player askPlayerType(int playerNumber) {
        promptPlayerName(playerNumber);
        
        // Dialogue pour le nom
        String name = JOptionPane.showInputDialog(this,
            "Entrez le nom du joueur " + playerNumber + " :",
            "Joueur " + playerNumber,
            JOptionPane.QUESTION_MESSAGE);
        
        if (name == null || name.trim().isEmpty()) {
            name = "Joueur " + playerNumber;
        }
        
        promptPlayerType(name);
        
        // Dialogue pour le type
        JDialog dialog = new JDialog(this, "Type de joueur", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel(name + " - Type de joueur ?");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonsPanel.setBackground(BG_PRIMARY);
        
        final int[] choice = {1};
        
        JButton btnHuman = createStyledButton("Humain");
        btnHuman.addActionListener(e -> {
            choice[0] = 1;
            dialog.dispose();
        });
        
        JButton btnIA1 = createStyledButton("IA Simple");
        btnIA1.addActionListener(e -> {
            choice[0] = 2;
            dialog.dispose();
        });
        
        JButton btnIA2 = createStyledButton("IA Bluff");
        btnIA2.addActionListener(e -> {
            choice[0] = 3;
            dialog.dispose();
        });
        
        buttonsPanel.add(btnHuman);
        buttonsPanel.add(btnIA1);
        buttonsPanel.add(btnIA2);
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        dialog.add(panel);
        dialog.setVisible(true);
        
        switch (choice[0]) {
            case 1:
                return new Player(name, new HumanStrategy());
            case 2:
                return new Player(name, new Strategy1());
            case 3:
                return new Player(name, new Strategy2());
            default:
                return new Player(name, new HumanStrategy());
        }
    }

    /**
     * Demande le RuleSet a utiliser via la GUI.
     */
    public RuleSet askRuleSet() {
        promptRuleSet();
        
        JDialog dialog = new JDialog(this, "Choix des règles", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Choisissez les règles");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonsPanel.setBackground(BG_PRIMARY);
        
        final int[] choice = {1};
        
        JButton btn1 = createStyledButton("Règle 1 - Standard");
        btn1.addActionListener(e -> {
            choice[0] = 1;
            dialog.dispose();
        });
        
        JButton btn2 = createStyledButton("Règle 2 - Variante");
        btn2.addActionListener(e -> {
            choice[0] = 2;
            dialog.dispose();
        });
        
        JButton btn3 = createStyledButton("Règle 3 - Variante");
        btn3.addActionListener(e -> {
            choice[0] = 3;
            dialog.dispose();
        });
        
        buttonsPanel.add(btn1);
        buttonsPanel.add(btn2);
        buttonsPanel.add(btn3);
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        dialog.add(panel);
        dialog.setVisible(true);
        
        switch (choice[0]) {
            case 2:
                return new Rule2();
            case 3:
                return new Rule3();
            default:
                return new Rule1();
        }
    }

    /**
     * Demande l'activation des extensions via la GUI.
     * @return tableau [extensionBM, extensionCards]
     */
    public boolean[] askExtensions() {
        boolean[] extensions = new boolean[2];

        promptExtensionsQuestion();
        
        JDialog dialog = new JDialog(this, "Extensions", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Activer les extensions ?");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        optionsPanel.setBackground(BG_PRIMARY);
        
        final boolean[] bmChoice = {false};
        final boolean[] cardsChoice = {false};
        
        JCheckBox bmCheckbox = new JCheckBox("Extension Carte BM (Bonus/Malus)");
        bmCheckbox.setBackground(BG_PRIMARY);
        bmCheckbox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bmCheckbox.setForeground(TEXT_PRIMARY);
        bmCheckbox.addActionListener(e -> bmChoice[0] = bmCheckbox.isSelected());
        
        JCheckBox cardsCheckbox = new JCheckBox("Extension Cartes 5-8");
        cardsCheckbox.setBackground(BG_PRIMARY);
        cardsCheckbox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cardsCheckbox.setForeground(TEXT_PRIMARY);
        cardsCheckbox.addActionListener(e -> cardsChoice[0] = cardsCheckbox.isSelected());
        
        optionsPanel.add(bmCheckbox);
        optionsPanel.add(cardsCheckbox);
        panel.add(optionsPanel, BorderLayout.CENTER);
        
        JButton okButton = createStyledButton("Continuer");
        okButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BG_PRIMARY);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
        
        extensions[0] = bmChoice[0];
        extensions[1] = cardsChoice[0];
        
        return extensions;
    }

    /**
     * Demande au joueur humain de créer son offre via la GUI.
     */
    public Offer askHumanOffer(Player player) {
        // Affiche la main sous forme graphique
        promptHumanOffer(player);

        // Saisie du choix via SafeInput (alimenté par les boutons)
        int faceDownNumber = ConsoleView.SafeInput.readIntInRange(
                "Carte FACE CACHEE ? (1-2) : ", 1, 2);
        int faceDownIndex = faceDownNumber - 1;
        int faceUpIndex = (faceDownIndex == 0 ? 1 : 0);

        List<Card> hand = player.getHand();
        Offer offer = new Offer(player);
        offer.setFaceDown(hand.get(faceDownIndex));
        offer.setFaceUp(hand.get(faceUpIndex));

        return offer;
    }

    /**
     * Demande au joueur humain de choisir une carte parmi les offres.
     */
    public Card askHumanTake(Player player, List<Offer> offers, Game game) {
        if (offers == null || offers.isEmpty()) {
            return null;
        }

        // Choix de l'offre
        promptOfferSelection(player, offers);

        int offerIndex;
        if (offers.size() == 1) {
            offerIndex = 0;
        } else {
            offerIndex = ConsoleView.SafeInput.readIntInRange(
                    "Quelle offre ? (1-" + offers.size() + ") : ",
                    1, offers.size()) - 1;
        }

        Offer chosenOffer = offers.get(offerIndex);

        // Choix visible / cachée
        promptVisibleChoice(chosenOffer);
        String choice = ConsoleView.SafeInput.readChoice(
                "Visible ou Cachee ? (v/c) : ", "v", "c");

        Card chosen;
        Card unchosen;
        Card result;

        if (choice.equalsIgnoreCase("v")) {
            chosen = chosenOffer.getFaceUp();
            unchosen = chosenOffer.getUnchosen(chosen);
            result = chosenOffer.takeFaceUp();
        } else {
            chosen = chosenOffer.getFaceDown();
            unchosen = chosenOffer.getUnchosen(chosen);
            result = chosenOffer.takeFaceDown();
        }

        game.addCarryOverCard(unchosen);
        return result;
    }

    /**
     * Demande au joueur de prendre dans sa propre offre via la GUI.
     */
    public Card askTakeFromOwnOffer(Player player, Offer offer, Game game) {
        // Prompt graphique
        promptOwnOfferChoice(player, offer);

        int choice = ConsoleView.SafeInput.readIntInRange(
                "Choix (1-2) : ", 1, 2);

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
    }

    public void promptHumanOffer(Player player) {
        List<ButtonSpec> buttons = new ArrayList<>();
        List<Card> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            buttons.add(new ButtonSpec("Carte " + (i + 1), String.valueOf(i + 1), card, true));
        }
        showButtons(player.getName() + " - Choisissez la carte FACE CACHEE :", buttons);
    }

    public void promptOfferSelection(Player player, List<Offer> offers) {
        // Affiche chaque offre avec carte visible + carte cachee, cliquable
        preparePrompt(player.getName() + " - Choisissez une offre :");
        runSync(() -> {
            promptOptions.removeAll();

            Color[] offerColors = {
                new Color(244, 67, 54),   // Rouge
                new Color(76, 175, 80),  // Vert
                new Color(255, 152, 0),  // Orange
                new Color(156, 39, 176)  // Violet
            };

            for (int i = 0; i < offers.size(); i++) {
                Offer offer = offers.get(i);
                int offerIndex = i;

                JPanel offerPanel = new JPanel(new BorderLayout(5, 5));
                offerPanel.setBackground(Color.WHITE);

                Color offerColor = offerColors[i % offerColors.length];
                offerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(offerColor, 2),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));

                // Ligne des deux cartes : visible + cachee
                JPanel cardsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
                cardsRow.setBackground(Color.WHITE);

                JLabel visibleLabel = new JLabel();
                ImageIcon visibleIcon = CardImages.get(offer.getFaceUp(), true);
                if (visibleIcon != null) {
                    visibleLabel.setIcon(visibleIcon);
                } else if (offer.getFaceUp() != null) {
                    visibleLabel.setText(offer.getFaceUp().toString());
                    visibleLabel.setFont(new Font("Arial", Font.PLAIN, 9));
                }

                JLabel hiddenLabel = new JLabel();
                ImageIcon hiddenIcon = CardImages.get(offer.getFaceDown(), false);
                if (hiddenIcon != null) {
                    hiddenLabel.setIcon(hiddenIcon);
                } else if (offer.getFaceDown() != null) {
                    hiddenLabel.setText("Carte cachee");
                    hiddenLabel.setFont(new Font("Arial", Font.ITALIC, 9));
                }

                cardsRow.add(visibleLabel);
                cardsRow.add(hiddenLabel);

                JLabel owner = new JLabel(offer.getOwner().getName(), SwingConstants.CENTER);
                owner.setFont(new Font("Arial", Font.BOLD, 11));
                owner.setForeground(offerColor);

                offerPanel.add(cardsRow, BorderLayout.CENTER);
                offerPanel.add(owner, BorderLayout.SOUTH);
                offerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

                offerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        submitValue(String.valueOf(offerIndex + 1));
                    }
                });

                promptOptions.add(offerPanel);
            }

            promptPanel.revalidate();
            promptPanel.repaint();
        });
    }

    public void promptVisibleChoice(Offer offer) {
        List<ButtonSpec> buttons = List.of(
                new ButtonSpec("Carte visible", "v", offer.getFaceUp(), true),
                new ButtonSpec("Carte cachee", "c", offer.getFaceDown(), false)
        );
        showButtons("Visible ou cachee ?", buttons);
    }

    public void promptOwnOfferChoice(Player player, Offer offer) {
        List<ButtonSpec> buttons = List.of(
                new ButtonSpec("Visible", "1", offer.getFaceUp(), true),
                new ButtonSpec("Cachee", "2", offer.getFaceDown(), false)
        );
        showButtons(player.getName() + " - Choisissez une carte :", buttons);
    }

    public void promptBmType() {
        List<ButtonSpec> buttons = List.of(
                new ButtonSpec("Bonus", "1"),
                new ButtonSpec("Malus", "2")
        );
        showButtons("Type de carte BM :", buttons);
    }

    public void promptBmValue(model.extension.BMCard.BMType type) {
        if (type == model.extension.BMCard.BMType.BONUS) {
            showSpinnerPrompt("Valeur du Bonus (1-2) :", 1, 2, 1);
        } else {
            showSpinnerPrompt("Valeur du Malus (1-3) :", 1, 3, 1);
        }
    }
    
    public void promptBmTarget(Player smallestJestPlayer, List<Player> allPlayers) {
        List<ButtonSpec> buttons = new ArrayList<>();
        for (Player player : allPlayers) {
            buttons.add(new ButtonSpec(player.getName(), String.valueOf(allPlayers.indexOf(player) + 1)));
        }
        showButtons(smallestJestPlayer.getName() + " - Choisissez le destinataire de la carte BM :", buttons);
    }

    private static class ButtonSpec {
        String label;
        String value;
        Card card;
        boolean faceUp = true;

        ButtonSpec(String label, String value) {
            this.label = label;
            this.value = value;
        }

        ButtonSpec(String label, String value, Card card, boolean faceUp) {
            this.label = label;
            this.value = value;
            this.card = card;
            this.faceUp = faceUp;
        }
    }

    /**
     * Affiche le resultat de la carte BM.
     */
    public void displayBmApplication(Player target, BMCard card, int delta) {
        if (target == null || card == null) return;
        String effect = delta >= 0 ? "gagne " : "perd ";
        String message = "Extension BM : " + target.getName() + " " + effect + Math.abs(delta) + " points.";
        log(message);
        preparePrompt(message);
        updateGameState();
    }

    private void runSync(Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(action);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateGameState() {
        if (game == null) return;
        runSync(() -> {
            // Garder le panneau des trophées, supprimer seulement les panneaux des joueurs
            for (int i = gameStatePanel.getComponentCount() - 1; i >= 0; i--) {
                Component comp = gameStatePanel.getComponent(i);
                if (comp != trophiesPanel) {
                    gameStatePanel.remove(i);
                }
            }
            
            int playerIndex = 0;
            for (Player player : game.getPlayers()) {
                JPanel playerPanel = new JPanel();
                playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
                playerPanel.setBackground(BG_SECONDARY);
                playerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 1),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
                
                // Nom du joueur
                JLabel nameLabel = new JLabel(player.getName());
                nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                nameLabel.setForeground(ACCENT);
                nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                playerPanel.add(nameLabel);
                
                playerPanel.add(Box.createVerticalStrut(5));
                
                // Score
                JLabel scoreLabel = new JLabel("Score: " + player.getFinalScore() + " pts");
                scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                scoreLabel.setForeground(TEXT_PRIMARY);
                scoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                playerPanel.add(scoreLabel);
                
                playerPanel.add(Box.createVerticalStrut(8));
                
                // Cartes
                JLabel cardsTitle = new JLabel("Cartes:");
                cardsTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
                cardsTitle.setForeground(ACCENT);
                cardsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
                playerPanel.add(cardsTitle);
                
                playerPanel.add(Box.createVerticalStrut(3));

                JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
                cardsPanel.setBackground(BG_SECONDARY);
                cardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                // Afficher les cartes normales
                for (Card card : player.getJest().getCards()) {
                    if (card instanceof TrophyCard) {
                        continue;
                    }
                    
                    JLabel cardLabel = new JLabel();
                    ImageIcon icon = CardImages.get(card, true);
                    if (icon != null) {
                        cardLabel.setIcon(icon);
                    } else {
                        cardLabel.setText(card.toString());
                        cardLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
                        cardLabel.setForeground(TEXT_SECONDARY);
                    }
                    cardsPanel.add(cardLabel);
                }

                JScrollPane cardsScroll = new JScrollPane(cardsPanel);
                cardsScroll.setPreferredSize(new Dimension(0, 180));
                cardsScroll.getViewport().setBackground(BG_SECONDARY);
                cardsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                cardsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                cardsScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
                cardsScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
                playerPanel.add(cardsScroll);
                
                // Afficher les trophées gagnés à la fin
                List<TrophyCard> playerTrophies = new ArrayList<>();
                for (Card card : player.getJest().getCards()) {
                    if (card instanceof TrophyCard) {
                        playerTrophies.add((TrophyCard) card);
                    }
                }
                
                if (!playerTrophies.isEmpty()) {
                    playerPanel.add(Box.createVerticalStrut(8));
                    
                    JLabel trophiesTitle = new JLabel("Trophées gagnés:");
                    trophiesTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    trophiesTitle.setForeground(ACCENT);
                    trophiesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
                    playerPanel.add(trophiesTitle);
                    
                    playerPanel.add(Box.createVerticalStrut(3));
                    
                    JPanel trophiesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
                    trophiesPanel.setBackground(BG_SECONDARY);
                    trophiesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    for (TrophyCard trophy : playerTrophies) {
                        JPanel trophyItem = new JPanel(new BorderLayout(3, 3));
                        trophyItem.setBackground(BG_SECONDARY);
                        trophyItem.setBorder(BorderFactory.createLineBorder(ACCENT, 1));
                        
                        // Afficher la carte originale du trophée
                        Card originalCard = trophy.getOriginalCard();
                        if (originalCard != null && !(originalCard instanceof TrophyCard)) {
                            ImageIcon icon = CardImages.get(originalCard, true);
                            if (icon != null) {
                                JLabel cardLabel = new JLabel(icon);
                                trophyItem.add(cardLabel, BorderLayout.CENTER);
                            }
                        }
                        
                        trophiesPanel.add(trophyItem);
                    }
                    
                    playerPanel.add(trophiesPanel);
                }
                playerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, playerPanel.getPreferredSize().height));
                gameStatePanel.add(playerPanel);
                gameStatePanel.add(Box.createVerticalStrut(10));
                
                playerIndex++;
            }
            gameStatePanel.revalidate();
            gameStatePanel.repaint();
        });
    }

    /**
     * Affiche les offres sur l interface.
     */
    public void displayOffers(List<Offer> offers) {
        this.currentOffers = offers;
        runSync(() -> {
            promptLabel.setText("Offres disponibles");
            promptOptions.removeAll();
            
            for (Offer offer : offers) {
                JPanel cardPanel = new JPanel(new BorderLayout(8, 8));
                cardPanel.setBackground(BG_SECONDARY);
                cardPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));

                // Ligne des deux cartes : visible + cachée
                JPanel cardsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
                cardsRow.setBackground(BG_SECONDARY);

                JLabel visibleLabel = new JLabel();
                ImageIcon visibleIcon = CardImages.get(offer.getFaceUp(), true);
                if (visibleIcon != null) {
                    visibleLabel.setIcon(visibleIcon);
                } else if (offer.getFaceUp() != null) {
                    visibleLabel.setText(offer.getFaceUp().toString());
                    visibleLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    visibleLabel.setForeground(TEXT_SECONDARY);
                }

                JLabel hiddenLabel = new JLabel();
                ImageIcon hiddenIcon = CardImages.get(offer.getFaceDown(), false);
                if (hiddenIcon != null) {
                    hiddenLabel.setIcon(hiddenIcon);
                } else if (offer.getFaceDown() != null) {
                    hiddenLabel.setText("Cachée");
                    hiddenLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));
                    hiddenLabel.setForeground(TEXT_SECONDARY);
                }

                cardsRow.add(visibleLabel);
                cardsRow.add(hiddenLabel);
                
                JLabel owner = new JLabel(offer.getOwner().getName(), SwingConstants.CENTER);
                owner.setFont(new Font("SansSerif", Font.BOLD, 12));
                owner.setForeground(TEXT_PRIMARY);
                
                cardPanel.add(cardsRow, BorderLayout.CENTER);
                cardPanel.add(owner, BorderLayout.SOUTH);
                promptOptions.add(cardPanel);
            }
            promptPanel.revalidate();
            promptPanel.repaint();
        });
        updateGameState();
    }

    /**
     * Affiche les trophees tires au debut de partie.
     */
    public void displayTrophies(List<TrophyCard> trophies) {
        runSync(() -> {
            promptLabel.setText("Trophées pour cette partie");
            promptOptions.removeAll();

            if (trophies == null || trophies.isEmpty()) {
                JLabel label = new JLabel("Aucun trophée pour cette variante");
                label.setFont(new Font("SansSerif", Font.ITALIC, 12));
                label.setForeground(TEXT_SECONDARY);
                promptOptions.add(label);
            } else {
                for (TrophyCard trophy : trophies) {
                    JPanel trophyPanel = new JPanel(new BorderLayout());
                    trophyPanel.setBackground(BG_SECONDARY);
                    trophyPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                    ));
                    
                    JLabel label = new JLabel(trophy.getName());
                    label.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    label.setForeground(TEXT_PRIMARY);
                    
                    trophyPanel.add(label, BorderLayout.CENTER);
                    promptOptions.add(trophyPanel);
                }
            }

            promptPanel.revalidate();
            promptPanel.repaint();
        });
    }

    /**
     * Ajoute un message au log.
     */
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    /**
     * Met a jour le status.
     */
    public void setStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }

    // Implementation GameObserver
    
    @Override
    public void onRoundStart(int roundNumber) {
        setStatus("Tour " + roundNumber);
        log("=== Debut du tour " + roundNumber + " ===");
    }

    @Override
    public void onOffersCreated(List<Offer> offers) {
        displayOffers(offers);
        log("Offres du tour :");
        for (Offer offer : offers) {
            log(" - " + offer.getOwner().getName() + " a cree son offre.");
        }
        log("A vous de choisir parmi les offres que vous souhaitez.");
    }

    @Override
    public void onCardTaken(Player player, Card card) {
        log(player.getName() + " a pris : " + card);
        updateGameState();
    }

    @Override
    public void onGameEnd(Player winner) {
        if (winner != null) {
            String message = "GAGNANT : " + winner.getName() + " (" + winner.getFinalScore() + " points)";
            preparePrompt(message);
            log("=== FIN DE PARTIE ===");
            log(message);
        } else {
            preparePrompt("Égalité parfaite");
            log("=== FIN DE PARTIE ===");
            log("Égalité parfaite");
        }
        updateGameState();
    }

    @Override
    public void onMessage(String message) {
        log(message);
    }
    
    /**
     * Demande la carte BM au joueur avec le plus petit Jest.
     */
    public model.extension.BMCard askBmCard(Player smallestJestPlayer, List<Player> allPlayers) {
        promptBmTarget(smallestJestPlayer, allPlayers);
        String targetChoice = ConsoleView.SafeInput.readChoice("Joueur (1-" + allPlayers.size() + ") : ", "1", String.valueOf(allPlayers.size()));
        Player target = allPlayers.get(Integer.parseInt(targetChoice) - 1);
        
        promptBmType();
        String typeChoice = ConsoleView.SafeInput.readChoice("Type (1-2) : ", "1", "2");
        
        model.extension.BMCard.BMType type = typeChoice.equals("1") ? 
            model.extension.BMCard.BMType.BONUS : model.extension.BMCard.BMType.MALUS;
        
        promptBmValue(type);
        
        String valueStr;
        if (type == model.extension.BMCard.BMType.BONUS) {
            valueStr = ConsoleView.SafeInput.readChoice("Valeur du Bonus (1-2) : ", "1", "2");
        } else {
            valueStr = ConsoleView.SafeInput.readChoice("Valeur du Malus (1-3) : ", "1", "3");
        }
        int value = Integer.parseInt(valueStr);
        
        model.extension.BMCard card = new model.extension.BMCard(type, value);
        card.setTarget(target);
        return card;
    }

    /**
     * Sauvegarde la partie en cours.
     */
    private void saveGame() {
        if (game == null) {
            JOptionPane.showMessageDialog(this, "Aucune partie en cours à sauvegarder.", "Sauvegarde", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sauvegarder la partie");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers de sauvegarde (*.jest)", "jest"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".jest")) {
                    filePath += ".jest";
                }
                
                java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                    new java.io.FileOutputStream(filePath)
                );
                oos.writeObject(game);
                oos.close();
                
                log("Partie sauvegardée : " + filePath);
                JOptionPane.showMessageDialog(this, "Partie sauvegardée avec succès !", "Sauvegarde", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                log("Erreur lors de la sauvegarde : " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Affiche les trophées dans le panneau d'état.
     */
    public void displayTrophiesInState(List<TrophyCard> trophies) {
        this.currentTrophies = trophies;
        runSync(() -> {
            // Nettoyer les anciens trophées (garder le titre et l'espacement)
            Component[] components = trophiesPanel.getComponents();
            for (int i = 2; i < components.length; i++) {
                trophiesPanel.remove(i);
            }
            
            if (trophies != null && !trophies.isEmpty()) {
                // Afficher les cartes côte à côte
                JPanel cardsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                cardsRow.setBackground(BG_SECONDARY);
                cardsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                for (TrophyCard trophy : trophies) {
                    JPanel trophyItem = new JPanel(new BorderLayout(5, 5));
                    trophyItem.setBackground(BG_SECONDARY);
                    trophyItem.setBorder(BorderFactory.createLineBorder(BORDER, 1));
                    
                    // Afficher la carte originale si disponible
                    Card originalCard = trophy.getOriginalCard();
                    if (originalCard != null && !(originalCard instanceof TrophyCard)) {
                        ImageIcon icon = CardImages.get(originalCard, true);
                        if (icon != null) {
                            JLabel cardLabel = new JLabel(icon);
                            trophyItem.add(cardLabel, BorderLayout.CENTER);
                        }
                    }
                    
                    // Afficher le nom du trophée en dessous
                    JLabel trophyLabel = new JLabel(trophy.getName(), SwingConstants.CENTER);
                    trophyLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    trophyLabel.setForeground(TEXT_PRIMARY);
                    trophyItem.add(trophyLabel, BorderLayout.SOUTH);
                    
                    cardsRow.add(trophyItem);
                }
                
                trophiesPanel.add(cardsRow);
            } else {
                JLabel noTrophyLabel = new JLabel("Aucun trophée");
                noTrophyLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
                noTrophyLabel.setForeground(TEXT_SECONDARY);
                noTrophyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                trophiesPanel.add(noTrophyLabel);
            }
            
            gameStatePanel.revalidate();
            gameStatePanel.repaint();
        });
    }

    /**
     * Affiche l'interface.
     */
    public void display() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    /**
     * Affiche un dialogue de demarrage pour choisir entre nouvelle partie ou charger.
     * @return 1 pour nouvelle partie, 2 pour charger une partie
     */
    public int showStartupDialog() {
        JDialog dialog = new JDialog(this, "Jest - Demarrage", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Bienvenue dans Jest !");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonsPanel.setBackground(BG_PRIMARY);
        
        final int[] choice = {0};
        
        JButton newGameButton = createStyledButton("Nouvelle Partie");
        newGameButton.addActionListener(e -> {
            choice[0] = 1;
            dialog.dispose();
        });
        
        JButton loadGameButton = createStyledButton("Charger une Partie");
        loadGameButton.addActionListener(e -> {
            choice[0] = 2;
            dialog.dispose();
        });
        
        buttonsPanel.add(newGameButton);
        buttonsPanel.add(loadGameButton);
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        dialog.add(panel);
        dialog.setVisible(true);
        
        return choice[0];
    }

    /**
     * Demande le nom du fichier a charger.
     * @return le nom du fichier
     */
    public String askLoadFilename() {
        String filename = JOptionPane.showInputDialog(this, 
            "Entrez le nom du fichier a charger :", 
            "Charger une partie",
            JOptionPane.QUESTION_MESSAGE);
        return filename != null ? filename : "";
    }
}
