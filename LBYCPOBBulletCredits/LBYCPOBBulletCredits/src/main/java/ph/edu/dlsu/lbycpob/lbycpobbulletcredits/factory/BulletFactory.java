package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet.BulletComponent;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet.BulletDurability;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet.BulletPool;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.EntityType;


import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * BulletFactory.java
 * ==================
 * Implements the FACTORY DESIGN PATTERN for bullets specifically.
 *
 * NOTE:
 * A Factory's whole job is "give me a finished object; you don't need to
 * know how it was built." Here, callers (FirePattern strategies, or the
 * Player when shooting) simply say "spawn a bullet going this direction at
 * this speed" - they never need to know about shapes, BulletComponent
 * wiring, or the object pool underneath. That knowledge is centralized in
 * exactly one place, which makes future changes (e.g. swapping a bullet's
 * shape for a sprite image) a one-file edit.
 *
 * SHAPE NOTE:
 * Player bullets are rendered as small rectangles ("shots"/pellets),
 * clearly distinct at a glance from the circular enemy "word" bullets -
 * important in a screen that can have dozens of both on screen at once.
 *
 * SPEED CAP NOTE:
 * Enemy bullet speed can get multiplied twice over (once by a Wave's own
 * difficultyMultiplier, again by DifficultyManager's time-based ramp - see
 * EnemyCreditComponent), which can compound into velocities high enough
 * that a bullet's per-frame movement becomes larger than the player's
 * hitbox. FXGL's collision detection is a discrete per-frame overlap
 * check (no continuous/swept detection), so a fast enough bullet can
 * "tunnel" straight through the player between two frames without ever
 * registering a collision. MAX_BULLET_SPEED below caps every bullet's
 * velocity at spawn time so this can never happen, regardless of how the
 * difficulty multipliers stack.
 *
 * POOLING NOTE:
 * Bullets are never truly destroyed. Once "used up" they are parked far off
 * screen, marked invisible/inactive, and handed to a BulletPool. The next
 * time a bullet is needed, we grab one from the pool and reposition it
 * instead of allocating a new Entity - see BulletPool.java for why this
 * matters in a genre that can have hundreds of bullets on screen.
 *
 * A SEPARATE pool is kept per (player-vs-enemy) x (durability tier)
 * combination. This is deliberate: each tier has its own fixed shape,
 * color, and size (see buildBulletView below), and a pooled Entity's view
 * Node is expensive to re-style on the fly. Keeping tiers in separate
 * pools means a recycled bullet is always handed back out looking exactly
 * like it did before - no per-reuse re-styling needed.
 */
public class BulletFactory {

    private static final double PLAYER_BULLET_WIDTH = 7;
    private static final double PLAYER_BULLET_HEIGHT = 20;
    private static final double ENEMY_BULLET_RADIUS = 13;

    /** See "SPEED CAP NOTE" above - the single most important number for
     *  preventing bullets from tunneling through the player undetected. */
    private static final double MAX_BULLET_SPEED = 480;

    /** Safety-net lifetime only - see BulletComponent, which now mainly
     *  recycles bullets based on actually leaving the screen. This just
     *  guards against a bullet that somehow never leaves (e.g. zero
     *  velocity from a future bug) lingering forever. */
    private static final double SAFETY_MAX_LIFETIME_SECONDS = 10.0;

    // Bullets are "parked" here while inactive in the pool - far enough
    // off any reasonable screen resolution that they can never be seen
    // or accidentally collide with anything.
    private static final double PARK_X = -5000;
    private static final double PARK_Y = -5000;

    private final BulletPool playerBulletPool = new BulletPool();
    private final BulletPool enemyIndestructiblePool = new BulletPool();
    private final BulletPool enemyFragilePool = new BulletPool();
    private final BulletPool enemyToughPool = new BulletPool();

