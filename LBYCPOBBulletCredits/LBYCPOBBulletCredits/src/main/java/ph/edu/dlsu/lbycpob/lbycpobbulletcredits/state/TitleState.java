package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.state;


import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.GameContext;

/**
 * TitleState.java
 * ===============
 * The very first state the game is in. Shows a title/instructions message
 * and waits for the player to press ENTER (handled centrally in Main.java,
 * which asks gameStateManager which state is active before deciding what
 * a key press should do).
 */
public class TitleState implements GameState {

    private final GameContext context;

    public TitleState(GameContext context) {
        this.context = context;
    }

    @Override
    public void onEnter() {
        context.hud.showMessage("OOP BULLET CREDITS\n\nFight through the Pillars of OOP!\nArrow keys / WASD to move, auto-fire is always on.\n\nPress ENTER to start");
    }

    @Override
    public void onExit() {
        context.hud.hideMessage();
    }

    @Override
    public void onUpdate(double tpf) {
        // Nothing to update on the title screen itself - it is purely
        // waiting for player input, handled in Main.java.
    }

    @Override
    public String getName() {
        return "TITLE";
    }
}
