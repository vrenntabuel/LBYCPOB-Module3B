package ph.edu.dlsu.lbycpob.breakoutfxgl.model;

/**
 * Paddle represents the player-controlled bar at the bottom of the
 * screen. Like Ball, it EXTENDS GameObject and reuses all the shared
 * position/size/collision code.
 */
public class Paddle extends GameObject {

    private final double speed; // pixels per second

    private static final double DEFAULT_WIDTH = 120;
    private static final double DEFAULT_HEIGHT = 20;
    private static final double MIN_WIDTH = 50;
    private static final double MAX_WIDTH = 240;

    public Paddle(double x, double y) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.speed = 480;
    }

    /**
     * The Paddle still MUST override update() because it is abstract
     * in GameObject, even though the paddle does not move by itself
     * every frame (it only moves when the player presses a key, which
     * is handled in moveLeft()/moveRight() below, called directly
     * from the keyboard input handler). This is a good example for
     * students that "override" does not always mean "do something
     * complicated" - an intentionally empty method body is still a
     * valid, meaningful override.
     */
    @Override
    public void update(double deltaSeconds) {
        // No automatic movement; the paddle only moves on key press.
    }

    /** Moves the paddle left, but never past the left wall (minX). */
    public void moveLeft(double deltaSeconds, double minX) {
        double newX = getX() - speed * deltaSeconds;
        setX(Math.max(minX, newX));
    }

    /** Moves the paddle right, but never past the right wall (maxX). */
    public void moveRight(double deltaSeconds, double maxX) {
        double newX = getX() + speed * deltaSeconds;
        setX(Math.min(maxX - getWidth(), newX));
    }

    /**
     * Returns where along the paddle (from -1.0 left edge to 1.0
     * right edge) the given x-coordinate lands. Used by Ball to
     * decide the bounce angle.
     */
    public double relativeHitPosition(double ballCenterX) {
        double paddleCenterX = getX() + getWidth() / 2.0;
        double half = getWidth() / 2.0;
        double offset = (ballCenterX - paddleCenterX) / half;
        // clamp between -1 and 1 in case the ball hit right at the corner
        return Math.max(-1.0, Math.min(1.0, offset));
    }

    /** Widens the paddle (WidenPaddlePowerUp effect), capped at MAX_WIDTH. */
    public void widen(double amount) {
        setWidth(Math.min(MAX_WIDTH, getWidth() + amount));
    }

    /** Shrinks the paddle (ShrinkPaddlePowerUp effect), floored at MIN_WIDTH. */
    public void shrink(double amount) {
        setWidth(Math.max(MIN_WIDTH, getWidth() - amount));
    }
}
