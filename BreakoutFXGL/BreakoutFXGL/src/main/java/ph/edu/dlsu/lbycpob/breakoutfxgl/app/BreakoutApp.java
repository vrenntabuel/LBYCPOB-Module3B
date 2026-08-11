package ph.edu.dlsu.lbycpob.breakoutfxgl.app;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import ph.edu.dlsu.lbycpob.breakoutfxgl.audio.AudioManager;
import ph.edu.dlsu.lbycpob.breakoutfxgl.controller.GameManager;
import ph.edu.dlsu.lbycpob.breakoutfxgl.model.*;
import ph.edu.dlsu.lbycpob.breakoutfxgl.view.BrickView;
import ph.edu.dlsu.lbycpob.breakoutfxgl.view.BrickViewFactory;
import ph.edu.dlsu.lbycpob.breakoutfxgl.view.EntityViewFactory;
import ph.edu.dlsu.lbycpob.breakoutfxgl.view.HudView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * BreakoutApp is the VIEW (and application entry point) in our MVC
 * design. Its responsibilities are strictly about drawing and input:
 * <p>
 * - It creates and owns a GameManager (the Controller).
 * - It turns keyboard events into calls on GameManager
 * (EVENT HANDLING).
 * - Its onUpdate(tpf) method IS the ANIMATION LOOP: FXGL calls it
 * automatically about 60 times per second.
 * - Each frame, it asks GameManager to update the model, then it
 * copies the model's current position/state onto the on-screen
 * FXGL Entities so the player sees the new positions.
 * <p>
 * Notice this class never contains game RULES (no collision math, no
 * scoring math) - all of that lives in the Model/Controller classes,
 * and even most of the DRAWING details live in the view package
 * (BrickViewFactory, EntityViewFactory, HudView). This class is
 * mainly "glue": it decides WHEN those pieces run, not HOW they work.
 */
public class BreakoutApp extends GameApplication {

    private static final int FIELD_WIDTH = 800;
    private static final int FIELD_HEIGHT = 800;

    // A light, soft background
    private static final Color BACKGROUND_COLOR = Color.web("#f5f6fa");

    private GameManager gameManager;

    private final BrickViewFactory brickViewFactory = new BrickViewFactory();
    private final EntityViewFactory entityViewFactory = new EntityViewFactory();
    private HudView hudView;

    // Maps each non-brick Model object to the FXGL Entity that visually
    // represents it on screen (ball, paddle, power-up), so we know which
    // shape to move each frame and which shape to remove when the model
    // object is gone. Bricks are tracked separately (see brickViews)
    // because they also need appearance (not just position) updates.
    private final Map<GameObject, Entity> entityMap = new HashMap<>();
    private final Map<Brick, BrickView> brickViews = new HashMap<>();

    // The paddle's width changes at runtime (widen/shrink power-ups), so we
    // keep a direct reference to its Rectangle to resize it every frame.
    private EntityViewFactory.PaddleView paddleView;

    // Owns the background music and sound effects. Created once in
    // initGame() (after the JavaFX platform has started) and reused across
    // restarts - only GameManager gets recreated each time buildNewGame()
    // runs, not the music.
    private AudioManager audioManager;

    static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(FIELD_WIDTH);
        settings.setHeight(FIELD_HEIGHT);
        settings.setTitle("Breakout - LBYCPOB OOP Sample");
        settings.setVersion("3.0");
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(BACKGROUND_COLOR);

        // Created here (not as a plain field initializer) because it uses
        // JavaFX media classes, which need the JavaFX platform to already
        // be running - initGame() is called after that has happened.
        audioManager = new AudioManager();

        buildNewGame();