    /** Spawns a bullet fired BY the player. Angle 0 = right, increases clockwise (screen space). */
    public Entity spawnPlayerBullet(double x, double y, double angleRadians, double speed) {
        // Durability is meaningless for player bullets (they are always
        // consumed on any hit - see CollisionManager), so INDESTRUCTIBLE
        // here is just an unused placeholder value.
        return spawnBullet(x, y, angleRadians, speed, true, BulletDurability.INDESTRUCTIBLE);
    }

    /** Spawns a bullet fired BY an enemy "OOP concept", with a given durability tier. */
    public Entity spawnEnemyBullet(double x, double y, double angleRadians, double speed, BulletDurability durability) {
        return spawnBullet(x, y, angleRadians, speed, false, durability);
    }

    /**
     * Identical to spawnEnemyBullet - kept as a separate, clearly-named
     * method because WallPattern spawns many bullets spread across a range
     * of x-positions rather than from one single origin point, and the
     * distinct name makes that call site easier to read.
     */
    public Entity spawnEnemyBulletAt(double x, double y, double angleRadians, double speed, BulletDurability durability) {
        return spawnEnemyBullet(x, y, angleRadians, speed, durability);
    }

    private Entity spawnBullet(double x, double y, double angleRadians, double speed,
                                boolean isPlayerBullet, BulletDurability durability) {
        BulletPool pool = selectPool(isPlayerBullet, durability);

        double clampedSpeed = Math.min(speed, MAX_BULLET_SPEED);
        double velocityX = Math.cos(angleRadians) * clampedSpeed;
        double velocityY = Math.sin(angleRadians) * clampedSpeed;

        Entity recycled = pool.obtain();
        if (recycled != null) {
            // Reuse an existing, currently-parked bullet: wake it up,
            // reposition it, and give its BulletComponent a fresh
            // direction/lifetime (durability/appearance stay the same,
            // since this pool only ever holds bullets of this one tier).
            recycled.setPosition(x, y);
            recycled.setVisible(true);
            recycled.getComponent(BulletComponent.class)
                    .relaunch(velocityX, velocityY, SAFETY_MAX_LIFETIME_SECONDS);
            return recycled;
        }

        EntityType type = isPlayerBullet ? EntityType.PLAYER_BULLET : EntityType.ENEMY_BULLET;

        return entityBuilder()
                .type(type)
                .at(x, y)
                .bbox(buildHitBox(isPlayerBullet, durability))
                .collidable()
                .view(buildBulletView(isPlayerBullet, durability))
                .with(new BulletComponent(velocityX, velocityY, SAFETY_MAX_LIFETIME_SECONDS, this, isPlayerBullet, durability))
                .buildAndAttach();
    }

    private BulletPool selectPool(boolean isPlayerBullet, BulletDurability durability) {
        if (isPlayerBullet) {
            return playerBulletPool;
        }
        return switch (durability) {
            case FRAGILE -> enemyFragilePool;
            case TOUGH -> enemyToughPool;
            case INDESTRUCTIBLE -> enemyIndestructiblePool;
        };
    }

    /**
     * Player bullets get a small rectangular hitbox centered on the spawn
     * point (matching their rectangular view - see buildBulletView).
     * Enemy bullets keep a circular hitbox, sized up slightly for the
     * TOUGH tier to match its larger view.
     */
    private HitBox buildHitBox(boolean isPlayerBullet, BulletDurability durability) {
        if (isPlayerBullet) {
            return new HitBox(new Point2D(-PLAYER_BULLET_WIDTH / 2.0, -PLAYER_BULLET_HEIGHT / 2.0),
                    BoundingShape.box(PLAYER_BULLET_WIDTH, PLAYER_BULLET_HEIGHT));
        }
        double radius = durability == BulletDurability.TOUGH ? ENEMY_BULLET_RADIUS * 1.35 : ENEMY_BULLET_RADIUS;
        return new HitBox(BoundingShape.circle(radius));
    }

