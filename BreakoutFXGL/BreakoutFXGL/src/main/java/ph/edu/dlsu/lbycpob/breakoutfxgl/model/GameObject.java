package ph.edu.dlsu.lbycpob.breakoutfxgl.model;

/**
 * GameObject is the parent (base) class for every object that can move
 * around or be collided with in the Breakout game: Ball, Paddle, Brick,
 * and PowerUp all extend this class.
 * <p>
 * WHY THIS CLASS EXISTS (INHERITANCE):
 * Instead of writing x, y, width, height, and collision code four
 * separate times, we write it ONCE here. Ball, Paddle, Brick, and
 * PowerUp then reuse ("inherit") this code with the "extends" keyword.
 * <p>
 * WHY THE FIELDS ARE PRIVATE (ENCAPSULATION):
 * Other classes are not allowed to change x or y directly
 * (myBall.x = 5 would not even compile). They must go through the
 * getX()/setX() methods below. This protects the internal state of
 * the object and lets us add validation later (for example, making
 * sure width never becomes negative) without breaking other code.
 * <p>
 * WHY update() IS abstract (POLYMORPHISM):
 * This class does not know HOW a Ball should move versus how a Brick
 * should sit still, so it leaves update() unimplemented and forces
 * every subclass to provide its own version. Later, GameManager will
 * hold a list of GameObjects and call update() on each one without
 * caring what specific type it is - Java automatically runs the
 * correct method for each object at runtime.
 */
public abstract class GameObject {

    // "private" fields = encapsulated data, hidden from other classes.
    private double x;
    private double y;
    private double width;
    private double height;

    /**
     * Constructor: runs once when a new GameObject (or, really, one of
     * its subclasses) is created with "new".
     */
    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // ---------------------------------------------------------------
    // Getters and setters (the "public doorway" into our private data)
    // ---------------------------------------------------------------

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        // A tiny bit of validation to protect the object's own state -
        // this is only possible because "width" is private.
        if (width > 0) {
            this.width = width;
        }
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        if (height > 0) {
            this.height = height;
        }
    }

    /**
     * Convenience methods used often enough during collision checks
     * that it is worth giving them names instead of repeating the
     * math (x + width) everywhere.
     */
    public double getRight() {
        return x + width;
    }

    public double getBottom() {
        return y + height;
    }

    /**
     * update() runs once per animation frame (about 60 times a second).
     * deltaSeconds is the time, in seconds, since the previous frame -
     * we multiply speeds by this value so the game runs at the same
     * speed on both a fast and a slow computer.
     * <p>
     * Every subclass MUST override this method (that is what
     * "abstract" forces). This is our POLYMORPHISM hook.
     */
    public abstract void update(double deltaSeconds);

    /**
     * Simple axis-aligned bounding box (AABB) collision test.
     * Two rectangles overlap if, on both the X axis and the Y axis,
     * they overlap at the same time.
     * <p>
     * This one method is reused for EVERY kind of collision in the
     * game: ball-vs-paddle, ball-vs-brick, paddle-vs-powerup, and so
     * on, because all of those objects are GameObjects.
     */
    public boolean collidesWith(GameObject other) {
        boolean overlapX = this.x < other.getRight() && this.getRight() > other.x;
        boolean overlapY = this.y < other.getBottom() && this.getBottom() > other.y;
        return overlapX && overlapY;
    }
}
