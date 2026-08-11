package ph.edu.dlsu.lbycpob.breakoutfxgl.model;


import ph.edu.dlsu.lbycpob.breakoutfxgl.controller.GameManager;

/**
 * Concrete PowerUp: shrinks the paddle. Included so students see that
 * not every "power-up" has to help the player - some are penalties,
 * and the same PowerUp framework handles both cases without any
 * change to GameManager's collision-handling code.
 */
public class ShrinkPaddlePowerUp extends PowerUp {

    private static final double SHRINK_AMOUNT = 35;

    public ShrinkPaddlePowerUp(double x, double y) {
        super(x, y);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        gameManager.getPaddle().shrink(SHRINK_AMOUNT);
    }

    @Override
    public String getLabel() {
        return "SHRINK";
    }

    @Override
    public String getColorHex() {
        return "#e74c3c"; // red, signals "danger" to the player
    }
}
