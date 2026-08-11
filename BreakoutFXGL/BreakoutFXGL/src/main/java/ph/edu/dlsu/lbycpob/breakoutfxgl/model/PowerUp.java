package ph.edu.dlsu.lbycpob.breakoutfxgl.model;


import ph.edu.dlsu.lbycpob.breakoutfxgl.controller.GameManager;

/**
 * PowerUp is the abstract parent class for every falling bonus item
 * in the game (widen paddle, shrink paddle, multi-ball, ...).
 * <p>
 * PowerUp EXTENDS GameObject, so it already has position/size/collision
 * behavior. It adds two things every power-up needs:
 * <p>
 *   1. A shared falling motion (see update() below) - this part is
 *      the SAME for every power-up, so we write it once here.
 * <p>
 *   2. An abstract applyEffect() method - this part is DIFFERENT for
 *      every power-up, so each subclass (WidenPaddlePowerUp,
 *      ShrinkPaddlePowerUp, MultiBallPowerUp, ...) must supply its own.
 * <p>
 * This mix of "shared code lives in the parent, unique code lives in
 * the child" is a very common and very useful inheritance pattern.
 * <p>
 * POLYMORPHISM PAYOFF: GameManager keeps a single List of whatever
 * power-ups are currently falling. When the paddle catches one,
 * GameManager just calls powerUp.applyEffect(this) - it does not need
 * an if/else chain checking "is this a widen power-up? is this a
 * multi-ball power-up?" Java picks the correct method automatically
 * based on the real (subclass) type of the object at runtime.
 */
public abstract class PowerUp extends GameObject {

    private static final double FALL_SPEED = 130.0; // pixels per second
    private static final double SIZE = 22;

    public PowerUp(double x, double y) {
        super(x, y, SIZE, SIZE);
    }

    @Override
    public void update(double deltaSeconds) {
        setY(getY() + FALL_SPEED * deltaSeconds);
    }

    /**
     * Applies this power-up's unique effect to the running game.
     * Passing in the GameManager lets each power-up reach whatever
     * part of the game it needs to change (the paddle, the ball list,
     * the score, and so on) without PowerUp needing to know about all
     * of those details itself.
     */
    public abstract void applyEffect(GameManager gameManager);

    /**
     * A short label used by the view layer to draw text on top of the
     * falling power-up icon (for example "WIDE", "SHRINK", "MULTI").
     * Also abstract, for the same polymorphism reasons as applyEffect().
     */
    public abstract String getLabel();

    /** Hex color used to draw this power-up; subclasses may override for variety. */
    public String getColorHex() {
        return "#2ecc71";
    }
}
