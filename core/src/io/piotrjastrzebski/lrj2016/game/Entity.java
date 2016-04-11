package io.piotrjastrzebski.lrj2016.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by PiotrJ on 10/04/16.
 */
public class Entity {
	private static Pool<Entity> pool = new Pool<Entity>() {
		@Override protected Entity newObject () {
			return new Entity();
		}
	};

	public enum Facing {NORTH, EAST, SOUTH, WEST}

	public static final int TYPE_PLAYER = 1;
	public static final int TYPE_PLAYER_BULLET = 2;
	public static final int TYPE_ENEMY = 3;
	public static final int TYPE_ENEMY_BULLET = 4;
	public int type;
	public float health;
	public Rectangle b = new Rectangle();
	public float speed;
	public float vx;
	public float vy;
	public Facing facing = Facing.NORTH;
	public Asset asset;
	public Array<Cannon> cannons = new Array<>();
	public float shootCooldown;
	public float shootTimer;
	public float dmgOnHit;
	public float spawnTimer;
	public boolean delete;

	public static Entity get () {
		return pool.obtain();
	}
	public static void free (Entity entity) {
		pool.free(entity);
		entity.type = 0;
		entity.health = 0;
		entity.b.set(0, 0, 0, 0);
		entity.speed = 0;
		entity.vy = 0;
		entity.vy = 0;
		entity.facing = Facing.NORTH;
		Asset.free(entity.asset);
		entity.asset = null;
		for (Cannon cannon : entity.cannons) {
			Cannon.free(cannon);
		}
		entity.cannons.clear();
		entity.shootCooldown = 0;
		entity.shootTimer = 0;
		entity.dmgOnHit = 0;
		entity.spawnTimer = 0;
		entity.delete = false;
	}
}
