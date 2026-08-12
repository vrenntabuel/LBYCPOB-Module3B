package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.state;


import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.GameContext;

import static com.almasb.fxgl.dsl.FXGL.getGameController;

/**
 * PausedState.java
 * ================
 * Freezes gameplay. We lean on FXGL's own engine pause/resume here
 * (rather than just skipping our own onUpdate calls) because entity
 * Components like BulletComponent and EnemyCreditComponent run on FXGL's
 * update loop directly - truly stopping the action requires pausing that
 * loop, not just our State's onUpdate method.
 */
public class PausedState implements GameState {

    private final GameContext context;
    private final GameState previousState;

    public PausedState(GameContext context, GameState previousState) {
        this.context = context;
        this.previousState = previousState;
    }

    @Override
    public void onEnter() {
        getGameController().pauseEngine();
        context.hud.showMessage("PAUSED\n\nPress P to resume");
    }

    @Override
    public void onExit() {
        getGameController().resumeEngine();
        context.hud.hideMessage();
    }

    @Override
    public void onUpdate(double tpf) {
        // Nothing to do - the engine itself is paused, so no entities are
        // updating right now. Waiting for player input in Main.java.
    }

    /** Resumes gameplay by returning to whichever state was active before pausing. */
    public void resume() {
        context.gameStateManager.changeState(previousState);
    }

    @Override
    public String getName() {
        return "PAUSED";
    }
}
