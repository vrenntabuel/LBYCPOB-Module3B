package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.state;

/**
 * GameState.java
 * ==============
 * Interface for the STATE DESIGN PATTERN, applied to overall game flow
 * (Title -> Playing -> Paused -> Game Over / Victory).
 *
 * NOTE:
 * Without the State pattern, code driving the game loop tends to fill up
 * with giant if/else chains like:
 *     if (currentStateName.equals("PLAYING")) { ... }
 *     else if (currentStateName.equals("PAUSED")) { ... }
 * The State pattern instead gives each state its OWN small class, and the
 * surrounding code just calls state.onUpdate(tpf) without caring which
 * concrete state it currently holds - another example of polymorphism
 * doing the "which branch do I take" work for us.
 */
public interface GameState {

    /** Called exactly once, the moment this state becomes active. */
    void onEnter();

    /** Called exactly once, the moment this state is replaced by another. */
    void onExit();

    /** Called every frame while this state is the active one. */
    void onUpdate(double tpf);

    /** A short label, mainly useful for debugging/HUD display. */
    String getName();
}
