package ph.edu.dlsu.lbycpob.breakoutfxgl.model;

/**
 * BrickType is a small enum (a fixed list of named constants) that
 * describes the different kinds of bricks in the game.
 *
 * Using an enum here (instead of, say, an int code like 0, 1, 2) is
 * good practice: the code that follows can never accidentally use an
 * invalid brick type, and each constant can carry its own data
 * (hit points, colors, and image paths), which is a lightweight
 * example of how enums in Java are actually full classes under the
 * hood.
 *
 * IMAGE-BASED RENDERING WITH A ROBUST FALLBACK:
 * Each type points at two PNG file names - a "before" image (the
 * brick at full health) and an "after" image (the brick once it has
 * taken damage but is not destroyed yet, i.e. right before it
 * breaks). The actual image LOADING and the "what if the file is
 * missing" fallback logic lives in the view layer
 * (view/BrickTextureLoader.java and view/BrickViewFactory.java) - this
 * class only stores WHERE those images are expected to be found, plus
 * a plain color to fall back on so the game is always playable even
 * with no art assets at all.
 *
 * Expected image files (place them under src/main/resources/assets/textures/):
 *   brick_normal.png
 *   brick_strong.png
 *   brick_strong_cracked.png
 *   brick_unbreakable.png
 * NORMAL bricks break in a single hit, so their "cracked" image would
 * never actually be shown - it is left out on purpose (afterImagePath
 * is null for NORMAL and UNBREAKABLE).
 */
public enum BrickType {

    // hitPoints, colorHex, damagedColorHex, beforeImagePath, afterImagePath
    NORMAL(1, "#0984e3", "#0984e3",
            "/assets/textures/brick_normal.png", null),

    STRONG(2, "#fdcb6e", "#e17055",
            "/assets/textures/brick_strong.png", "/assets/textures/brick_strong_cracked.png"),

    UNBREAKABLE(Integer.MAX_VALUE, "#636e72", "#636e72",
            "/assets/textures/brick_unbreakable.png", null);

    private final int hitPoints;
    private final String colorHex;
    private final String damagedColorHex;
    private final String beforeImagePath;
    private final String afterImagePath;

    BrickType(int hitPoints, String colorHex, String damagedColorHex,
              String beforeImagePath, String afterImagePath) {
        this.hitPoints = hitPoints;
        this.colorHex = colorHex;
        this.damagedColorHex = damagedColorHex;
        this.beforeImagePath = beforeImagePath;
        this.afterImagePath = afterImagePath;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    /** Fallback rectangle color used when this brick is at full health (or has no "damaged" look). */
    public String getColorHex() {
        return colorHex;
    }

    /** Fallback rectangle color used when this brick has been hit but not yet destroyed. */
    public String getDamagedColorHex() {
        return damagedColorHex;
    }

    /** Classpath location of the "full health" PNG for this brick type. */
    public String getBeforeImagePath() {
        return beforeImagePath;
    }

    /** Classpath location of the "damaged" PNG for this brick type, or null if this type has no such state. */
    public String getAfterImagePath() {
        return afterImagePath;
    }
}
