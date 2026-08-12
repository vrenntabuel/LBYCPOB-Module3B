package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet;

import com.almasb.fxgl.entity.Entity;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;


/**
 * RandomPattern.java
 * ==================
 * Fires bullets at random angles within a downward-facing cone, at
 * randomized intervals. This keeps the player from memorizing a fixed
 * rhythm, which is exactly why "randomized firing" was called out in the
 * design brief as one of the required bullet-hell behaviors.
 */
public class RandomPattern implements FirePattern {

    private final double minInterval;
    private final double maxInterval;
    private final double bulletSpeed;
    private final double coneRadians;
    private final BulletDurability durability;

    private double timeSinceLastShot = 0;
    private double nextInterval;

    public RandomPattern(double minInterval, double maxInterval, double bulletSpeed,
                          double coneRadians, BulletDurability durability) {
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
        this.bulletSpeed = bulletSpeed;
        this.coneRadians = coneRadians;
        this.durability = durability;
        this.nextInterval = randomBetween(minInterval, maxInterval);
    }

    private double randomBetween(double min, double max) {
        return min + Math.random() * (max - min);
    }

    @Override
    public void update(Entity sourceEntity, double originX, double originY,
                       BulletFactory bulletFactory, double tpf, double difficultyScale) {
        timeSinceLastShot += tpf;
        double effectiveInterval = nextInterval / difficultyScale;

        if (timeSinceLastShot >= effectiveInterval) {
            timeSinceLastShot = 0;
            nextInterval = randomBetween(minInterval, maxInterval);

            double baseAngle = Math.PI / 2; // downward
            double randomOffset = (Math.random() - 0.5) * coneRadians;
            double angle = baseAngle + randomOffset;

            bulletFactory.spawnEnemyBullet(originX, originY, angle, bulletSpeed * difficultyScale, durability);
        }
    }
}
