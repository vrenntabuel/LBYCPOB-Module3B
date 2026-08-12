package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity;

/**
 * Damageable.java
 * ===============
 * Any part of the game that can be hurt and eventually destroyed
 * (the player, ally ships, enemy "OOP concepts") implements this
 * interface. CollisionManager only needs to know "can I damage you?" -
 * it does not need to know whether it is dealing with a Player or an
 * EnemyCredit. This is a small example of POLYMORPHISM: the same
 * takeDamage() call behaves differently depending on the concrete class
 * that receives it.
 */
public interface Damageable {

    /**
     * Applies damage. Implementations decide what "damage" means for them
     * (losing a life vs. losing a hit point) and should ignore the call
     * while temporarily invincible, if applicable.
     */
    void takeDamage(int amount);

    /** @return true once this object has no health/lives remaining. */
    boolean isDestroyed();

    /** @return current health, for HUD/health-bar display. */
    int getCurrentHealth();

    /** @return maximum health, for HUD/health-bar display. */
    int getMaxHealth();
}
