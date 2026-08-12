package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity;



import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventBus;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventType;

import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;

/**
 * PlayerComponent.java
 * ====================
 * Represents the small triangular ship the human player controls.
 * Extends ShipComponent to reuse eight-directional movement and the
 * shared damage/invincibility logic, and adds player-specific concerns:
 * unlimited ammo, a constant fire rate, and a "lives" system (as opposed
 * to AllyShip, which has no lives - see AllyShipComponent).
 */
public class PlayerComponent extends ShipComponent {

    private static final int STARTING_LIVES = 14;
    private static final int HEALTH_PER_LIFE = 1;
    private static final double FIRE_INTERVAL_SECONDS = 0.12;
    private static final double BULLET_SPEED = 620;

    private final BulletFactory bulletFactory;

    private int livesRemaining = STARTING_LIVES;
    private double timeSinceLastShot = 0;
    private boolean isGameOver = false;

    public PlayerComponent(BulletFactory bulletFactory) {
        // A "life" in this design is modeled as one point of health so the
        // shared ShipComponent damage logic (currentHealth, invincibility)
        // can be reused directly instead of duplicated.
        super(HEALTH_PER_LIFE, 420 /* pixels per second move speed */);
        this.bulletFactory = bulletFactory;
    }

    /** Continuous forward projectile stream, rate-limited by a cooldown timer. */
    public void shoot(double tpf) {
        timeSinceLastShot += tpf;
        if (timeSinceLastShot >= FIRE_INTERVAL_SECONDS && !isGameOver) {
            timeSinceLastShot = 0;

            double x = entity.getX() + entity.getWidth() / 2.0;
            double y = entity.getY();

            // Angle 3*PI/2 (equivalently -PI/2) points straight up on
            // screen, toward the enemies scrolling down from the top.
            double straightUp = (3 * Math.PI) / 2;
            bulletFactory.spawnPlayerBullet(x, y, straightUp, BULLET_SPEED);

            // NOTE: the shoot sound effect is a genuinely looping audio
            // track now, not a one-shot triggered per bullet - see
            // AudioManager.startShootLoop()/stopShootLoop(), controlled by
            // PlayingState/PausedState/GameOverState/VictoryState instead
            // of from here.
        }
    }

    @Override
    protected void onDamaged() {
        GameEventBus.getInstance().publish(GameEventType.PLAYER_DAMAGED);
    }

    @Override
    protected void onDestroyed() {
        livesRemaining--;
        GameEventBus.getInstance().publish(GameEventType.PLAYER_LIFE_LOST, livesRemaining);

        if (livesRemaining <= 0) {
            isGameOver = true;
            GameEventBus.getInstance().publish(GameEventType.PLAYER_DIED);
        } else {
            respawn();
        }
    }

    /** Returns the player to the starting position with full health and a
     *  fresh window of invincibility so they are not immediately re-hit. */
    private void respawn() {
        currentHealth = HEALTH_PER_LIFE;
        invincibilityTimer = INVINCIBILITY_DURATION_SECONDS * 2; // extra grace period
        entity.setPosition(getAppWidth() / 2.0 - 15, getAppHeight() - 80);
    }

    public int getLivesRemaining() {
        return livesRemaining;
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
