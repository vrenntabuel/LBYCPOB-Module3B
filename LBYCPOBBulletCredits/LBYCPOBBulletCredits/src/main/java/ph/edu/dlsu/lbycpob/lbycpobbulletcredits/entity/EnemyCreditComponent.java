package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity;

import com.almasb.fxgl.entity.component.Component;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet.FirePattern;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.data.CreditEntry;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.effect.FlashEffectFactory;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.effect.SmokeEffectFactory;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.manager.DifficultyManager;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventBus;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventType;


/**
 * EnemyCreditComponent.java
 * =========================
 * The on-screen behavior for one "developer credit" enemy - except here
 * the credits are OOP concepts (Encapsulation, Abstraction, Inheritance,
 * Polymorphism, and their supporting sub-concepts). Each instance owns a
 * FirePattern STRATEGY object (see bullet/FirePattern.java) describing how
 * it shoots, so this class stays simple: track health, drift onto screen,
 * "arm" itself with a smoke-and-light telegraph, delegate shooting to the
 * strategy, and announce destruction.
 */
public class EnemyCreditComponent extends Component implements Damageable {

    private static final double ENTRY_SPEED = 60; // pixels/sec while sliding onto screen
    private static final double STOP_Y = 140;      // y-position where it settles

    /** How long an enemy "smokes"/flashes after settling before it opens
     *  fire - pillar bosses get a longer, more dramatic telegraph. */
    private static final double ARMING_DURATION_REGULAR = 0.8;
    private static final double ARMING_DURATION_PILLAR = 1.6;
    private static final double SMOKE_PUFF_INTERVAL = 0.16;

    private final CreditEntry data;
    private final BulletFactory bulletFactory;

    /** The difficulty multiplier this enemy's Wave was configured with at
     *  spawn time (see WaveManager.buildWaves()). Combined every frame with
     *  DifficultyManager's time-based multiplier below, so difficulty keeps
     *  climbing for as long as this enemy stays alive - not just once, at
     *  the moment it spawned. */
    private final double baseDifficultyScale;

    private int currentHealth;
    private boolean hasSettled = false;

    /** Counts down once the enemy has settled into position; while this is
     *  above zero the enemy is "arming" (smoke + flashing light) but not
     *  yet shooting. */
    private double armingTimer;
    private double smokePuffTimer = 0;

    /** The sweeping light-bar effect started the moment this enemy settles;
     *  stopped and cleared the moment it starts firing (or is destroyed
     *  mid-telegraph) - see FlashEffectFactory. */
    private FlashEffectFactory.FlashHandle flashHandle;

    public EnemyCreditComponent(CreditEntry data, BulletFactory bulletFactory, double baseDifficultyScale) {
        this.data = data;
        this.bulletFactory = bulletFactory;
        this.baseDifficultyScale = baseDifficultyScale;
        this.currentHealth = data.getMaxHealth();
        this.armingTimer = data.isPillarBoss() ? ARMING_DURATION_PILLAR : ARMING_DURATION_REGULAR;
    }

