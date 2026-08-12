package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity;

import com.almasb.fxgl.entity.component.Component;

/**
 * ShipComponent.java
 * ==================
 * Abstract base class shared by Player and AllyShip - both are "ships"
 * that can move around the screen in eight directions and can be damaged.
 * Putting the shared logic here (movement, invincibility-after-hit,
 * health tracking) means Player and AllyShip only need to add what makes
 * each of them unique.
 *
 * BEGINNER NOTE - ABSTRACT CLASS vs. INTERFACE:
 * We use an abstract class (not just an interface) here because Player
 * and AllyShip do not just SHARE A CONTRACT (like Damageable does) - they
 * share actual, reusable IMPLEMENTATION (the move() method body, the
 * invincibility countdown). That is the classic signal to reach for an
 * abstract class instead of an interface.
 */
public abstract class ShipComponent extends Component implements Damageable {

    protected int currentHealth;
    protected int maxHealth;
    protected double moveSpeed;

    /** Counts down after taking a hit; while > 0 the ship cannot be hurt again. */
    protected double invincibilityTimer = 0;
    protected static final double INVINCIBILITY_DURATION_SECONDS = 1.5;

    protected ShipComponent(int maxHealth, double moveSpeed) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.moveSpeed = moveSpeed;
    }

    /**
     * Moves the ship by a normalized direction vector (dx, dy each in
     * [-1, 1]) scaled by moveSpeed and frame time. Supports all eight
     * directions naturally: e.g. (1, -1) moves diagonally up-right, and
     * because we normalize the vector first, diagonal movement is not
     * faster than straight movement (a classic bullet-hell fairness rule).
     */
    public void move(double dx, double dy, double tpf) {
        double length = Math.hypot(dx, dy);
        if (length == 0) {
            return;
        }
        double normalizedX = dx / length;
        double normalizedY = dy / length;

        entity.translateX(normalizedX * moveSpeed * tpf);
        entity.translateY(normalizedY * moveSpeed * tpf);
    }

    @Override
    public void onUpdate(double tpf) {
        if (invincibilityTimer > 0) {
            invincibilityTimer -= tpf;
            // Simple visual feedback: flicker opacity while invincible so
            // the player gets clear confirmation that a hit landed and
            // that they currently cannot be hurt again.
            boolean visibleThisFrame = (Math.floor(invincibilityTimer * 10) % 2 == 0);
            entity.setOpacity(visibleThisFrame ? 0.4 : 1.0);
        } else {
            entity.setOpacity(1.0);
        }
    }

    @Override
    public void takeDamage(int amount) {
        if (invincibilityTimer > 0 || isDestroyed()) {
            return; // can't be hurt while flashing/invincible or already gone
        }
        currentHealth = Math.max(0, currentHealth - amount);
        invincibilityTimer = INVINCIBILITY_DURATION_SECONDS;

        onDamaged();

        if (isDestroyed()) {
            onDestroyed();
        }
    }

    @Override
    public boolean isDestroyed() {
        return currentHealth <= 0;
    }

    @Override
    public int getCurrentHealth() {
        return currentHealth;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isInvincible() {
        return invincibilityTimer > 0;
    }

    /** Hook for subclasses to react to taking damage (e.g. play a sound). */
    protected abstract void onDamaged();

    /** Hook for subclasses to react to being destroyed (e.g. respawn logic). */
    protected abstract void onDestroyed();
}
