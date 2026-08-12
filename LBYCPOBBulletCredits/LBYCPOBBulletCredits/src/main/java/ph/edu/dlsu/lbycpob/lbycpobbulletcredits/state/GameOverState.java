package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.state;

import com.almasb.fxgl.entity.Entity;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.GameContext;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.effect.FlashEffectFactory;


import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

/**
 * GameOverState.java
 * ==================
 * Entered once the player has lost their final life. Implements the
 * "Continue System" from the design brief: pressing R (handled in
 * Main.java, which checks gameStateManager.isInState(GameOverState.class))
 * calls retry(), which clears the old encounter and starts a brand new
 * PlayingState from wave 1.
 */
public class GameOverState implements GameState {

    private final GameContext context;

    public GameOverState(GameContext context) {
        this.context = context;
    }

    @Override
    public void onEnter() {
        // The HUD already shows "GAME OVER" via the PLAYER_DIED event
        // published from PlayerComponent, so there is nothing extra to
        // display here - onEnter mainly exists as an extension point.
    }

    @Override
    public void onExit() {
        context.hud.hideMessage();
    }

    @Override
    public void onUpdate(double tpf) {
        // Waiting for player input (handled in Main.java) - no per-frame
        // gameplay logic runs while in this state.
    }

    /** Wipes the current encounter and begins a fresh run from wave 1. */
    public void retry() {
        // getEntitiesCopy() returns every entity currently in the world.
        // We copy the list first because removeFromWorld() modifies the
        // world's live entity list as we go - iterating that list directly
        // while removing from it would throw a ConcurrentModificationException.
        for (Entity entity : getGameWorld().getEntitiesCopy()) {
            entity.removeFromWorld();
        }

        // MUST happen right after the wipe above: parked/pooled bullets
        // were included in that entity sweep too (they were still
        // technically "in the world", just invisible and off-screen), and
        // removeFromWorld() strips their components. If BulletFactory's
        // pools still held references to them, the next spawnPlayerBullet()
        // call would try to read a BulletComponent that no longer exists
        // and crash. See BulletFactory.clearPools() for the full story.
        context.bulletFactory.clearPools();

        // Same idea as clearPools() above: an enemy that was still
        // "arming" (see EnemyCreditComponent) when the wipe above ran
        // never got the chance to stop its own sweeping light-bar effect,
        // which loops INDEFINITELY by design. Left alone, that would keep
        // animating forever in the background after every such retry.
        FlashEffectFactory.stopAll();

        context.assistanceManager.reset();

        context.gameStateManager.changeState(new PlayingState(context));
    }

    @Override
    public String getName() {
        return "GAME_OVER";
    }
}
