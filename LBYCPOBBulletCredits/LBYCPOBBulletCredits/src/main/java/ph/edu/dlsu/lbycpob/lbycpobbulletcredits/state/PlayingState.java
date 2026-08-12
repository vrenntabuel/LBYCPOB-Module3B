package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.state;

import com.almasb.fxgl.entity.Entity;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.GameContext;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.manager.DifficultyManager;


import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;

/**
 * PlayingState.java
 * =================
 * The core gameplay loop. Responsible for spawning the player (on first
 * entry only), driving the WaveManager every frame, and watching for the
 * two ways gameplay can end: the player running out of lives (-> GameOverState)
 * or every wave being cleared (-> VictoryState).
 */
public class PlayingState implements GameState {

    private final GameContext context;
    private boolean isFirstEntry = true;

    public PlayingState(GameContext context) {
        this.context = context;
    }

    @Override
    public void onEnter() {
        if (isFirstEntry) {
            isFirstEntry = false;

            double startX = getAppWidth() / 2.0 - 15;
            double startY = getAppHeight() - 80;
            context.playerEntity = context.playerFactory.spawnPlayer(startX, startY);
            context.hud.bindPlayer(context.getPlayer());

            context.audioManager.playBackgroundMusic();

            // Fresh run (either the very first one, or after a Game Over
            // retry): the time-based difficulty ramp starts over too, so
            // the player is not dropped straight back into max difficulty.
            DifficultyManager.getInstance().reset();

            context.waveManager.resetToFirstWave();
        }

        // Runs on EVERY entry into PLAYING, including resuming from a
        // pause - this is what makes the shoot sound loop start back up
        // the instant gameplay resumes, and (paired with the stop call in
        // onExit() below) stop cleanly the instant it doesn't.
        context.audioManager.startShootLoop();
    }

    @Override
    public void onExit() {
        // Stops the looping shoot sound the moment gameplay pauses, ends
        // in a Game Over, or is won - see onEnter() above for the
        // matching start call.
        context.audioManager.stopShootLoop();
    }

    @Override
    public void onUpdate(double tpf) {
        // Drives the time-based difficulty ramp (see DifficultyManager) -
        // only while actually playing, so pausing also pauses the ramp.
        DifficultyManager.getInstance().update(tpf);

        context.waveManager.update(tpf);

        if (context.getPlayer().isGameOver()) {
            context.gameStateManager.changeState(new GameOverState(context));
            return;
        }

        if (context.waveManager.isAllWavesComplete()) {
            context.gameStateManager.changeState(new VictoryState(context));
        }
    }

    @Override
    public String getName() {
        return "PLAYING";
    }
}
