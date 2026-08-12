package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet;

import com.almasb.fxgl.entity.Entity;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;


/**
 * StraightPattern.java
 * ====================
 * The simplest FirePattern: fire one bullet straight down at a steady
 * interval. Good starting point for reading how the Strategy pattern
 * pieces fit together before looking at the fancier patterns.
 */
public class StraightPattern implements FirePattern {

    /** Seconds between shots at difficultyScale == 1.0. */
    private final double baseInterval;
    private final double bulletSpeed;
    private final BulletDurability durability;

    /** Internal timer - each enemy gets its OWN instance of this pattern,
     *  so this field does not leak state between different enemies. */
    private double timeSinceLastShot = 0;

    public StraightPattern(double baseInterval, double bulletSpeed, BulletDurability durability) {
        this.baseInterval = baseInterval;
        this.bulletSpeed = bulletSpeed;
        this.durability = durability;
    }

    @Override
    public void update(Entity sourceEntity, double originX, double originY,
                       BulletFactory bulletFactory, double tpf, double difficultyScale) {
        timeSinceLastShot += tpf;

        // Higher difficultyScale shrinks the interval, so bullets come faster.
        double effectiveInterval = baseInterval / difficultyScale;

        if (timeSinceLastShot >= effectiveInterval) {
            timeSinceLastShot = 0;
            // Math.PI / 2 in FXGL's screen-space (y grows downward) points
            // straight down toward the player.
            double angleRadians = Math.PI / 2;
            bulletFactory.spawnEnemyBullet(originX, originY, angleRadians, bulletSpeed * difficultyScale, durability);
        }
    }
}
