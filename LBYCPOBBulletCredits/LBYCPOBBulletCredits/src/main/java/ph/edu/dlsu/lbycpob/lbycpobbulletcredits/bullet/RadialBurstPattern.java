package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet;

import com.almasb.fxgl.entity.Entity;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;


/**
 * RadialBurstPattern.java
 * =======================
 * Fires a full ring of bullets outward in every direction at once, like a
 * firework. `bulletsPerBurst` controls how many bullets make up the ring.
 */
public class RadialBurstPattern implements FirePattern {

    private final double baseInterval;
    private final double bulletSpeed;
    private final int bulletsPerBurst;
    private final BulletDurability durability;

    private double timeSinceLastShot = 0;

    public RadialBurstPattern(double baseInterval, double bulletSpeed, int bulletsPerBurst, BulletDurability durability) {
        this.baseInterval = baseInterval;
        this.bulletSpeed = bulletSpeed;
        this.bulletsPerBurst = bulletsPerBurst;
        this.durability = durability;
    }

    @Override
    public void update(Entity sourceEntity, double originX, double originY,
                       BulletFactory bulletFactory, double tpf, double difficultyScale) {
        timeSinceLastShot += tpf;
        double effectiveInterval = baseInterval / difficultyScale;

        if (timeSinceLastShot >= effectiveInterval) {
            timeSinceLastShot = 0;

            // Split a full circle (2 * PI radians) evenly among all bullets
            // in the burst so they fan out symmetrically.
            double angleStep = (2 * Math.PI) / bulletsPerBurst;

            for (int i = 0; i < bulletsPerBurst; i++) {
                double angle = i * angleStep;
                bulletFactory.spawnEnemyBullet(originX, originY, angle, bulletSpeed * difficultyScale, durability);
            }
        }
    }
}
