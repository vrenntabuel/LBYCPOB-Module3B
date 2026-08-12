package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer;

/**
 * GameEventType.java
 * ==================
 * Every kind of noteworthy event that HUD, AudioManager, or other
 * interested classes might want to react to.
 */
public enum GameEventType {
    ENEMY_DAMAGED,
    ENEMY_DESTROYED,
    PILLAR_BOSS_DAMAGED,
    PILLAR_BOSS_DESTROYED,
    PLAYER_DAMAGED,
    PLAYER_LIFE_LOST,
    PLAYER_DIED,
    ALLY_SACRIFICED,
    WAVE_STARTED,
    WAVE_COMPLETE,
    GAME_VICTORY,
    NARRATIVE_LINE
}
