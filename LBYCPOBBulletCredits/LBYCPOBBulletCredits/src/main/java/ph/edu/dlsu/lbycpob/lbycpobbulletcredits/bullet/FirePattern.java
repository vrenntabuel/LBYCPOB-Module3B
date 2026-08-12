package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet;

import com.almasb.fxgl.entity.Entity;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;


/**
 * FirePattern.java
 * ================
 * This interface is the heart of the STRATEGY DESIGN PATTERN in this
 * project. Every enemy "OOP concept" holds a reference to one FirePattern
 * implementation. At run time we can swap which implementation an enemy
 * uses without changing the EnemyCredit class at all - that is exactly
 * what the Strategy pattern is for: separating an algorithm (how to shoot)
 * from the object that uses it (the enemy).
 *
 * BEGINNER NOTE:
 * An interface only declares WHAT a class must be able to do - it does not
 * say HOW. Each class that implements FirePattern (StraightPattern,
 * RadialBurstPattern, SpiralPattern, etc.) provides its own "how".
 */
public interface FirePattern {

    /**
     * Called once per frame by the owning EnemyCredit. Implementations
     * should track their own internal timers and only actually spawn
     * bullets when enough time has passed - do not spawn a bullet on
     * every single call or the screen will instantly fill up.
     *
     * @param sourceEntity    the enemy entity that is shooting
     * @param originX         x position bullets should spawn from
     * @param originY         y position bullets should spawn from
     * @param bulletFactory   used to actually create bullet entities
     * @param tpf             "time per frame" in seconds, supplied by FXGL
     * @param difficultyScale multiplier (>= 1.0) applied to speed/rate so
     *                        that later waves feel harder
     */
    void update(Entity sourceEntity, double originX, double originY,
                BulletFactory bulletFactory, double tpf, double difficultyScale);
}
