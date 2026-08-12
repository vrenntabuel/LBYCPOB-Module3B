package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet;

import com.almasb.fxgl.entity.Entity;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;


import static com.almasb.fxgl.dsl.FXGL.getAppWidth;

/**
 * WallPattern.java
 * ================
 * Fires a dense horizontal "wall" of bullets moving downward, with one
 * randomly placed gap the player must dodge through. This is one of the
 * patterns responsible for making the late game "intentionally overwhelming".
 */
public class WallPattern implements FirePattern {

    private final double baseInterval;
    private final double bulletSpeed;
    private final int wallSegments;
    private final double wallWidth;
    private final BulletDurability durability;

    private double timeSinceLastShot = 0;

    public WallPattern(double baseInterval, double bulletSpeed, int wallSegments,
                        double wallWidth, BulletDurability durability) {
        this.baseInterval = baseInterval;
        this.bulletSpeed = bulletSpeed;
        this.wallSegments = wallSegments;
        this.wallWidth = wallWidth;
        this.durability = durability;
    }

    @Override
    public void update(Entity sourceEntity, double originX, double originY,
                       BulletFactory bulletFactory, double tpf, double difficultyScale) {
        timeSinceLastShot += tpf;
        double effectiveInterval = baseInterval / difficultyScale;

        if (timeSinceLastShot >= effectiveInterval) {
            timeSinceLastShot = 0;

            // Pick one segment to leave empty so the wall is dodgeable.
            int gapIndex = (int) (Math.random() * wallSegments);
            double segmentSpacing = wallWidth / wallSegments;

            // BUGFIX - "some bullets do not extend towards the end of
            // screen": this used to center the wall on the FIRING ENEMY'S
            // OWN horizontal position (originX). An enemy spawned near
            // the left or right edge of the screen could then push wall
            // segments on the far side of that center point well past the
            // actual screen edge - spawning them already off-screen,
            // where BulletComponent's new bounds-based recycling would
            // remove them on their very first update, before they were
            // ever visibly on screen. A horizontal "wall" barrier reads
            // more correctly centered on the screen itself anyway
            // (regardless of which word happens to be firing it), so the
            // wall is now always centered on the screen's own midpoint.
            double screenCenterX = getAppWidth() / 2.0;
            double startX = screenCenterX - (wallWidth / 2);

            for (int i = 0; i < wallSegments; i++) {
                if (i == gapIndex) {
                    continue;
                }
                double spawnX = startX + (i * segmentSpacing);
                // All bullets in the wall travel straight down.
                bulletFactory.spawnEnemyBulletAt(spawnX, originY, Math.PI / 2, bulletSpeed * difficultyScale, durability);
            }
        }
    }
}
