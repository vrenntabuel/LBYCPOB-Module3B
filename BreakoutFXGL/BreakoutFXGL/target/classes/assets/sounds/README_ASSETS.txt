Place your audio files in this folder using these exact file names:

  background_music.wav   - looping background music
  ball_bounce.wav         - short sound effect for wall/paddle/brick bounces
  brick_break.wav         - short sound effect for a brick being destroyed

If a file is missing, unreadable, or this whole folder is empty, the
game does NOT crash - AudioManager (see the audio package) simply
treats that sound as unavailable and the matching play...() method
quietly does nothing. This is the same "robust fallback" behavior used
for the brick images: the game is always playable, with or without
audio assets.

Any format supported by javafx.scene.media (WAV, MP3, AIFF) will work;
.wav is used above only as the expected default extension. If you use
a different format, update the file names in
AudioManager.BACKGROUND_MUSIC_PATH / BALL_BOUNCE_PATH / BRICK_BREAK_PATH
to match.

Background music loops automatically and keeps playing across restarts
(pressing R). Press M in-game to toggle mute for both music and sound
effects.
