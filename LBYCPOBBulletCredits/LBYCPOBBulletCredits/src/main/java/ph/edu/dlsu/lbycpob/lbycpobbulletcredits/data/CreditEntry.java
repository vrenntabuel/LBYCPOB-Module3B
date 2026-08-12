package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.data;


import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet.FirePattern;

/**
 * CreditEntry.java
 * ================
 * This is a plain data class (sometimes called a "POJO" - Plain Old Java
 * Object) describing ONE enemy that will scroll onto the screen, just like
 * a name scrolling by in a game's ending credits. Instead of a programmer's
 * name, our "credits" are Object-Oriented Programming concepts.
 *
 * NOTE:
 * We keep this class free of any FXGL/JavaFX code on purpose. It only
 * stores DATA. The EnemyCreditFactory later reads this data and builds an
 * actual on-screen entity from it. This separation (data vs. presentation)
 * is itself a small example of encapsulation and single-responsibility
 * design - fitting, given the game's theme!
 */
public class CreditEntry {

    private final String displayName;
    private final int maxHealth;
    private final FirePattern firePattern;
    private final boolean isPillarBoss;

    /**
     * @param displayName  the text shown on screen, e.g. "Encapsulation"
     * @param maxHealth    how many hits the entry can take before dying
     * @param firePattern  the Strategy object controlling how it shoots
     * @param isPillarBoss true for one of the four main OOP pillars
     *                     (Encapsulation, Abstraction, Inheritance,
     *                     Polymorphism) - these are the tougher mini-bosses
     */
    public CreditEntry(String displayName, int maxHealth, FirePattern firePattern, boolean isPillarBoss) {
        this.displayName = displayName;
        this.maxHealth = maxHealth;
        this.firePattern = firePattern;
        this.isPillarBoss = isPillarBoss;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public FirePattern getFirePattern() {
        return firePattern;
    }

    public boolean isPillarBoss() {
        return isPillarBoss;
    }
}
