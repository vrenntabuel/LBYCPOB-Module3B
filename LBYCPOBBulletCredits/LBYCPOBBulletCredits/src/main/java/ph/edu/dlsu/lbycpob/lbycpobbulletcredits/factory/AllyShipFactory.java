package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.AllyShipComponent;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.EntityType;


import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * AllyShipFactory.java
 * ====================
 * FACTORY DESIGN PATTERN implementation for the cooperative AI ally ships.
 * Ally ships are drawn as smaller triangles than the player's, in a
 * different color, so they are easy to visually distinguish at a glance
 * during frantic bullet-hell moments.
 */
public class AllyShipFactory {

    private final BulletFactory bulletFactory;

    public AllyShipFactory(BulletFactory bulletFactory) {
        this.bulletFactory = bulletFactory;
    }

    public Entity spawnAllyShip(double x, double y) {
        Polygon triangleShape = new Polygon(
                10, 0,   // nose (tip)
                0, 20,   // rear-left
                20, 20   // rear-right
        );
        triangleShape.setFill(Color.LIGHTGREEN);

        return entityBuilder()
                .type(EntityType.ALLY_SHIP)
                .at(x, y)
                .bbox(new HitBox(BoundingShape.box(20, 20)))
                .collidable()
                .view(triangleShape)
                .with(new AllyShipComponent(bulletFactory))
                .buildAndAttach();
    }
}
