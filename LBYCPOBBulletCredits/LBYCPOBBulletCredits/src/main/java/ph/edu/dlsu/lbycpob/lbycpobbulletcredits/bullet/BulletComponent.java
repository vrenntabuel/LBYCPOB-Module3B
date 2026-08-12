package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet;

import com.almasb.fxgl.entity.component.Component;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;


import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;

/**
 * BulletComponent.java
 * ====================
 * An FXGL "Component" is a small, reusable chunk of behavior that gets
 * attached to an Entity (FXGL's version of the Component pattern, closely
 * related to an Entity-Component-System architecture). This component
 * gives ANY entity it's attached to the ability to travel in a straight
 * line at a constant speed, eventually expire, and - for ENEMY bullets -
 * absorb a certain number of hits from player bullets before breaking.
 *
 * NOTE:
 * This class does still need to know which BulletPool it belongs to (via
 * BulletFactory) so it can hand itself back when its lifetime runs out
 * naturally (see onUpdate below) - but it does NOT need to know how
 * bullets are drawn, how patterns decide when to fire, etc. Keeping that
 * one small piece of self-recycling knowledge here (rather than requiring
 * some other class to notice every expired bullet) keeps the Object Pool
 * pattern actually effective - see the note on recycling below.
 */
public class BulletComponent extends Component {
    private static final double OFFSCREEN_MARGIN = 80;

    private double velocityX;
    private double velocityY;

    /** Safety-net expiry - see the class javadoc above. */
    private double remainingLifetime;

    private final BulletFactory bulletFactory;
    private final boolean isPlayerBullet;

    /**
     * How tough this bullet is against PLAYER bullets. Fixed for the
     * lifetime of this pooled entity - see BulletFactory, which keeps a
     * separate pool per durability tier specifically so a pooled bullet
     * never needs to change durability (and therefore never needs its
     * appearance re-styled) when it is reused.
     */
    private final BulletDurability durability;
    private int remainingHits;

    /**
     * Whether this bullet is currently "live" in the game.
     *
     * NOTE ON POOLING:
     * Rather than truly destroying and re-creating bullet entities (which
     * FXGL does not make cheap to undo mid-game), pooled bullets stay
     * attached to the game world permanently but sit "asleep" (isActive =
     * false, invisible, parked far off-screen) until BulletFactory wakes
     * them back up via relaunch(). This keeps the Object Pool pattern's
     * performance benefit while working naturally with FXGL's entity
     * lifecycle.
     */
    private boolean isActive = true;

    public BulletComponent(double velocityX, double velocityY, double lifetimeSeconds,
                            BulletFactory bulletFactory, boolean isPlayerBullet,
                            BulletDurability durability) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.remainingLifetime = lifetimeSeconds;
        this.bulletFactory = bulletFactory;
        this.isPlayerBullet = isPlayerBullet;
        this.durability = durability;
        this.remainingHits = durability.hitsToDestroy;
    }

    /** Allows BulletFactory to re-launch a recycled bullet in a new direction. */
    public void relaunch(double velocityX, double velocityY, double lifetimeSeconds) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.remainingLifetime = lifetimeSeconds;
        this.remainingHits = durability.hitsToDestroy;
        this.isActive = true;
    }

    /** Puts this bullet to sleep so BulletFactory can safely reuse it later. */
    public void deactivate() {
        this.isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public BulletDurability getDurability() {
        return durability;
    }

    public boolean isIndestructible() {
        return durability == BulletDurability.INDESTRUCTIBLE;
    }

    /**
     * Called by CollisionManager when a player bullet hits this (enemy)
     * bullet.
     *
     * @return true if this hit was enough to destroy the bullet (caller
     *         should then recycle it); false if it survived (INDESTRUCTIBLE
     *         bullets always return false).
     */
    public boolean registerHitFromPlayerBullet() {
        if (isIndestructible()) {
            return false;
        }
        remainingHits--;
        return remainingHits <= 0;
    }

    @Override
    public void onUpdate(double tpf) {
        if (!isActive) {
            return;
        }

        // entity.translate(...) moves the entity by the given amount this frame.
        entity.translateX(velocityX * tpf);
        entity.translateY(velocityY * tpf);

        remainingLifetime -= tpf;

        double x = entity.getX();
        double y = entity.getY();
        boolean offScreen = x < -OFFSCREEN_MARGIN || x > getAppWidth() + OFFSCREEN_MARGIN
                || y < -OFFSCREEN_MARGIN || y > getAppHeight() + OFFSCREEN_MARGIN;

        if (offScreen || remainingLifetime <= 0) {
            // IMPORTANT: a bullet that leaves the screen (the normal case)
            // or hits the safety-net timer (the rare/abnormal case) must
            // still be handed back to BulletFactory's pool here - not just
            // marked invisible. Collisions already recycle bullets via
            // CollisionManager, but most bullets in a bullet-hell game
            // miss everything and only ever get recycled this way. If
            // this branch did not also call recycle(), those bullets
            // would never re-enter the pool, and the game would slowly
            // accumulate more and more "ghost" entities for the entire
            // session - exactly what pooling is meant to avoid.
            bulletFactory.recycle(entity, isPlayerBullet);
        }
    }
}
