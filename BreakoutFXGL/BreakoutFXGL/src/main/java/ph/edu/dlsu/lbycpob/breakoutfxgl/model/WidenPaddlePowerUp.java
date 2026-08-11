package ph.edu.dlsu.lbycpob.breakoutfxgl.model;


import ph.edu.dlsu.lbycpob.breakoutfxgl.controller.GameManager;

/**
 * Concrete PowerUp: makes the paddle wider, making the ball easier
 * to hit. This class only needs to fill in the two abstract methods
 * from PowerUp - everything else (falling motion, position, size,
 * collision) is inherited for free.
 */
public class WidenPaddlePowerUp extends PowerUp {

    private static final double WIDEN_AMOUNT = 40;

    public WidenPaddlePowerUp(double x, double y) {
        super(x, y);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        gameManager.getPaddle().widen(WIDEN_AMOUNT);
    }

    @Override
    public String getLabel() {
        return "WIDE";
    }

    @Override
    public String getColorHex() {
        return "#2ecc71"; // green
    }
}
