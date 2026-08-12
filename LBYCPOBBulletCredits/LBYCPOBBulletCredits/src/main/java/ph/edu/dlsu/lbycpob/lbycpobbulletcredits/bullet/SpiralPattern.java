package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet;

import com.almasb.fxgl.entity.Entity;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;


/**
 * SpiralPattern.java
 * ==================
 * Classic "bullet-hell spiral": fires one bullet at a time, but rotates the
 * firing angle a little more each shot, producing a slowly turning spiral
 * arm across the whole screen.
 */
public class SpiralPattern implements FirePattern {

    private final double shotInterval;
    private final double bulletSpeed;
    private final double angleStepRadians;
    private final BulletDurability durability;

    private double timeSinceLastShot = 0;
    private double currentAngle = 0;

    /**
     * @param shotInterval     seconds between individual bullets (kept short
     *                         so the spiral looks continuous)
     * @param bulletSpeed      speed of each bullet
     * @param angleStepRadians how much the firing angle rotates every shot
     * @param durability       destructibility tier for the bullets this
     *                         pattern fires
     */
    public SpiralPattern(double shotInterval, double bulletSpeed, double angleStepRadians, BulletDurability durability) {
        this.shotInterval = shotInterval;
        this.bulletSpeed = bulletSpeed;
        this.angleStepRadians = angleStepRadians;
        this.durability = durability;
    }

    @Override
    public void update(Entity sourceEntity, double originX, double originY,
                       BulletFactory bulletFactory, double tpf, double difficultyScale) {
        timeSinceLastShot += tpf;
        double effectiveInterval = shotInterval / difficultyScale;

        if (timeSinceLastShot >= effectiveInterval) {
            timeSinceLastShot = 0;

            bulletFactory.spawnEnemyBullet(originX, originY, currentAngle, bulletSpeed * difficultyScale, durability);

            currentAngle += angleStepRadians;
            if (currentAngle > 2 * Math.PI) {
                currentAngle -= 2 * Math.PI;
            }
        }
    }
}
