package io.piotrjastrzebski.lrj2016.game;


/**
 * Created by PiotrJ on 10/04/16.
 */
public class Cannon {
	public static final int TYPE_PLAYER = 1;
	public static final int TYPE_PLAYER_BULLET = 2;
	public int type;
	public float cooldown;
	public float cooldownTimer;
	public int xOffset;
	public int yOffset;
}
