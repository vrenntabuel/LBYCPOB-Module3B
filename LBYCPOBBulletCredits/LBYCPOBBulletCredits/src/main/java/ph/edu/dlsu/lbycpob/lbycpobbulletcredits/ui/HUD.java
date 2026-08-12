package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.VPos;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.PlayerComponent;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventBus;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventListener;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventType;


import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * HUD.java
 * ========
 * The on-screen display: a stats panel (lives/HP), a wave-title banner,
 * and a centered modal panel for bigger messages (title screen, pause,
 * game over, victory, narrative lines). Implements GameEventListener so
 * it updates itself reactively through the OBSERVER pattern rather than
 * every other class needing a direct reference to it.
 * <p>
 * VISUAL STYLE:
 * Everything here shares one small "indie game UI" vocabulary - dark
 * semi-transparent rounded panels, a thin glowing accent border, a
 * condensed monospace font for that retro-arcade feel, and a two-color
 * accent scheme (cyan for normal information, gold for emphasis/danger) -
 * rather than plain unstyled Text nodes floating over the game world.
 */
public class HUD implements GameEventListener {

    private static final Color PANEL_FILL = Color.rgb(8, 10, 22, 0.82);
    private static final Color ACCENT_CYAN = Color.rgb(90, 220, 255);
    private static final Color ACCENT_GOLD = Color.rgb(255, 210, 90);

    private static final String UI_FONT = "Consolas";

    /**
     * Every piece of HUD text uses this SAME font size, so the stats
     * panel, wave banner, and message panel all read as one consistent
     * typographic system rather than an arbitrary mix of sizes. Emphasis
     * between them comes from color (cyan vs. gold) and panel styling
     * instead of varying the font size.
     */
    private static final double HUD_FONT_SIZE = 22;

    // --- Stats panel (bottom-left): lives + HP ---
    private final Rectangle statsPanel = new Rectangle();
    private final Text statsText = new Text();

    // --- Wave title banner (top-center), auto-fades after a few seconds ---
    private final Rectangle waveBanner = new Rectangle();
    private final Text waveBannerText = new Text();
    private final SequentialTransition waveFadeSequence;

    // --- Centered modal message panel (title / pause / game over / victory / narrative) ---
    private final Rectangle messagePanel = new Rectangle();
    private final Text messageText = new Text();

    private PlayerComponent player;

    public HUD() {
        buildStatsPanel();
        waveFadeSequence = buildWaveBanner();
        buildMessagePanel();
    }

