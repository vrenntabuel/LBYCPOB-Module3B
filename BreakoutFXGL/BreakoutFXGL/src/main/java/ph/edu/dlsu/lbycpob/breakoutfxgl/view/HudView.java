package ph.edu.dlsu.lbycpob.breakoutfxgl.view;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/**
 * HudView is a small, self-contained piece of the VIEW layer whose
 * only job is to build and update the on-screen scoreboard and the
 * "Game Over" / "Level Cleared" message card, in a light, indie-game
 * style (rounded cards, soft drop shadows, a calm color palette).
 * <p>
 * Keeping this UI-building code in its own class instead of stuffing
 * it into BreakoutApp keeps that class focused on wiring the game
 * loop together, and makes it obvious where to look if you only want
 * to restyle the HUD.
 * <p>
 * No emoji or special Unicode symbols are used anywhere in this class
 * - "lives remaining" is shown with plain JavaFX Circle shapes
 * instead of a heart symbol, so everything here stays fully typable
 * plain text and shapes.
 */
public class HudView {

    // A calm, light, "indie game" palette - readable on a white background.
    private static final Color CARD_BACKGROUND = Color.web("#ffffff");
    private static final Color CARD_BORDER = Color.web("#dfe6e9");
    private static final Color TEXT_DARK = Color.web("#2d3436");
    private static final Color TEXT_MUTED = Color.web("#636e72");
    private static final Color ACCENT_BLUE = Color.web("#0984e3");
    private static final Color LIFE_FILLED = Color.web("#0984e3");
    private static final Color LIFE_EMPTY = Color.web("#dfe6e9");

    private final double fieldWidth;
    private final double fieldHeight;
    private final int maxLives;

    private final StackPane hudRoot;
    private final Text scoreValueText;
    private final HBox livesBox;
    private final List<Circle> lifeDots = new ArrayList<>();

    private final StackPane statusOverlay;
    private final Text statusTitleText;
    private final Text statusSubtitleText;
    private final Rectangle cardRectangle;
    private final Rectangle dimRectangle;

    public HudView(double fieldWidth, double fieldHeight, int maxLives) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.maxLives = maxLives;

        // --- Top scoreboard bar -------------------------------------------------
        Rectangle barBackground = new Rectangle(fieldWidth - 40, 56);
        barBackground.setArcWidth(18);
        barBackground.setArcHeight(18);
        barBackground.setFill(CARD_BACKGROUND);
        barBackground.setStroke(CARD_BORDER);
        barBackground.setStrokeWidth(1.5);
        barBackground.setEffect(new DropShadow(10, Color.rgb(45, 52, 54, 0.15)));

        Text scoreLabel = new Text("SCORE");
        scoreLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        scoreLabel.setFill(TEXT_MUTED);

        scoreValueText = new Text("0");
        scoreValueText.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        scoreValueText.setFill(ACCENT_BLUE);

        VBox scoreBlock = new VBox(0, scoreLabel, scoreValueText);
        scoreBlock.setAlignment(Pos.CENTER_LEFT);

        Text livesLabel = new Text("LIVES");
        livesLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        livesLabel.setFill(TEXT_MUTED);

