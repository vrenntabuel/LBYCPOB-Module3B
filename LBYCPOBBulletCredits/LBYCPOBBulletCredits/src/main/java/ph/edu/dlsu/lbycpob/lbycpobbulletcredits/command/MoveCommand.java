package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.command;


import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.PlayerComponent;

/**
 * MoveCommand.java
 * ================
 * A concrete Command that tells the player's ship to move in a specific
 * direction. FXGL's input system holds movement keys "down" for as long
 * as the player holds the physical key, calling execute() every frame
 * while that is true - which is exactly the "continuous movement"
 * behavior the design brief asked for.
 */
public class MoveCommand implements InputCommand {

    private final PlayerComponent player;
    private final double dx;
    private final double dy;

    /**
     * @param dx -1 (left), 0 (none), or 1 (right)
     * @param dy -1 (up),   0 (none), or 1 (down)
     */
    public MoveCommand(PlayerComponent player, double dx, double dy) {
        this.player = player;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute(double tpf) {
        player.move(dx, dy, tpf);
    }
}
