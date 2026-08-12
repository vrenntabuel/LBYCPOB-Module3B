package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet;

import com.almasb.fxgl.entity.Entity;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;


/**
 * SpreadPattern.java
 * ==================
 * Fires a fan/cone of bullets centered on a base angle (by default,
 * straight down). `spreadCount` bullets are evenly spaced across
 * `totalSpreadRadians` of arc.
 */
public class SpreadPattern implements FirePattern {

    private final double baseInterval;
    private final double bulletSpeed;
    private final int spreadCount;
    private final double totalSpreadRadians;
    private final BulletDurability durability;

    private double timeSinceLastShot = 0;

    public SpreadPattern(double baseInterval, double bulletSpeed, int spreadCount,
                          double totalSpreadRadians, BulletDurability durability) {
        this.baseInterval = baseInterval;
        this.bulletSpeed = bulletSpeed;
        this.spreadCount = spreadCount;
        this.totalSpreadRadians = totalSpreadRadians;
        this.durability = durability;
    }

    @Override
    public void update(Entity sourceEntity, double originX, double originY,
                       BulletFactory bulletFactory, double tpf, double difficultyScale) {
        timeSinceLastShot += tpf;
        double effectiveInterval = baseInterval / difficultyScale;

        if (timeSinceLastShot >= effectiveInterval) {
            timeSinceLastShot = 0;

            double baseAngle = Math.PI / 2; // straight down
            double startAngle = baseAngle - (totalSpreadRadians / 2);
            double angleStep = spreadCount > 1 ? totalSpreadRadians / (spreadCount - 1) : 0;

            for (int i = 0; i < spreadCount; i++) {
                double angle = startAngle + (angleStep * i);
                bulletFactory.spawnEnemyBullet(originX, originY, angle, bulletSpeed * difficultyScale, durability);
            }
        }
    }
}
