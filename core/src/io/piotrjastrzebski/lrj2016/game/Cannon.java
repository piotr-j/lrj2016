package io.piotrjastrzebski.lrj2016.game;

import com.badlogic.gdx.utils.Pool;

/**
 * Created by PiotrJ on 10/04/16.
 */
public class Cannon {
	private static Pool<Cannon> pool = new Pool<Cannon>() {
		@Override protected Cannon newObject () {
			return new Cannon();
		}
	};
	public static final int TYPE_PLAYER = 1;
	public static final int TYPE_PLAYER_BULLET = 2;
	public int type;
	public float cooldown;
	public float cooldownTimer;
	public int xOffset;
	public int yOffset;


	public static Cannon get () {
		return pool.obtain();
	}
	public static void free (Cannon cannon) {
		pool.free(cannon);
		cannon.type = 0;
		cannon.cooldown = 0;
		cannon.cooldownTimer = 0;
		cannon.xOffset = 0;
		cannon.yOffset = 0;
	}
}
