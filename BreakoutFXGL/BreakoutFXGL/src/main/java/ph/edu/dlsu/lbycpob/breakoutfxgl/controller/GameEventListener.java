package ph.edu.dlsu.lbycpob.breakoutfxgl.controller;

/**
 * GameEventListener is a small, FXGL-free contract that lets the
 * CONTROLLER (GameManager) announce that something happened in the
 * game - "the ball just bounced", "a brick just broke" - without
 * GameManager needing to know or care WHO is listening, or HOW they
 * react (playing a sound? incrementing a combo counter? flashing the
 * screen?). GameManager only ever calls these two methods; it never
 * imports javafx.scene.media or anything else from the view/audio
 * layer. That keeps GameManager just as reusable and testable as
 * before this feature was added.
 *
 * TWO DESIGN IDEAS IN ONE SMALL INTERFACE:
 *
 * 1) OBSERVER PATTERN / DEPENDENCY INVERSION - the CONTROLLER defines
 *    this interface, and the audio package's AudioManager class
 *    (a VIEW-layer concern) implements it. The low-level detail (how
 *    a sound actually gets played) depends on the high-level contract
 *    the controller declares, not the other way around. This is the
 *    same reason PowerUp.applyEffect(GameManager) works the way it
 *    does elsewhere in this project.
 *
 * 2) NULL OBJECT PATTERN - NO_OP below is a listener that quietly does
 *    nothing. GameManager always has a valid listener to call (either
 *    NO_OP or a real AudioManager), so it never needs an
 *    "if (listener != null)" check scattered through its collision
 *    code. BreakoutApp swaps in a real AudioManager once the game
 *    starts; until then, or in a unit test that never sets one,
 *    GameManager works exactly the same, just silently.
 */
public interface GameEventListener {

    GameEventListener NO_OP = new GameEventListener() {
        @Override
        public void onBallBounce() {
            // Intentionally does nothing - see the Null Object explanation above.
        }

        @Override
        public void onBrickBroken() {
            // Intentionally does nothing - see the Null Object explanation above.
        }

        @Override
        public void onGameOver() {
            // Intentionally does nothing - see the Null Object explanation above.
        }

        @Override
        public void onVictory() {
            // Intentionally does nothing - see the Null Object explanation above.
        }
    };

    /** Called whenever the ball bounces off a wall, the paddle, or a brick. */
    void onBallBounce();

    /** Called whenever a brick is destroyed (a STRONG brick only triggers this on its final hit, not its first). */
    void onBrickBroken();

    void onGameOver();

    void onVictory();
}
