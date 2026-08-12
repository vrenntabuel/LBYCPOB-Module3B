package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity;


import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventBus;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventType;

/**
 * AllyShipComponent.java
 * ======================
 * A friendly AI-controlled ship that assists the player (the "Cooperative
 * System" from the design brief). Unlike the player, an ally ship:
 *   - fires automatically, with no input required
 *   - has exactly one health point, so a single hit "sacrifices" it -
 *     this doubles as basic bullet interception, since any enemy bullet
 *     that hits the ally never reaches the player
 *   - does NOT respawn - once sacrificed, it is gone for the rest of the
 *     encounter, which is what AssistanceManager uses to decide when to
 *     grant a new one (see AssistanceManager.java)
 */
public class AllyShipComponent extends ShipComponent {

    private static final int ALLY_HEALTH = 1;
    private static final double FIRE_INTERVAL_SECONDS = 0.35;
    private static final double BULLET_SPEED = 560;

    private final BulletFactory bulletFactory;
    private double timeSinceLastShot = 0;

    public AllyShipComponent(BulletFactory bulletFactory) {
        super(ALLY_HEALTH, 380);
        this.bulletFactory = bulletFactory;
    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf); // keep invincibility-flicker behavior from the base class

        if (isDestroyed()) {
            return;
        }

        timeSinceLastShot += tpf;
        if (timeSinceLastShot >= FIRE_INTERVAL_SECONDS) {
            timeSinceLastShot = 0;
            double x = entity.getX() + entity.getWidth() / 2.0;
            double y = entity.getY();
            double straightUp = (3 * Math.PI) / 2;
            bulletFactory.spawnPlayerBullet(x, y, straightUp, BULLET_SPEED);
        }
    }

    @Override
    protected void onDamaged() {
        // Any hit is fatal for an ally ship (see class javadoc), so nothing
        // extra happens here - onDestroyed() below handles the reaction.
    }

    @Override
    protected void onDestroyed() {
        // "Sacrifice animation": briefly fade and shrink the ship before
        // removing it, giving the player visual feedback that the ally
        // took a hit meant for them.
        entity.setScaleX(0.4);
        entity.setScaleY(0.4);
        entity.setOpacity(0.0);

        GameEventBus.getInstance().publish(GameEventType.ALLY_SACRIFICED);
        entity.removeFromWorld();
    }
}
