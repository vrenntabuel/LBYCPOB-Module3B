package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.command;

/**
 * InputCommand.java
 * =================
 * Implements the COMMAND DESIGN PATTERN for player input.
 *
 * NOTE:
 * Instead of writing input-handling code that directly reaches into the
 * Player object from wherever a key press is detected, we wrap each
 * possible action ("move up-left", "shoot") in its own small object that
 * knows only how to execute() itself. Benefits this unlocks later without
 * changing Player at all:
 *   - Rebindable controls (just point a different key at the same Command)
 *   - Replay systems / input logging (record which Commands fired, when)
 *   - AI-controlled ally ships reusing the very same ShootCommand a human
 *     player uses
 */
public interface InputCommand {
    void execute(double tpf);
}
