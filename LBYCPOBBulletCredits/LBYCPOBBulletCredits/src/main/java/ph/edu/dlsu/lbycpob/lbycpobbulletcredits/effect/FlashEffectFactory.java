package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.effect;

import com.almasb.fxgl.entity.Entity;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.EntityType;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * FlashEffectFactory.java
 * =======================
 * Spawns a horizontal "light bar" that sweeps back and forth across an
 * enemy word while it is "arming" (see EnemyCreditComponent), telegraphing
 * that it is about to open fire. Deliberately built from plain JavaFX
 * animation/paint classes (TranslateTransition + a translucent gradient
 * Rectangle + a Glow effect) rather than any external asset, so it works
 * with no extra image files.
 *
 * "The larger the text, the more spectacular the light" - callers pass in
 * whether this is a pillar boss, and this factory scales the bar's size,
 * glow intensity, and sweep speed up accordingly (see spawnHorizontalFlash).
 */
public final class FlashEffectFactory {

    /**
     * Every currently-running flash effect, tracked so stopAll() (see
     * below) can force-stop any that are still mid-sweep. Without this,
     * an enemy destroyed by GameOverState's full-world wipe while still
     * "arming" would leave its TranslateTransition looping INDEFINITELY
     * forever in the background - EnemyCreditComponent normally stops it
     * via FlashHandle.stopAndRemove(), but that wipe bypasses each
     * entity's own component logic entirely.
     */
    private static final Set<FlashHandle> ACTIVE_HANDLES =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    private FlashEffectFactory() {
        // static-only utility class - never instantiated
    }

    /**
     * A handle to a running flash effect so the caller can stop and clean
     * it up later (when the enemy actually starts firing, or is destroyed
     * mid-telegraph).
     */
    public static final class FlashHandle {
        private final Entity entity;
        private final Animation animation;

        private FlashHandle(Entity entity, Animation animation) {
            this.entity = entity;
            this.animation = animation;
        }

        /** Stops the sweep animation and removes the light bar from the world. */
        public void stopAndRemove() {
            animation.stop();
            if (entity.isActive()) {
                entity.removeFromWorld();
            }
            ACTIVE_HANDLES.remove(this);
        }
    }

    /**
     * Force-stops every currently-running flash effect, regardless of
     * which enemy it belonged to. Call this alongside any full-world wipe
     * (see GameOverState.retry() and BulletFactory.clearPools() for the
     * same pattern applied to pooled bullets).
     */
    public static void stopAll() {
        for (FlashHandle handle : Set.copyOf(ACTIVE_HANDLES)) {
            handle.stopAndRemove();
        }
    }

    /**
     * @param wordX       the enemy entity's x position
     * @param wordY       the enemy entity's y position
     * @param wordWidth   the enemy entity's on-screen width
     * @param wordHeight  the enemy entity's on-screen height
     * @param isPillarBoss whether this is one of the four OOP pillar bosses -
     *                     bigger words get a noticeably bigger, brighter,
     *                     faster-sweeping light
     */
    public static FlashHandle spawnHorizontalFlash(double wordX, double wordY,
                                                     double wordWidth, double wordHeight,
                                                     boolean isPillarBoss) {
        double barHeight = isPillarBoss ? Math.max(8, wordHeight * 0.22) : Math.max(3, wordHeight * 0.12);
        double barWidth = wordWidth * (isPillarBoss ? 0.6 : 0.4);
        double glowLevel = isPillarBoss ? 1.0 : 0.55;
        double sweepDurationMs = isPillarBoss ? 480 : 750;

        Color coreColor = isPillarBoss ? Color.rgb(255, 245, 180, 0.95) : Color.rgb(255, 255, 255, 0.75);

        // Transparent -> bright -> transparent gradient along the bar's own
        // width, so it reads as a soft beam of light rather than a hard-edged
        // rectangle sliding across the word.
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.5, coreColor),
                new Stop(1, Color.TRANSPARENT));

        Rectangle bar = new Rectangle(barWidth, barHeight, gradient);
        bar.setEffect(new Glow(glowLevel));

        // No .bbox(...) and no .collidable() - purely decorative, must
        // never participate in collision detection.
        Entity flashEntity = entityBuilder()
                .type(EntityType.BACKGROUND)
                .at(wordX, wordY + (wordHeight / 2.0) - (barHeight / 2.0))
                .view(bar)
                .buildAndAttach();

        // Sweep the bar from just left of the word to just past its right
        // edge, then loop indefinitely for as long as the caller keeps this
        // handle alive (i.e. for the whole "arming" duration).
        TranslateTransition sweep = new TranslateTransition(Duration.millis(sweepDurationMs), bar);
        sweep.setFromX(-barWidth);
        sweep.setToX(wordWidth);
        sweep.setCycleCount(Animation.INDEFINITE);
        sweep.play();

        FlashHandle handle = new FlashHandle(flashEntity, sweep);
        ACTIVE_HANDLES.add(handle);
        return handle;
    }
}
