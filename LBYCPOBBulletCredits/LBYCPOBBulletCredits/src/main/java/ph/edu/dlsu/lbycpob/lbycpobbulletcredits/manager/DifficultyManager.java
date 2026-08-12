package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.manager;

/**
 * DifficultyManager.java
 * ======================
 * SINGLETON DESIGN PATTERN implementation that makes the game harder the
 * longer a run lasts, independent of which wave the player is on. This is
 * the "Difficulty Scaling" requirement from the design brief: "Difficulty
 * increases continuously throughout the credits."
 *
 * HOW IT COMBINES WITH PER-WAVE DIFFICULTY:
 * Each Wave already has its own difficultyMultiplier (see data/Wave.java),
 * so a later wave already fires faster/denser than an earlier one. This
 * class adds a SECOND, independent multiplier layered on top, driven by a
 * clock instead of wave progress - so even if the player lingers on a
 * single wave for a long time, bullets keep getting faster and denser.
 * EnemyCreditComponent multiplies both factors together every frame (see
 * EnemyCreditComponent.onUpdate) so existing, already-spawned enemies
 * speed up over time too, not just newly-spawned ones.
 */
public class DifficultyManager {

    private static DifficultyManager instance;

    /** How much the time-based multiplier grows per second survived. */
    private static final double RAMP_PER_SECOND = 0.02;

    /** Upper bound so the game becomes "nearly impossible" rather than
     *  literally unplayable/glitchy from absurdly large numbers. */
    private static final double MAX_MULTIPLIER = 3.5;

    private double elapsedSeconds = 0;

    private DifficultyManager() {
        // private constructor - see class javadoc on the Singleton pattern
    }

    public static DifficultyManager getInstance() {
        if (instance == null) {
            instance = new DifficultyManager();
        }
        return instance;
    }

    /** Call once per frame while gameplay is active (see PlayingState.onUpdate). */
    public void update(double tpf) {
        elapsedSeconds += tpf;
    }

    /**
     * @return a multiplier starting at 1.0 and climbing steadily the longer
     *         the current run has lasted, capped at MAX_MULTIPLIER.
     */
    public double getTimeMultiplier() {
        return Math.min(MAX_MULTIPLIER, 1.0 + (elapsedSeconds * RAMP_PER_SECOND));
    }

    public double getElapsedSeconds() {
        return elapsedSeconds;
    }

    /** Called on a fresh run (first start, or after a Game Over retry) so
     *  difficulty ramps back up from the beginning rather than staying
     *  maxed out from a previous attempt. */
    public void reset() {
        elapsedSeconds = 0;
    }
}
