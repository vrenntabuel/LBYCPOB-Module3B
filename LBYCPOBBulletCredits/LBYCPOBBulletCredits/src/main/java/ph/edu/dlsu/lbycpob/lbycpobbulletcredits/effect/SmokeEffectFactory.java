package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.effect;

import com.almasb.fxgl.entity.Entity;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.EntityType;


import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * SmokeEffectFactory.java
 * =======================
 * Spawns small, short-lived, non-collidable "smoke puff" entities used to
 * telegraph that an OOP-concept enemy is about to start firing (see
 * EnemyCreditComponent's "arming" phase). This is deliberately built from
 * plain JavaFX animation classes (FadeTransition, TranslateTransition,
 * ScaleTransition) rather than a sprite sheet or particle-system asset,
 * so it works out of the box with no extra image files required.
 *
 * BEGINNER NOTE:
 * Each puff is its own tiny FXGL entity with EntityType.BACKGROUND (so it
 * is never accidentally targeted by any collision handler) and no bbox at
 * all (so FXGL never even considers it for collision checks in the first
 * place). It animates itself and removes itself from the world the moment
 * its animation finishes, via the transition's onFinished callback - no
 * external code needs to track or clean these up.
 */
public final class SmokeEffectFactory {

    private SmokeEffectFactory() {
        // static-only utility class - never instantiated
    }

    /**
     * Spawns one small puff of smoke near the given point. Call this
     * repeatedly (e.g. every ~0.15-0.2 seconds) while an enemy is
     * "arming" for a continuous smoking effect rather than one single burst.
     *
     * @param centerX      roughly where the enemy is
     * @param centerY      roughly where the enemy is
     * @param spreadWidth  how widely puffs should be scattered horizontally
     *                     (pass the enemy's on-screen text width for a nice
     *                     effect that hugs the word)
     */
    public static void spawnPuff(double centerX, double centerY, double spreadWidth) {
        double offsetX = (Math.random() - 0.5) * spreadWidth;
        double offsetY = (Math.random() - 0.5) * 16;
        double startX = centerX + offsetX;
        double startY = centerY + offsetY;

        double radius = 5 + Math.random() * 6;
        Circle puff = new Circle(radius, Color.rgb(150, 150, 150, 0.55));

        // No .bbox(...) and no .collidable() - this entity is purely
        // decorative and must never participate in collision detection.
        Entity smokeEntity = entityBuilder()
                .type(EntityType.BACKGROUND)
                .at(startX, startY)
                .view(puff)
                .buildAndAttach();

        double driftX = (Math.random() - 0.5) * 26;
        double driftY = -18 - Math.random() * 22;
        double durationMs = 650 + Math.random() * 350;

        TranslateTransition drift = new TranslateTransition(Duration.millis(durationMs), puff);
        drift.setByX(driftX);
        drift.setByY(driftY);

        FadeTransition fade = new FadeTransition(Duration.millis(durationMs), puff);
        fade.setFromValue(0.7);
        fade.setToValue(0.0);

        ScaleTransition grow = new ScaleTransition(Duration.millis(durationMs), puff);
        grow.setFromX(0.6);
        grow.setFromY(0.6);
        grow.setToX(1.5);
        grow.setToY(1.5);

        ParallelTransition combined = new ParallelTransition(drift, fade, grow);
        combined.setOnFinished(event -> {
            // Guard against double-removal: if something else (like
            // GameOverState.retry(), which wipes every entity in the
            // world) already removed this puff while its animation was
            // still playing, calling removeFromWorld() again would be
            // invalid - only do it if the entity is still actually there.
            if (smokeEntity.isActive()) {
                smokeEntity.removeFromWorld();
            }
        });
        combined.play();
    }
}