    @Override
    public void onUpdate(double tpf) {
        if (isDestroyed()) {
            return;
        }

        if (!hasSettled) {
            // Slide down from off-screen until reaching its resting height,
            // similar to a name scrolling up in end credits - except this
            // one scrolls DOWN into the player's territory before it opens fire.
            entity.translateY(ENTRY_SPEED * tpf);
            if (entity.getY() >= STOP_Y) {
                entity.setY(STOP_Y);
                hasSettled = true;

                // The instant it settles, start both telegraph effects:
                // a steady trickle of smoke (see below) and a sweeping
                // horizontal light bar sized/brightened according to how
                // big this word is - see FlashEffectFactory's javadoc for
                // "the larger the text, the more spectacular the light".
                flashHandle = FlashEffectFactory.spawnHorizontalFlash(
                        entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight(), data.isPillarBoss());
            }
            return;
        }

        if (armingTimer > 0) {
            // "Arming" phase: the enemy has arrived but is not shooting
            // yet. A steady trickle of smoke puffs plus the sweeping light
            // bar started above telegraph to the player that it is about
            // to start firing, giving them a fair warning window rather
            // than an instant ambush the moment it stops moving.
            armingTimer -= tpf;
            smokePuffTimer -= tpf;

            if (smokePuffTimer <= 0) {
                smokePuffTimer = SMOKE_PUFF_INTERVAL;
                double smokeX = entity.getX() + entity.getWidth() / 2.0;
                double smokeY = entity.getY() + entity.getHeight() / 2.0;
                SmokeEffectFactory.spawnPuff(smokeX, smokeY, entity.getWidth() * 0.7);
            }
            return;
        }

        if (flashHandle != null) {
            // Arming just finished this frame - stop the light bar right
            // as firing begins. Guarded by the null check so this only
            // ever runs once per enemy.
            flashHandle.stopAndRemove();
            flashHandle = null;
        }

        FirePattern firePattern = data.getFirePattern();
        double originX = entity.getX() + entity.getWidth() / 2.0;
        double originY = entity.getY() + entity.getHeight();

        // Combine this enemy's wave-based difficulty with the run's
        // time-based difficulty so bullets keep getting faster/denser the
        // longer the fight (or the whole run) drags on.
        double effectiveDifficulty = baseDifficultyScale * DifficultyManager.getInstance().getTimeMultiplier();

        firePattern.update(entity, originX, originY, bulletFactory, tpf, effectiveDifficulty);
    }

    @Override
    public void takeDamage(int amount) {
        if (isDestroyed()) {
            return;
        }
        currentHealth = Math.max(0, currentHealth - amount);

        boolean justDestroyed = isDestroyed();

        if (!justDestroyed) {
            // Only fire the "still alive, just got hit" events when this
            // hit did NOT finish the enemy off - otherwise the damage
            // sound and the destruction/explosion sound would both fire
            // on the exact same frame and overlap unpleasantly.
            //
            // Exactly ONE of these two fires per hit (never both): pillar
            // bosses get their own distinct, more dramatic sound via
            // PILLAR_BOSS_DAMAGED (see AudioManager's SFX_BOSS_HIT),
            // while every other OOP concept word uses the regular
            // ENEMY_DAMAGED event (SFX_WORD_HIT) - keeping the two sounds
            // from ever overlapping on the same hit.
            if (data.isPillarBoss()) {
                GameEventBus.getInstance().publish(GameEventType.PILLAR_BOSS_DAMAGED, data.getDisplayName());
            } else {
                GameEventBus.getInstance().publish(GameEventType.ENEMY_DAMAGED, data.getDisplayName());
            }
        } else {
            onDestroyed();
        }
    }

    private void onDestroyed() {
        // Clean up an in-progress arming telegraph if the enemy is
        // destroyed before it ever got to fire - otherwise the sweeping
        // light bar would be left animating over an empty spot forever.
        if (flashHandle != null) {
            flashHandle.stopAndRemove();
            flashHandle = null;
        }

        // Destruction animation: a quick fade/scale-up before removal
        // gives visual feedback without needing an external sprite sheet.
        entity.setScaleX(1.5);
        entity.setScaleY(1.5);
        entity.setOpacity(0.0);

        GameEventType event = data.isPillarBoss()
                ? GameEventType.PILLAR_BOSS_DESTROYED
                : GameEventType.ENEMY_DESTROYED;
        GameEventBus.getInstance().publish(event, data.getDisplayName());

        entity.removeFromWorld();
    }

    @Override
    public boolean isDestroyed() {
        return currentHealth <= 0;
    }

    @Override
    public int getCurrentHealth() {
        return currentHealth;
    }

    @Override
    public int getMaxHealth() {
        return data.getMaxHealth();
    }

    public String getDisplayName() {
        return data.getDisplayName();
    }

    public boolean isPillarBoss() {
        return data.isPillarBoss();
    }
}
