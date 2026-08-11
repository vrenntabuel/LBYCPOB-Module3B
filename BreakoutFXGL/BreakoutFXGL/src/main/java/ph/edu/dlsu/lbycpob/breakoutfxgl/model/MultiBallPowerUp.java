package ph.edu.dlsu.lbycpob.breakoutfxgl.model;


import ph.edu.dlsu.lbycpob.breakoutfxgl.controller.GameManager;

/**
 * Concrete PowerUp: duplicates the first active ball, giving the
 * player two balls to play with at once.
 */
public class MultiBallPowerUp extends PowerUp {

    public MultiBallPowerUp(double x, double y) {
        super(x, y);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        gameManager.spawnExtraBall();
    }

    @Override
    public String getLabel() {
        return "MULTI";
    }

    @Override
    public String getColorHex() {
        return "#9b59b6"; // purple
    }
}
