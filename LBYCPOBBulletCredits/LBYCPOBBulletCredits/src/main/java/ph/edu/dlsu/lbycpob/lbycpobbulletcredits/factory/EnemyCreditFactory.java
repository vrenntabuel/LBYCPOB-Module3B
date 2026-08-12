package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.data.CreditEntry;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.EnemyCreditComponent;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.EntityType;


import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;

/**
 * EnemyCreditFactory.java
 * =======================
 * FACTORY DESIGN PATTERN implementation for enemy "OOP concept" entities.
 * Turns a data-only CreditEntry into a fully-formed, visible, damageable
 * FXGL entity - rendering the concept's name as large on-screen text so it
 * reads exactly like a scrolling credits sequence, per the design brief.
 */
public class EnemyCreditFactory {

    /**
     * JavaFX's Text.getLayoutBounds() measures the font's full line height
     * (ascent + descent + internal leading), which is noticeably taller
     * than the actual visible letters - especially for words with no
     * descenders (like "Class" or "Interface"). Left uncorrected, that
     * makes the hitbox extend well above and below the glyphs, so a
     * bullet can visually appear to sail through empty space just above
     * or below a word and still "hit" it. These insets trim the hitbox
     * down toward the visible ink. Vertical gets trimmed more aggressively
     * than horizontal because line-height padding is mostly a vertical
     * phenomenon; a little slack is still kept on both axes so letters
     * with descenders (e.g. the 'p' in "Encapsulation", the 'y' in
     * "Polymorphism") are not clipped.
     *
     * Pillar bosses use noticeably larger insets than regular words: a
     * bold, larger font carries proportionally MORE internal line-height
     * padding than a regular-weight smaller one, so the same fraction that
     * looks tight on regular text still leaves a visibly loose hitbox
     * around a pillar boss's name if left uncorrected.
     */
    private static final double VERTICAL_INSET_FRACTION_REGULAR = 0.12;
    private static final double HORIZONTAL_INSET_FRACTION_REGULAR = 0.045;
    private static final double VERTICAL_INSET_FRACTION_PILLAR = 0.20;
    private static final double HORIZONTAL_INSET_FRACTION_PILLAR = 0.08;

    private static final double REGULAR_FONT_SIZE = 26;
    private static final double PILLAR_FONT_SIZE = 48;

    private final BulletFactory bulletFactory;

    public EnemyCreditFactory(BulletFactory bulletFactory) {
        this.bulletFactory = bulletFactory;
    }

    /**
     * @param data            the concept's name/health/fire-pattern data
     * @param spawnXFraction  0.0 (left edge) to 1.0 (right edge) - lets
     *                        WaveManager spread several enemies horizontally
     * @param difficultyScale multiplier passed through to the FirePattern
     *                        strategy so later waves fire faster/denser
     */
    public Entity spawnEnemyCredit(CreditEntry data, double spawnXFraction, double difficultyScale) {
        // Pillar bosses (Encapsulation, Abstraction, Inheritance,
        // Polymorphism) are rendered larger and in a different color so
        // they immediately read as more dangerous than regular sub-concepts.
        boolean isPillar = data.isPillarBoss();

        Text label = new Text(data.getDisplayName());
        label.setFont(Font.font("Consolas", isPillar ? FontWeight.BOLD : FontWeight.NORMAL,
                isPillar ? PILLAR_FONT_SIZE : REGULAR_FONT_SIZE));
        label.setFill(isPillar ? Color.GOLD : Color.WHITESMOKE);
        label.setEffect(new DropShadow(isPillar ? 12 : 6, isPillar ? Color.DARKRED : Color.BLACK));

        double textWidth = label.getLayoutBounds().getWidth();
        double textHeight = label.getLayoutBounds().getHeight();

        double spawnX = (getAppWidth() - textWidth) * spawnXFraction;
        double spawnY = -textHeight; // start just above the visible screen

        // Shrink the hitbox in toward the visible letters (see the inset
        // fraction javadoc above) and offset it by the inset amount so it
        // stays centered within the entity's full view size.
        double verticalInsetFraction = isPillar ? VERTICAL_INSET_FRACTION_PILLAR : VERTICAL_INSET_FRACTION_REGULAR;
        double horizontalInsetFraction = isPillar ? HORIZONTAL_INSET_FRACTION_PILLAR : HORIZONTAL_INSET_FRACTION_REGULAR;

        double verticalInset = textHeight * verticalInsetFraction;
        double horizontalInset = textWidth * horizontalInsetFraction;
        double hitboxWidth = textWidth - (2 * horizontalInset);
        double hitboxHeight = textHeight - (2 * verticalInset);

        return entityBuilder()
                .type(EntityType.ENEMY_CREDIT)
                .at(spawnX, spawnY)
                .bbox(new HitBox(new Point2D(horizontalInset, verticalInset),
                        BoundingShape.box(hitboxWidth, hitboxHeight)))
                .collidable()
                .view(label)
                .with(new EnemyCreditComponent(data, bulletFactory, difficultyScale))
                .buildAndAttach();
    }
}
