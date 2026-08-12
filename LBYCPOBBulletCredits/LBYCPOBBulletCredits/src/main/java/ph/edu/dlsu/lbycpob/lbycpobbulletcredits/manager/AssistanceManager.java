package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.manager;



import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.AllyShipFactory;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventBus;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventListener;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventType;

import static com.almasb.fxgl.dsl.FXGL.getAppWidth;

/**
 * AssistanceManager.java
 * ======================
 * Implements the "Cooperative System" and "Continue System" requirements:
 *   - After the player dies enough times in a row, an AI ally ship is
 *     granted automatically (dynamic spawning), softening the intentionally
 *     brutal late-game difficulty.
 *   - After MANY repeated failures, a scripted narrative line is published
 *     through the event bus (which the HUD displays) - this is the
 *     "this is worthless, isn't it" moment described in the brief.
 *
 * Implements GameEventListener so it reacts to PLAYER_LIFE_LOST /
 * PLAYER_DIED events via the OBSERVER pattern rather than PlayerComponent
 * needing to know anything about assistance logic directly.
 */
public class AssistanceManager implements GameEventListener {

    private static final int DEATHS_BEFORE_ALLY = 2;
    private static final int DEATHS_BEFORE_NARRATIVE_LINE = 5;

    private final AllyShipFactory allyShipFactory;
    private int consecutiveFailures = 0;
    private boolean narrativeLineShown = false;

    public AssistanceManager(AllyShipFactory allyShipFactory) {
        this.allyShipFactory = allyShipFactory;
    }

    public void startListening() {
        GameEventBus.getInstance().subscribe(this);
    }

    @Override
    public void onGameEvent(GameEventType event, Object payload) {
        if (event == GameEventType.PLAYER_LIFE_LOST) {
            consecutiveFailures++;

            if (consecutiveFailures == DEATHS_BEFORE_ALLY) {
                grantAllyShip();
            }

            if (consecutiveFailures >= DEATHS_BEFORE_NARRATIVE_LINE && !narrativeLineShown) {
                narrativeLineShown = true;
                GameEventBus.getInstance().publish(
                        GameEventType.NARRATIVE_LINE,
                        "...keep fighting, you're almost there!"
                );
            }
        } else if (event == GameEventType.WAVE_STARTED) {
            // A fresh wave counts as forward progress, easing off the
            // failure counter slightly so assistance is not permanent.
            consecutiveFailures = Math.max(0, consecutiveFailures - 1);
        }
    }

    private void grantAllyShip() {
        double x = getAppWidth() / 2.0 - 80;
        double y = 620;
        allyShipFactory.spawnAllyShip(x, y);
    }

    public void reset() {
        consecutiveFailures = 0;
        narrativeLineShown = false;
    }
}