    private void buildStatsPanel() {
        statsPanel.setArcWidth(18);
        statsPanel.setArcHeight(18);
        statsPanel.setFill(PANEL_FILL);
        statsPanel.setStroke(ACCENT_CYAN);
        statsPanel.setStrokeWidth(1.5);
        statsPanel.setWidth(250);
        statsPanel.setHeight(72);
        statsPanel.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.6)));

        statsText.setFont(Font.font(UI_FONT, FontWeight.BOLD, HUD_FONT_SIZE));
        statsText.setFill(ACCENT_CYAN);
        statsText.setTextOrigin(VPos.TOP);
    }

    private SequentialTransition buildWaveBanner() {
        waveBanner.setArcWidth(16);
        waveBanner.setArcHeight(16);
        waveBanner.setFill(PANEL_FILL);
        waveBanner.setStroke(ACCENT_GOLD);
        waveBanner.setStrokeWidth(2);
        waveBanner.setEffect(new DropShadow(14, Color.rgb(0, 0, 0, 0.6)));
        waveBanner.setVisible(false);

        waveBannerText.setFont(Font.font(UI_FONT, FontWeight.BOLD, HUD_FONT_SIZE));
        waveBannerText.setFill(ACCENT_GOLD);
        waveBannerText.setTextOrigin(VPos.TOP);
        waveBannerText.setTextAlignment(TextAlignment.CENTER);
        waveBannerText.setEffect(new DropShadow(6, Color.BLACK));
        waveBannerText.setVisible(false);

        // A single reusable animation: hold fully visible for a beat, then
        // fade both the banner background and its text out together. Built
        // ONCE and restarted (via stop() + playFromStart()) on every
        // showWaveTitle() call, rather than creating fresh Transition
        // objects each time - which would let multiple fades run
        // concurrently on the same nodes and flicker if waves start in
        // quick succession.
        PauseTransition hold = new PauseTransition(Duration.seconds(1.8));

        FadeTransition fadeBanner = new FadeTransition(Duration.seconds(0.8), waveBanner);
        fadeBanner.setToValue(0.0);
        FadeTransition fadeText = new FadeTransition(Duration.seconds(0.8), waveBannerText);
        fadeText.setToValue(0.0);
        ParallelTransition fadeBoth = new ParallelTransition(fadeBanner, fadeText);

        SequentialTransition sequence = new SequentialTransition(hold, fadeBoth);
        sequence.setOnFinished(_ -> {
            waveBanner.setVisible(false);
            waveBannerText.setVisible(false);
        });
        return sequence;
    }

    private void buildMessagePanel() {
        messagePanel.setArcWidth(28);
        messagePanel.setArcHeight(28);
        messagePanel.setFill(PANEL_FILL);
        messagePanel.setStroke(ACCENT_CYAN);
        messagePanel.setStrokeWidth(2.5);
        messagePanel.setEffect(new DropShadow(24, Color.rgb(0, 0, 0, 0.75)));
        messagePanel.setVisible(false);

        messageText.setFont(Font.font(UI_FONT, FontWeight.BOLD, HUD_FONT_SIZE));
        messageText.setFill(Color.WHITESMOKE);
        messageText.setTextOrigin(VPos.TOP);
        messageText.setTextAlignment(TextAlignment.CENTER);
        messageText.setEffect(new DropShadow(8, Color.BLACK));
        messageText.setVisible(false);
    }

    /** Adds the HUD's nodes to the FXGL scene overlay and lays them out - call once during setup. */
    public void attachToScene() {
        // Bottom-left corner, with a small margin.
        statsPanel.setTranslateX(20);
        statsPanel.setTranslateY(getAppHeight() - statsPanel.getHeight() - 20);
        statsText.setTranslateX(statsPanel.getTranslateX() + 18);
        statsText.setTranslateY(statsPanel.getTranslateY() + 14);

        getGameScene().addUINode(statsPanel);
        getGameScene().addUINode(statsText);
        getGameScene().addUINode(waveBanner);
        getGameScene().addUINode(waveBannerText);
        getGameScene().addUINode(messagePanel);
        getGameScene().addUINode(messageText);

        GameEventBus.getInstance().subscribe(this);
    }

    public void bindPlayer(PlayerComponent player) {
        this.player = player;
        refreshStatsText();
    }

    private void refreshStatsText() {
        if (player != null) {
            statsText.setText("LIVES  " + player.getLivesRemaining()
                    + "\nHULL   " + player.getCurrentHealth() + " / " + player.getMaxHealth());
        }
    }

    /**
     * Shows a wave-title banner centered near the top of the screen, then
     * automatically fades it out after a couple of seconds - a small bit
     * of "juice" so it announces the moment without permanently cluttering
     * the screen for the rest of the wave.
     */
    public void showWaveTitle(String title) {
        waveBannerText.setText(title);
        waveBannerText.applyCss();

        double textWidth = waveBannerText.getLayoutBounds().getWidth();
        double textHeight = waveBannerText.getLayoutBounds().getHeight();
        double paddingX = 32;
        double paddingY = 16;

        double bannerWidth = textWidth + paddingX * 2;
        double bannerHeight = textHeight + paddingY * 2;

        waveBanner.setWidth(bannerWidth);
        waveBanner.setHeight(bannerHeight);
        waveBanner.setTranslateX((getAppWidth() - bannerWidth) / 2.0);
        waveBanner.setTranslateY(30);

        waveBannerText.setTranslateX((getAppWidth() - textWidth) / 2.0);
        waveBannerText.setTranslateY(30 + paddingY);

        waveBanner.setOpacity(1.0);
        waveBannerText.setOpacity(1.0);
        waveBanner.setVisible(true);
        waveBannerText.setVisible(true);

        // Stop any fade still in progress from a previous wave title
        // before restarting, so quick successive waves never leave two
        // fades fighting over the same node's opacity.
        waveFadeSequence.stop();
        waveFadeSequence.playFromStart();
    }

    /**
     * Shows a message inside the centered modal panel - used for the
     * title screen, pause prompt, game over / victory text, and narrative
     * lines. The panel auto-sizes (with a sensible max width and word
     * wrap for longer text) and is always centered on screen.
     */
    public void showMessage(String text) {
        double maxPanelWidth = Math.min(getAppWidth() * 0.62, 680);
        double paddingX = 44;
        double paddingY = 36;
        double contentWidth = maxPanelWidth - paddingX * 2;

        messageText.setWrappingWidth(contentWidth);
        messageText.setText(text);
        messageText.applyCss();

        double textHeight = messageText.getLayoutBounds().getHeight();
        double panelWidth = maxPanelWidth;
        double panelHeight = textHeight + paddingY * 2;

        messagePanel.setWidth(panelWidth);
        messagePanel.setHeight(panelHeight);

        double panelX = (getAppWidth() - panelWidth) / 2.0;
        double panelY = (getAppHeight() - panelHeight) / 2.0;
        messagePanel.setTranslateX(panelX);
        messagePanel.setTranslateY(panelY);

        messageText.setTranslateX(panelX + paddingX);
        messageText.setTranslateY(panelY + paddingY);

        messagePanel.setOpacity(1.0);
        messageText.setOpacity(1.0);
        messagePanel.setVisible(true);
        messageText.setVisible(true);
    }

    public void hideMessage() {
        messagePanel.setVisible(false);
        messageText.setVisible(false);
    }

    @Override
    public void onGameEvent(GameEventType event, Object payload) {
        switch (event) {
            case PLAYER_DAMAGED:
            case PLAYER_LIFE_LOST:
                refreshStatsText();
                break;
            case PLAYER_DIED:
                showMessage("GAME OVER\n\nPress R to retry");
                break;
            case WAVE_STARTED:
                showWaveTitle(String.valueOf(payload));
                break;
            case GAME_VICTORY:
                // Intentionally no showMessage() call here - VictoryState
                // sets its own exact message text directly (including the
                // "press R / ESC" instructions), immediately after
                // publishing this same event. Handling it here too would
                // just show a message that gets instantly overwritten.
                break;
            case NARRATIVE_LINE:
                showMessage(String.valueOf(payload));
                break;
            default:
                break;
        }
    }
}