    /**
     * Each bullet type/tier gets a distinct look so the player can tell at
     * a glance what a bullet is and what it will do if they shoot it:
     *   - Player bullets: a small cyan RECTANGLE (a "shot"/pellet) with a
     *     soft glow - visually distinct from every enemy bullet, which are
     *     all circular "word" bullets.
     *   - INDESTRUCTIBLE enemy bullets: solid orange-red circle, no
     *     outline - the classic "just dodge me" bullet-hell projectile.
     *   - FRAGILE enemy bullets: green circle with a white outline - shoot
     *     these down in a single hit.
     *   - TOUGH enemy bullets: larger, purple circle with a thicker white
     *     outline - can be shot down, but takes several hits.
     */
    private Shape buildBulletView(boolean isPlayerBullet, BulletDurability durability) {
        if (isPlayerBullet) {
            // Centered on the entity's local origin (matching how the
            // circular enemy bullets are centered) - a plain Rectangle
            // defaults to top-left origin, so we shift it by half its own
            // size and round the corners slightly so it reads as a small
            // energy bolt rather than a hard-edged block.
            Rectangle bolt = new Rectangle(-PLAYER_BULLET_WIDTH / 2.0, -PLAYER_BULLET_HEIGHT / 2.0,
                    PLAYER_BULLET_WIDTH, PLAYER_BULLET_HEIGHT);
            bolt.setArcWidth(4);
            bolt.setArcHeight(4);
            bolt.setFill(Color.CYAN);
            bolt.setEffect(new Glow(0.6));
            return bolt;
        }

        double radius = durability == BulletDurability.TOUGH ? ENEMY_BULLET_RADIUS * 1.35 : ENEMY_BULLET_RADIUS;
        Circle circle;
        switch (durability) {
            case FRAGILE -> {
                circle = new Circle(radius, Color.LIMEGREEN);
                circle.setStroke(Color.WHITE);
                circle.setStrokeWidth(1.5);
            }
            case TOUGH -> {
                circle = new Circle(radius, Color.MEDIUMPURPLE);
                circle.setStroke(Color.WHITE);
                circle.setStrokeWidth(2.5);
            }
            default -> circle = new Circle(radius, Color.ORANGERED);
        }
        return circle;
    }

    /**
     * Called by CollisionManager once a bullet has done its job (hit
     * something, or its lifetime already expired inside BulletComponent).
     * Instead of destroying the entity, we park it off-screen, mark it
     * inactive, and return it to the correct pool so it can be reused.
     */
    public void recycle(Entity bullet, boolean wasPlayerBullet) {
        BulletComponent component = bullet.getComponent(BulletComponent.class);
        if (!component.isActive()) {
            // Already recycled earlier this same frame - do not release
            // it into the pool a second time.
            return;
        }

        component.deactivate();
        bullet.setVisible(false);
        bullet.setPosition(PARK_X, PARK_Y);

        BulletPool pool = selectPool(wasPlayerBullet, component.getDurability());
        pool.release(bullet);
    }

    /**
     * Drops every entity reference currently sitting in the pools, WITHOUT
     * touching those entities directly.
     *
     * NOTE - why this method exists:
     * Pooled bullets are parked (invisible, off-screen) rather than truly
     * removed from the FXGL world, so they can be woken back up cheaply -
     * see the "POOLING NOTE" in this class's javadoc. But if something
     * ELSE wipes the whole world (GameOverState.retry() does exactly
     * this, to clear the board for a fresh run), FXGL strips every
     * removed entity's components, including the parked bullets' the
     * BulletComponent they still need. If we didn't also clear the pools
     * here, BulletFactory would later hand out one of those now-broken
     * Entity references from obtain(), and the very next
     * recycled.getComponent(BulletComponent.class) call would throw
     * "Component BulletComponent not found!".
     *
     * Call this immediately after (or as part of) any full-world wipe.
     */
    public void clearPools() {
        playerBulletPool.clear();
        enemyIndestructiblePool.clear();
        enemyFragilePool.clear();
        enemyToughPool.clear();
    }
}
