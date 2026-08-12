package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet;

import com.almasb.fxgl.entity.Entity;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * BulletPool.java
 * ===============
 * Implements the OBJECT POOL DESIGN PATTERN.
 *
 * BEGINNER NOTE - why pool bullets at all?
 * A bullet-hell game can have HUNDREDS of bullets on screen simultaneously.
 * If we called `new` and threw away an Entity every single time a bullet
 * was fired or destroyed, Java's garbage collector would be under constant
 * pressure, which can cause visible stutter/lag - very bad in a genre where
 * split-second dodging matters!
 *
 * Instead, an object pool keeps a "bench" of already-created, currently
 * unused bullet entities. When we need a bullet, we ask the pool first:
 *   - if a spare bullet is on the bench, we reuse it (cheap!)
 *   - only if the bench is empty do we create a brand-new one
 * When a bullet dies (hits something or expires), instead of destroying it
 * we send it back to the bench for next time.
 */
public class BulletPool {

    /** Bullets that are not currently in use and are ready to be reused. */
    private final Deque<Entity> availableBullets = new ArrayDeque<>();

    /**
     * Retrieves a spare bullet entity if one is available.
     *
     * @return a recycled Entity, or null if the pool is currently empty
     *         (the caller is then responsible for creating a fresh one).
     */
    public Entity obtain() {
        return availableBullets.poll();
    }

    /**
     * Returns a bullet entity to the pool once it is no longer in use.
     * The caller must make sure the entity has already been removed from
     * the game world / made invisible before calling this.
     */
    public void release(Entity bullet) {
        availableBullets.offer(bullet);
    }

    /** Mostly useful for debugging / a future on-screen bullet counter. */
    public int availableCount() {
        return availableBullets.size();
    }

    /**
     * Drops every reference currently held by the pool WITHOUT touching the
     * underlying entities. Used when something else (like GameOverState's
     * retry logic) has already wiped/is about to wipe those entities out of
     * the FXGL world directly - see BulletFactory.clearPools() for the full
     * explanation of why this matters.
     */
    public void clear() {
        availableBullets.clear();
    }
}
