package ph.edu.dlsu.lbycpob.lbycpobbulletcredits;

import com.almasb.fxgl.entity.Entity;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.audio.AudioManager;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.PlayerComponent;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.AllyShipFactory;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.EnemyCreditFactory;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.PlayerFactory;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.manager.AssistanceManager;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.manager.CollisionManager;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.manager.SaveManager;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.manager.WaveManager;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.state.GameStateManager;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.ui.HUD;


/**
 * GameContext.java
 * ================
 * A small dependency-holder that bundles together every manager/factory a
 * GameState implementation might need. Rather than giving TitleState,
 * PlayingState, PausedState, GameOverState, and VictoryState each a
 * five-plus-parameter constructor, they all just receive one GameContext.
 *
 * NOTE:
 * This is sometimes informally called a "service locator" or "context
 * object". It is a pragmatic middle ground between passing a huge list of
 * parameters everywhere and using global singletons for everything.
 */
public class GameContext {

    public final GameStateManager gameStateManager = new GameStateManager();
    public final BulletFactory bulletFactory = new BulletFactory();
    public final PlayerFactory playerFactory = new PlayerFactory(bulletFactory);
    public final EnemyCreditFactory enemyCreditFactory = new EnemyCreditFactory(bulletFactory);
    public final AllyShipFactory allyShipFactory = new AllyShipFactory(bulletFactory);
    public final CollisionManager collisionManager = new CollisionManager(bulletFactory);
    public final WaveManager waveManager = new WaveManager(enemyCreditFactory);
    public final AssistanceManager assistanceManager = new AssistanceManager(allyShipFactory);
    public final SaveManager saveManager = new SaveManager();
    public final HUD hud = new HUD();
    public final AudioManager audioManager = AudioManager.getInstance();

    /** Set once the player entity has been spawned by PlayingState. */
    public Entity playerEntity;

    public PlayerComponent getPlayer() {
        return playerEntity.getComponent(PlayerComponent.class);
    }
}
