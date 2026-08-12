package ph.edu.dlsu.lbycpob.lbycpobbulletcredits;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.command.InputCommand;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.command.MoveCommand;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.command.ShootCommand;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.config.GameConfig;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.state.*;


import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Main.java
 * =========
 * The FXGL application entry point. FXGL applications override three key
 * lifecycle methods:
 * - initSettings: window size/title, called once before anything else
 * - initGame:     build the initial world/state, called once at startup
 * - initInput:    bind keys to actions, called once at startup
 * After that, FXGL's own game loop calls onUpdate(tpf) every frame
 * automatically, which we use to drive our GameStateManager.
 * <p>
 * NOTE:
 * This class is intentionally "thin" - it wires things together but
 * contains almost no gameplay logic itself. All of the interesting
 * behavior lives in the entity/, bullet/, manager/, and state/ packages.
 * This is a common and healthy pattern: keep your "main" class focused on
 * plumbing, not decision-making.
 */
public class

Main extends GameApplication {

    private GameContext context;

    // Track which directional keys are currently held. We combine all four
    // into a single direction vector once per frame in onUpdate() rather
    // than issuing a separate MoveCommand per key - if we issued one
    // MoveCommand per held key, holding two keys at once (e.g. up + left
    // for a diagonal) would move the ship twice in one frame and break the
    // "diagonal movement is not faster than straight movement" rule that
    // ShipComponent.move() otherwise guarantees via vector normalization.
    private boolean moveUp, moveDown, moveLeft, moveRight;

    @Override
    protected void initSettings(GameSettings settings) {
        GameConfig config = GameConfig.getInstance();
        settings.setWidth(config.getScreenWidth());
        settings.setHeight(config.getScreenHeight());
        settings.setTitle("LBYCPOB Credits");
        settings.setVersion("1.0");
        settings.setManualResizeEnabled(false);

        // Fullscreen: the game renders at the virtual resolution above
        // (1280x720) and FXGL scales that up to fill the real screen,
        // preserving aspect ratio and letterboxing if needed. Allowing
        // fullscreen (rather than forcing it) also lets FXGL's built-in
        // F11 toggle keep working if a player wants to drop back to a
        // window.
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);
    }

    @Override
    protected void initGame() {
        context = new GameContext();
        context.hud.attachToScene();
        getGameScene().setBackgroundColor(Color.rgb(8, 8, 20));
        context.collisionManager.registerAllHandlers();
        context.audioManager.startListening();
        context.assistanceManager.startListening();
        context.gameStateManager.changeState(new TitleState(context));
    }

    @Override
    protected void initInput() {
        // --- Movement: eight directions via WASD or arrow keys ---
        // Each key just flips a boolean flag; the actual movement command
        // is built and executed once per frame in onUpdate(), see the
        // moveUp/moveDown/moveLeft/moveRight fields above for why.
        bindDirectionKey(KeyCode.W, KeyCode.UP, pressed -> moveUp = pressed);
        bindDirectionKey(KeyCode.S, KeyCode.DOWN, pressed -> moveDown = pressed);
        bindDirectionKey(KeyCode.A, KeyCode.LEFT, pressed -> moveLeft = pressed);
        bindDirectionKey(KeyCode.D, KeyCode.RIGHT, pressed -> moveRight = pressed);

        // --- Start game from the title screen ---
        getInput().addAction(new UserAction("Start Game") {
            @Override
            protected void onActionBegin() {
                if (context.gameStateManager.isInState(TitleState.class)) {
                    context.gameStateManager.changeState(new PlayingState(context));
                }
            }
        }, KeyCode.ENTER);

        // --- Pause / resume ---
        getInput().addAction(new UserAction("Pause") {
            @Override
            protected void onActionBegin() {
                GameState current = context.gameStateManager.getCurrentState();
                if (current instanceof PlayingState) {
                    // Capture whichever PlayingState instance is actually
                    // active right now, so resuming always returns to the
                    // correct run - see PausedState's BUGFIX javadoc.
                    context.gameStateManager.changeState(new PausedState(context, current));
                } else if (current instanceof PausedState pausedState) {
                    pausedState.resume();
                }
            }
        }, KeyCode.P);

        // --- Retry after Game Over, or Play Again after Victory ---
        getInput().addAction(new UserAction("Retry") {
            @Override
            protected void onActionBegin() {
                GameState current = context.gameStateManager.getCurrentState();
                if (current instanceof GameOverState gameOverState) {
                    gameOverState.retry();
                } else if (current instanceof VictoryState victoryState) {
                    victoryState.restart();
                }
            }
        }, KeyCode.R);

        // --- Exit the game from the Victory screen ---
        getInput().addAction(new UserAction("Exit Game") {
            @Override
            protected void onActionBegin() {
                if (context.gameStateManager.getCurrentState() instanceof VictoryState victoryState) {
                    victoryState.requestExit();
                }
            }
        }, KeyCode.ESCAPE);
    }

    /**
     * Small functional-style helper interface for the two bindDirectionKey callbacks below.
     */
    private interface FlagSetter {
        void set(boolean pressed);
    }

    /**
     * Binds a primary key and an alternate key (e.g. W and UP-arrow) to the
     * same directional flag, set true on key-down and false on key-up.
     * <p>
     * BEGINNER NOTE:
     * FXGL identifies each UserAction by its name, and Input.addAction()
     * refuses to register the same action name twice - even for two
     * different keys. Reusing one UserAction instance for both the
     * primary and alternate key (as an earlier version of this method
     * did) throws "Action already exists" the moment the second
     * addAction() call runs. The fix is simple: create two separate
     * UserAction instances with distinct names, and have both call the
     * same FlagSetter so they still drive one shared boolean flag.
     */
    private void bindDirectionKey(KeyCode primary, KeyCode alternate, FlagSetter setter) {
        getInput().addAction(new UserAction("Direction " + primary) {
            @Override
            protected void onActionBegin() {
                setter.set(true);
            }

            @Override
            protected void onActionEnd() {
                setter.set(false);
            }
        }, primary);

        getInput().addAction(new UserAction("Direction " + alternate) {
            @Override
            protected void onActionBegin() {
                setter.set(true);
            }

            @Override
            protected void onActionEnd() {
                setter.set(false);
            }
        }, alternate);
    }

    @Override
    protected void onUpdate(double tpf) {
        context.gameStateManager.update(tpf);

        if (context.gameStateManager.isInState(PlayingState.class) && context.playerEntity != null) {
            double dx = (moveRight ? 1 : 0) - (moveLeft ? 1 : 0);
            double dy = (moveDown ? 1 : 0) - (moveUp ? 1 : 0);

            if (dx != 0 || dy != 0) {
                // COMMAND PATTERN: wrap this frame's requested movement in
                // a MoveCommand rather than calling player.move() directly.
                InputCommand move = new MoveCommand(context.getPlayer(), dx, dy);
                move.execute(tpf());
            }

            // Continuous firing: the ship fires every frame while playing,
            // matching the "unlimited ammunition / constant firing rate"
            // requirement, wired through the same Command pattern.
            InputCommand shoot = new ShootCommand(context.getPlayer());
            shoot.execute(tpf());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}