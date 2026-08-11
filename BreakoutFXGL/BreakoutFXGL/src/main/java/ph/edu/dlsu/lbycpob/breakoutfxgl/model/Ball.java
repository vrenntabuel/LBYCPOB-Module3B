package ph.edu.dlsu.lbycpob.breakoutfxgl.model;

/**
 * Ball represents the bouncing ball in Breakout.
 * <p>
 * Ball EXTENDS GameObject, so it automatically has x, y, width, height,
 * getters/setters, and collidesWith() without us writing that code
 * again. We only add what is special about a ball: its own speed in
 * the X and Y direction (dx, dy) and its radius.
 */
public class Ball extends GameObject {

    // A ball is round, but our collision detection uses rectangles,
    // so we still store width/height (equal to radius * 2) in the
    // parent class, and keep "radius" here for drawing the circle.
    private final double radius;

    // Speed along each axis, in pixels per second. Positive dx means
    // moving right; positive dy means moving down.
    private double dx;
    private double dy;

    private static final double DEFAULT_SPEED = 260.0;

    public Ball(double x, double y, double radius) {
        // "super(...)" calls the GameObject constructor above.
        super(x, y, radius * 2, radius * 2);
        this.radius = radius;
        this.dx = DEFAULT_SPEED;
        this.dy = -DEFAULT_SPEED; // start moving upward
    }

    public double getRadius() {
        return radius;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    /**
     * This is the Ball's OWN version of update(), required because
     * GameObject.update() is abstract. This is POLYMORPHISM in action:
     * GameManager will simply call ball.update(deltaSeconds) without
     * knowing (or needing to know) that this is the code that runs.
     */
    @Override
    public void update(double deltaSeconds) {
        setX(getX() + dx * deltaSeconds);
        setY(getY() + dy * deltaSeconds);
    }

    /** Reverses horizontal direction. Used when hitting a side wall or a brick's side. */
    public void bounceHorizontal() {
        dx = -dx;
    }

    /** Reverses vertical direction. Used when hitting the ceiling, a brick's top/bottom, or the paddle. */
    public void bounceVertical() {
        dy = -dy;
    }

    /**
     * Bouncing off the paddle is more interesting than a plain
     * vertical bounce: where the ball hits the paddle changes its
     * angle, just like in the original arcade game. This gives the
     * player some control over aiming.
     *
     * @param hitOffset a value from -1.0 (left edge of paddle)
     *                  to 1.0 (right edge of paddle), 0 = center
     */
    public void bounceOffPaddle(double hitOffset) {
        double speed = Math.sqrt(dx * dx + dy * dy);
        double maxAngle = Math.toRadians(60); // steepest allowed angle
        double angle = hitOffset * maxAngle;

        dx = speed * Math.sin(angle);
        dy = -Math.abs(speed * Math.cos(angle)); // always bounce upward
    }

    /** Slightly increases speed - could be used as a difficulty-ramp power-up effect. */
    public void increaseSpeed(double multiplier) {
        dx *= multiplier;
        dy *= multiplier;
    }

    /** Makes a fresh copy of this ball at the same position - used by the multi-ball power-up. */
    public Ball copy() {
        Ball clone = new Ball(getX(), getY(), radius);
        // Send the clone off in a slightly different direction so it
        // does not travel exactly on top of the original ball.
        clone.dx = -this.dx;
        clone.dy = this.dy;
        return clone;
    }
}
