package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.command;


import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.PlayerComponent;

/**
 * ShootCommand.java
 * =================
 * Concrete Command that tells the player's ship to fire. Held down (or, in
 * our default binding, always active) to satisfy the "continuous /
 * rapid firing" requirement without the player needing to mash a button.
 */
public class ShootCommand implements InputCommand {

    private final PlayerComponent player;

    public ShootCommand(PlayerComponent player) {
        this.player = player;
    }

    @Override
    public void execute(double tpf) {
        player.shoot(tpf);
    }
}