        livesBox = new HBox(6);
        livesBox.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < maxLives; i++) {
            Circle dot = new Circle(6, LIFE_FILLED);
            lifeDots.add(dot);
            livesBox.getChildren().add(dot);
        }

        VBox livesBlock = new VBox(4, livesLabel, livesBox);
        livesBlock.setAlignment(Pos.CENTER_RIGHT);

        Text titleText = new Text("LBYCPOB BREAKOUT");
        titleText.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        titleText.setFill(TEXT_DARK);

        HBox barContent = new HBox();
        barContent.setAlignment(Pos.CENTER);
        barContent.setSpacing(0);
        barContent.setPrefWidth(fieldWidth - 40 - 32); // leave inner padding
        HBox.setHgrow(titleText, javafx.scene.layout.Priority.ALWAYS);

        HBox leftSpacer = new HBox(scoreBlock);
        leftSpacer.setAlignment(Pos.CENTER_LEFT);
        leftSpacer.setPrefWidth((fieldWidth - 40 - 32) / 3.0);

        HBox centerSpacer = new HBox(titleText);
        centerSpacer.setAlignment(Pos.CENTER);
        centerSpacer.setPrefWidth((fieldWidth - 40 - 32) / 3.0);

        HBox rightSpacer = new HBox(livesBlock);
        rightSpacer.setAlignment(Pos.CENTER_RIGHT);
        rightSpacer.setPrefWidth((fieldWidth - 40 - 32) / 3.0);

        barContent.getChildren().addAll(leftSpacer, centerSpacer, rightSpacer);

        StackPane bar = new StackPane(barBackground, barContent);
        StackPane.setAlignment(barContent, Pos.CENTER);
        bar.setPrefSize(fieldWidth - 40, 56);

        hudRoot = new StackPane(bar);
        hudRoot.setPrefSize(fieldWidth, 76);
        hudRoot.setAlignment(Pos.TOP_CENTER);
        hudRoot.setMouseTransparent(true);

        // --- Center status overlay (Game Over / Level Cleared) -----------------
        Rectangle dimBackground = new Rectangle(fieldWidth, fieldHeight);
        dimBackground.setFill(Color.rgb(45, 52, 54, 0.0)); // fully transparent until shown

        Rectangle card = new Rectangle(360, 150);
        card.setArcWidth(24);
        card.setArcHeight(24);
        card.setFill(CARD_BACKGROUND);
        card.setStroke(CARD_BORDER);
        card.setStrokeWidth(1.5);
        card.setEffect(new DropShadow(20, Color.rgb(45, 52, 54, 0.25)));
        card.setVisible(false);

        statusTitleText = new Text("");
        statusTitleText.setFont(Font.font("Verdana", FontWeight.BOLD, 26));
        statusTitleText.setFill(TEXT_DARK);
        statusTitleText.setTextAlignment(TextAlignment.CENTER);

        statusSubtitleText = new Text("");
        statusSubtitleText.setFont(Font.font("Verdana", 14));
        statusSubtitleText.setFill(TEXT_MUTED);
        statusSubtitleText.setTextAlignment(TextAlignment.CENTER);

        VBox cardText = new VBox(10, statusTitleText, statusSubtitleText);
        cardText.setAlignment(Pos.CENTER);

        StackPane cardStack = new StackPane(card, cardText);
        cardStack.setPrefSize(360, 150);

        statusOverlay = new StackPane(dimBackground, cardStack);
        statusOverlay.setPrefSize(fieldWidth, fieldHeight);
        statusOverlay.setAlignment(Pos.CENTER);
        statusOverlay.setMouseTransparent(true);

        // Keep a reference to the card/background so showStatus()/clearStatus() can toggle them
        // later without needing to re-search the scene graph.
        this.cardRectangle = card;
        this.dimRectangle = dimBackground;
    }

    public javafx.scene.Node getHudRoot() {
        return hudRoot;
    }

    public javafx.scene.Node getStatusOverlay() {
        return statusOverlay;
    }

    public void updateScore(int score) {
        scoreValueText.setText(String.valueOf(score));
    }

    public void updateLives(int lives) {
        for (int i = 0; i < lifeDots.size(); i++) {
            lifeDots.get(i).setFill(i < lives ? LIFE_FILLED : LIFE_EMPTY);
        }
    }

    /** Shows the semi-transparent card with a title and subtitle, e.g. "GAME OVER" / "Press R to Restart". */
    public void showStatus(String title, String subtitle) {
        statusTitleText.setText(title);
        statusSubtitleText.setText(subtitle);
        cardRectangle.setVisible(true);
        dimRectangle.setFill(Color.rgb(45, 52, 54, 0.35));
    }

    /** Hides the status card, used when a fresh game starts (restart). */
    public void clearStatus() {
        statusTitleText.setText("");
        statusSubtitleText.setText("");
        cardRectangle.setVisible(false);
        dimRectangle.setFill(Color.rgb(45, 52, 54, 0.0));
    }
}
