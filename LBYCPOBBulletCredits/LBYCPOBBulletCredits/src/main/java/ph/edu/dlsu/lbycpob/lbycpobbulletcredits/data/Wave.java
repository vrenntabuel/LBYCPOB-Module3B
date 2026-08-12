package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.data;

import java.util.List;

/**
 * Wave.java
 * =========
 * A Wave groups several CreditEntry enemies that appear together before a
 * "pillar boss" (Encapsulation, Abstraction, Inheritance, Polymorphism)
 * closes out that stage. The WaveManager walks through a List<Wave> in
 * order, which is how the game progresses from easy to "intentionally
 * overwhelming", as the design brief requested.
 */
public class Wave {

    private final String title;
    private final List<CreditEntry> entries;
    private final double difficultyMultiplier;

    /**
     * @param title                 label shown briefly before the wave starts,
     *                              e.g. "Realm of Abstraction"
     * @param entries               the enemies belonging to this wave, in the
     *                              order they should spawn
     * @param difficultyMultiplier  scales bullet speed / fire rate for every
     *                              enemy in this wave - later waves use
     *                              higher multipliers
     */
    public Wave(String title, List<CreditEntry> entries, double difficultyMultiplier) {
        this.title = title;
        this.entries = entries;
        this.difficultyMultiplier = difficultyMultiplier;
    }

    public String getTitle() {
        return title;
    }

    public List<CreditEntry> getEntries() {
        return entries;
    }

    public double getDifficultyMultiplier() {
        return difficultyMultiplier;
    }
}
