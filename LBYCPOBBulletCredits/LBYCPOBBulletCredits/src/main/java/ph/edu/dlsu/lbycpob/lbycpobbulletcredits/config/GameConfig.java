package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.config;

/**
 * GameConfig.java
 * ===============
 * Another SINGLETON DESIGN PATTERN example (see also GameEventBus and
 * AudioManager), this time for simple global settings that many parts of
 * the game might want to read - screen size and a master difficulty
 * multiplier the player could eventually adjust from a settings menu.
 */
public class GameConfig {

    private static GameConfig instance;

    private final int screenWidth = 1280;
    private final int screenHeight = 720;

    /** Extra multiplier on top of each wave's own difficulty scaling -
     *  kept at 1.0 by default, but exposed so a future settings screen
     *  could offer "Easy / Normal / Bullet Hell" presets. */
    private double masterDifficultyMultiplier = 1.0;

    private GameConfig() {
        // private constructor - see class javadoc
    }

    public static GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public double getMasterDifficultyMultiplier() {
        return masterDifficultyMultiplier;
    }

    public void setMasterDifficultyMultiplier(double masterDifficultyMultiplier) {
        this.masterDifficultyMultiplier = masterDifficultyMultiplier;
    }
}
