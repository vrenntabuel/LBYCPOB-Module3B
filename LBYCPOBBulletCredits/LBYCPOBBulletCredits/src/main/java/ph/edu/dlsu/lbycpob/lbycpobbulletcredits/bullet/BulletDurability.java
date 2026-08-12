package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet;

/**
 * BulletDurability.java
 * ======================
 * Describes whether an ENEMY bullet can be destroyed by the player
 * shooting it, and if so, how tough it is:
 *
 *   INDESTRUCTIBLE - ignores player bullets completely; must be dodged.
 *                     This is still the classic bullet-hell default.
 *   FRAGILE        - destroyed by a single player bullet. Gives the
 *                     player some agency to thin out a pattern instead of
 *                     only ever dodging.
 *   TOUGH          - survives several player bullets before breaking -
 *                     used for boss-tier bullets so shooting them down is
 *                     possible but costly, not a free escape.
 *
 * BEGINNER NOTE:
 * This is not used for player bullets at all (they are always consumed
 * the instant they hit anything) - only for bullets fired BY enemy
 * "OOP concept" entities.
 */
public enum BulletDurability {
    INDESTRUCTIBLE(0),
    FRAGILE(1),
    TOUGH(3);

    /** How many player-bullet hits are needed to destroy this bullet.
     *  0 means "cannot be destroyed, ever". */
    public final int hitsToDestroy;

    BulletDurability(int hitsToDestroy) {
        this.hitsToDestroy = hitsToDestroy;
    }
}