        // Music should keep playing continuously across restarts (pressing
        // R rebuilds the level, but should not restart the soundtrack), so
        // this is started once here, not inside buildNewGame().
        audioManager.playBackgroundMusic();
    }

    /**
     * Creates a brand new GameManager and the on-screen entities for
     * its starting state. Used both the first time the game starts
     * (initGame()) and whenever the player restarts with R
     * (restartGame() below) - keeping this in one place means both
     * paths are guaranteed to set the game up identically.
     */
    private void buildNewGame() {
        // If we are rebuilding (restart), get rid of every entity left
        // over from the previous game first.
        for (Entity entity : entityMap.values()) {
            entity.removeFromWorld();
        }
        entityMap.clear();
        for (BrickView brickView : brickViews.values()) {
            brickView.getEntity().removeFromWorld();
        }
        brickViews.clear();

        gameManager = new GameManager(FIELD_WIDTH, FIELD_HEIGHT);

        // Every time a fresh GameManager is created (first launch, or a
        // restart via R), reconnect it to the audio system, so ball
        // bounces and brick breaks keep making sound after a restart too.
        if (audioManager != null) {
            gameManager.setGameEventListener(audioManager);
        }

        // Build the initial on-screen entity for the paddle and the
        // starting ball. Bricks and any balls/power-ups added later
        // are created lazily inside syncViewWithModel().
        createEntityForPaddle(gameManager.getPaddle());
        for (Ball ball : gameManager.getBalls()) {
            createEntityForBall(ball);
        }
        for (Brick brick : gameManager.getBricks()) {
            brickViews.put(brick, brickViewFactory.createBrickView(brick));
        }

        if (hudView != null) {
            hudView.clearStatus();
        }
    }

    @Override
    protected void initUI() {
        hudView = new HudView(FIELD_WIDTH, FIELD_HEIGHT, GameManager.getStartingLives());
        addUINode(hudView.getHudRoot(), 0, 0);
        addUINode(hudView.getStatusOverlay(), 0, 0);
    }

    /**
     * initInput() is where keyboard EVENT HANDLING is wired up.
     * We use UserAction so movement continues smoothly for as long as
     * the key is held down, instead of moving once per key press.
     */
    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                gameManager.movePaddleLeft(tpf());
            }
        }, KeyCode.LEFT);

        getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                gameManager.movePaddleRight(tpf());
            }
        }, KeyCode.RIGHT);

        // Alternate WASD-style keys, a common convenience for players.
        getInput().addAction(new UserAction("Move Left A") {
            @Override
            protected void onAction() {
                gameManager.movePaddleLeft(tpf());
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Move Right D") {
            @Override
            protected void onAction() {
                gameManager.movePaddleRight(tpf());
            }
        }, KeyCode.D);

        // R restarts the game. We use onActionBegin() instead of
        // onAction() here on purpose: onAction() fires every single
        // frame the key is held down, which would rebuild the game
        // over and over for as long as R stays pressed. onActionBegin()
        // fires exactly once, at the moment the key goes down.
        getInput().addAction(new UserAction("Restart Game") {
            @Override
            protected void onActionBegin() {
                buildNewGame();
            }
        }, KeyCode.R);

        // M toggles mute for both music and sound effects. onActionBegin()
        // is used for the same reason as Restart above - we want exactly
        // one toggle per key press, not one per frame the key is held.
        getInput().addAction(new UserAction("Toggle Mute") {
            @Override
            protected void onActionBegin() {
                if (audioManager != null) {
                    audioManager.toggleMute();
                }
            }
        }, KeyCode.M);
    }

    /**
     * onUpdate(tpf) is FXGL's ANIMATION LOOP callback: it fires once
     * per rendered frame. "tpf" stands for "time per frame", in
     * seconds, which is exactly the deltaSeconds our Model classes
     * expect.
     */
    @Override
    protected void onUpdate(double tpf) {
        if (gameManager.isGameOver()) {
            hudView.showStatus("GAME OVER", "Press R to Restart");
            return;
        }
        if (gameManager.isLevelCleared()) {
            hudView.showStatus("LEVEL CLEARED", "Press R to Restart");
            return;
        }

        gameManager.update(tpf);
        syncViewWithModel();
        hudView.updateScore(gameManager.getScore());
        hudView.updateLives(gameManager.getLives());
    }

    /**
     * Copies the current Model state onto the View (FXGL entities).
     * This is the heart of the MVC "glue": the Model never touches
     * FXGL classes, so all translation happens here, in one place.
     */
    private void syncViewWithModel() {
        // Paddle: always exists, just move it. We also resize its
        // Rectangle to match the model's current width every frame,
        // since widen()/shrink() power-ups change that width at
        // runtime - moving an Entity alone only ever repositions a
        // shape, it never resizes one.
        Paddle paddle = gameManager.getPaddle();
        moveEntity(paddle);
        if (paddleView != null) {
            paddleView.shape.setWidth(paddle.getWidth());
        }

        // Balls: some may be brand new (multi-ball power-up) and need
        // an entity created; the rest simply get repositioned.
        for (Ball ball : gameManager.getBalls()) {
            if (!entityMap.containsKey(ball)) {
                createEntityForBall(ball);
            } else {
                moveEntity(ball);
            }
        }
        removeGoneEntities(Ball.class, new HashSet<>(gameManager.getBalls()));

        // Bricks: destroyed bricks are already removed from
        // GameManager's ArrayList<Brick>; we remove their matching
        // views here, and refresh the appearance (before/after image
        // or fallback color) of the ones that remain.
        syncBrickViews();

        // Power-ups: new ones need an entity created; expired/collected
        // ones need their entity removed.
        for (PowerUp powerUp : gameManager.getPowerUps()) {
            if (!entityMap.containsKey(powerUp)) {
                createEntityForPowerUp(powerUp);
            } else {
                moveEntity(powerUp);
            }
        }
        removeGoneEntities(PowerUp.class, new HashSet<>(gameManager.getPowerUps()));
    }

    private void syncBrickViews() {
        Set<Brick> stillAliveBricks = new HashSet<>(gameManager.getBricks());

        // Remove views for any brick GameManager no longer has (destroyed).
        brickViews.keySet().removeIf(brick -> {
            if (stillAliveBricks.contains(brick)) {
                return false;
            }
            brickViews.get(brick).getEntity().removeFromWorld();
            return true;
        });

        // Bricks never move, so we only need to refresh their appearance
        // (in case a STRONG brick just took its first hit and should now
        // show its "damaged" image/color).
        for (Brick brick : gameManager.getBricks()) {
            BrickView view = brickViews.get(brick);
            if (view != null) {
                view.refresh(brick);
            }
        }
    }

    // -----------------------------------------------------------------
    // Entity creation helpers
    // -----------------------------------------------------------------

    private void createEntityForPaddle(Paddle paddle) {
        paddleView = entityViewFactory.createPaddleView(paddle);
        entityMap.put(paddle, paddleView.entity);
    }

    private void createEntityForBall(Ball ball) {
        entityMap.put(ball, entityViewFactory.createBallView(ball));
    }

    private void createEntityForPowerUp(PowerUp powerUp) {
        entityMap.put(powerUp, entityViewFactory.createPowerUpView(powerUp));
    }

    // -----------------------------------------------------------------
    // Generic helpers shared by ball/paddle/power-up entities
    // -----------------------------------------------------------------

    private void moveEntity(GameObject model) {
        Entity entity = entityMap.get(model);
        if (entity != null) {
            entity.setX(model.getX());
            entity.setY(model.getY());
        }
    }

    /**
     * Removes any tracked entity whose matching Model object is no
     * longer present in the Controller's "still alive" set for that
     * type (for example, a Ball that fell off screen, or an expired
     * power-up).
     */
    private void removeGoneEntities(Class<?> modelType, Set<GameObject> stillAlive) {
        entityMap.entrySet().removeIf(entry -> {
            GameObject model = entry.getKey();
            if (!modelType.isInstance(model)) {
                return false;
            }
            if (stillAlive.contains(model)) {
                return false;
            }
            entry.getValue().removeFromWorld();
            return true;
        });
    }
}
