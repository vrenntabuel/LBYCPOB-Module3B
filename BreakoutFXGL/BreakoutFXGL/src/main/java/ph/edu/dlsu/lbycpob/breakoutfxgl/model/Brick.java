package ph.edu.dlsu.lbycpob.breakoutfxgl.model;

/**
 * Brick represents one destructible block in the wall of bricks.
 * Like Ball and Paddle, it EXTENDS GameObject.
 */
public class Brick extends GameObject {

    private final BrickType type;
    private int remainingHitPoints;
    private boolean destroyed;

    public Brick(double x, double y, double width, double height, BrickType type) {
        super(x, y, width, height);
        this.type = type;
        this.remainingHitPoints = type.getHitPoints();
        this.destroyed = false;
    }

    /**
     * Bricks never move, so this override is intentionally empty -
     * the same idea explained in Paddle.update(): every GameObject
     * must provide *some* implementation, even a "do nothing" one,
     * because update() is abstract in the parent class.
     */
    @Override
    public void update(double deltaSeconds) {
        // Bricks are stationary.
    }

    public BrickType getType() {
        return type;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public int getRemainingHitPoints() {
        return remainingHitPoints;
    }

    /**
     * True once this brick has absorbed at least one hit but is still
     * alive (for example, a STRONG brick after its first hit). The
     * view layer uses this to decide whether to show the "before" or
     * "after" (cracked) image/color for this brick.
     */
    public boolean isDamaged() {
        return !destroyed && remainingHitPoints < type.getHitPoints();
    }

    /**
     * Called by GameManager when the ball collides with this brick.
     * Returns true if the brick was destroyed as a result of this hit,
     * which GameManager uses to decide whether to award points and
     * possibly spawn a PowerUp.
     */
    public boolean hit() {
        if (type == BrickType.UNBREAKABLE) {
            return false; // never breaks, no matter how many hits
        }
        remainingHitPoints--;
        if (remainingHitPoints <= 0) {
            destroyed = true;
        }
        return destroyed;
    }
}
