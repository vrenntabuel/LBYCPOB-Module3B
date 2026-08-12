package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity;

/**
 * EntityType.java
 * ================
 * FXGL asks every entity "what kind of thing are you?" so that collision
 * handlers and factories know how to treat it. This enum lists every kind
 * of object that can exist in our game world.
 *
 * BEGINNER NOTE:
 * An enum is just a fixed, named list of constant values. We use it here
 * instead of plain Strings ("player", "bullet", ...) because the compiler
 * will catch typos for us - EntityType.PLAYER can never be misspelled the
 * way "plyaer" could.
 */
public enum EntityType {
    PLAYER,
    ALLY_SHIP,
    ENEMY_CREDIT,
    PLAYER_BULLET,

    /**
     * FRAGILE or TOUGH enemy bullets (see bullet/BulletDurability.java) -
     * these DO have a collision handler registered against PLAYER_BULLET
     * so the player can shoot them down.
     */
    ENEMY_BULLET,

    /**
     * INDESTRUCTIBLE enemy bullets get their OWN entity type, deliberately
     * separate from ENEMY_BULLET above.
     *
     * BEGINNER NOTE - why a separate type instead of an if-check?
     * An earlier version of this game used a single ENEMY_BULLET type for
     * everything and had the PLAYER_BULLET-vs-ENEMY_BULLET collision
     * handler check "is this bullet indestructible?" and simply return
     * early if so, leaving both bullets untouched. In principle that
     * should behave identically to true pass-through - but relying on a
     * per-collision no-op means the physics engine still runs a full
     * collision check for every single indestructible bullet, every
     * frame, and any future change to that handler risks accidentally
     * breaking pass-through again.
     *
     * Giving indestructible bullets their own EntityType instead means we
     * simply never register a CollisionHandler between PLAYER_BULLET and
     * ENEMY_BULLET_INDESTRUCTIBLE at all (see CollisionManager). FXGL's
     * PhysicsWorld only checks entity pairs that have a matching handler
     * registered, so it will never even attempt a hit test between the
     * two - pass-through is then guaranteed structurally, not just by
     * careful if-branching.
     */
    ENEMY_BULLET_INDESTRUCTIBLE,

    BACKGROUND
}
