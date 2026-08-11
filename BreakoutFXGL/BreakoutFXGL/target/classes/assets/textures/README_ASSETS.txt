Place your brick PNG images in this folder using these exact file names:

  brick_normal.png            - NORMAL brick, full health (only state; breaks in one hit)
  brick_strong.png            - STRONG brick, full health (2 hit points)
  brick_strong_cracked.png    - STRONG brick, after its first hit (damaged, one hit left)
  brick_unbreakable.png       - UNBREAKABLE brick (single, unchanging appearance)

If a file is missing, unreadable, or this whole folder is empty, the
game does NOT crash - BrickTextureLoader (see the view package) simply
returns null for that image, and BrickViewFactory falls back to a
plain colored rectangle for that brick type instead. This is the
"robust fallback" behavior requested for the project: the game is
always playable, with or without art assets.

Recommended size: any square-ish PNG works, since each image is
stretched to fit the brick's actual width/height (80x24 by default,
see GameManager.buildLevel()).
