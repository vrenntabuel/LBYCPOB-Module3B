package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.state;

import com.almasb.fxgl.entity.Entity;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.GameContext;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.effect.FlashEffectFactory;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.manager.DifficultyManager;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventBus;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventType;


import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

/**
 * VictoryState.java
 * =================
 * Entered once WaveManager reports every wave - including the final
 * "Four Pillars United" gauntlet - has been cleared. Records the ending
 * as complete via SaveManager, plays the victory sound (via the
 * GAME_VICTORY event, handled in AudioManager), and offers the player two
 * options: play again from the beginning, or exit the game - handled by
 * Main.java, which calls restart() or requestExit() below depending on
 * which key is pressed while this state is active.
 */
public class VictoryState implements GameState {

    private final GameContext context;

    public VictoryState(GameContext context) {
        this.context = context;
    }

    @Override
    public void onEnter() {
        context.saveManager.markEndingComplete();
        GameEventBus.getInstance().publish(GameEventType.GAME_VICTORY);
        context.hud.showMessage("VICTORY\n\nYou have mastered Object-Oriented Programming!"
                + "\n\nPress R to play again, or ESC to exit");
    }

    @Override
    public void onExit() {
        context.hud.hideMessage();
    }

    @Override
    public void onUpdate(double tpf) {
        // Victory is a terminal state for this run until the player
        // chooses an option - no further gameplay logic executes here.
    }

    /**
     * Wipes the current (won) encounter and begins a brand new run from
     * wave 1 - the "repeat from the beginning" option. Mirrors
     * GameOverState.retry(); see that class for why each cleanup step
     * below is needed.
     */
    public void restart() {
        for (Entity entity : getGameWorld().getEntitiesCopy()) {
            entity.removeFromWorld();
        }

        context.bulletFactory.clearPools();
        FlashEffectFactory.stopAll();
        context.assistanceManager.reset();
        DifficultyManager.getInstance().reset();

        context.gameStateManager.changeState(new PlayingState(context));
    }

    /** Cleanly exits the application - the "exit the game" option. */
    public void requestExit() {
        getGameController().exit();
    }

    @Override
    public String getName() {
        return "VICTORY";
    }
}
