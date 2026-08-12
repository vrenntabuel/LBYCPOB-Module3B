package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.state;

/**
 * GameStateManager.java
 * =====================
 * The "Context" object in the STATE DESIGN PATTERN - it holds a reference
 * to whichever GameState is currently active and forwards onUpdate() calls
 * to it every frame. Other code (Main.java, the HUD, input handlers) asks
 * this manager "what state are we in?" or tells it "switch to this state
 * now" without needing to know the internal details of any one state.
 */
public class GameStateManager {

    private GameState currentState;

    public void changeState(GameState newState) {
        if (currentState != null) {
            currentState.onExit();
        }
        currentState = newState;
        currentState.onEnter();
    }

    public void update(double tpf) {
        if (currentState != null) {
            currentState.onUpdate(tpf);
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public boolean isInState(Class<? extends GameState> stateClass) {
        return currentState != null && stateClass.isInstance(currentState);
    }
}
