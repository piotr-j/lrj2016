package io.piotrjastrzebski.lrj2016;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.piotrjastrzebski.lrj2016.game.Cannon;
import io.piotrjastrzebski.lrj2016.game.Entity;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line;

/**
 * Created by PiotrJ on 07/04/16.
 */
public class GameScreen extends BaseScreen {
	private OrthographicCamera camera;
	private FitViewport viewport;
	private TextureRegion ship;

	Array<Entity> entities = new Array<Entity>();

	public GameScreen (LRJGame game) {
		super(game);
		camera = new OrthographicCamera();
		viewport = new FitViewport(64, 64, camera);
		ship = assets.ship;

		Entity player = new Entity();
		player.type = Entity.TYPE_PLAYER;
		player.b.set(0, 0, ship.getRegionWidth(), ship.getRegionHeight());
		player.asset = ship;
		player.health = 5;
		player.speed = 25;
		player.shootCooldown = .25f;
		player.cannons = new Array<Cannon>();
		Cannon cannon = new Cannon();
		cannon.cooldown = player.shootCooldown * 4;
		cannon.xOffset = 2;
		cannon.yOffset = 6;
		player.cannons.add(cannon);
		cannon = new Cannon();
		cannon.cooldown = player.shootCooldown * 4;
		cannon.xOffset = 3;
		cannon.yOffset = 8;
		player.cannons.add(cannon);
		cannon = new Cannon();
		cannon.cooldown = player.shootCooldown * 4;
		cannon.xOffset = 7;
		cannon.yOffset = 8;
		player.cannons.add(cannon);
		cannon = new Cannon();
		cannon.cooldown = player.shootCooldown * 4;
		cannon.xOffset = 8;
		cannon.yOffset = 6;
		player.cannons.add(cannon);
		entities.add(player);

		int x = -32;
		for (Entity.Facing facing : Entity.Facing.values()) {
			Entity enemy = new Entity();
			enemy.type = Entity.TYPE_ENEMY;
			enemy.b.set(x, 16, ship.getRegionWidth(), ship.getRegionHeight());
			enemy.asset = ship;
			enemy.health = 5;
			enemy.facing = facing;
			entities.add(enemy);
			x += 16;
		}

	}

	boolean drawGrid = true;

	Rectangle vb = new Rectangle();
	Vector3 debugPos = new Vector3();
	Vector2 tmp = new Vector2();
	@Override public void render (float delta) {

		for (int i = entities.size -1; i >= 0; i--) {
			Entity entity = entities.get(i);
			switch (entity.type) {
			case Entity.TYPE_PLAYER: {
				// movement
				tmp.setZero();
				if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
					tmp.x = -entity.speed;
				} else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
					tmp.x = entity.speed;
				}

				if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
					tmp.y = entity.speed;
				} else if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
					tmp.y = -entity.speed;
				}
				tmp.limit2(entity.speed * entity.speed);
				entity.b.x += tmp.x * delta;
				entity.b.y += tmp.y * delta;


				vb.set(
					camera.position.x - camera.viewportWidth /2,
					camera.position.y - camera.viewportHeight /2,
					camera.viewportWidth, camera.viewportHeight - 5);
				// shoot
				entity.shootTimer -= delta;
				for (Cannon cannon : entity.cannons) {
					cannon.cooldownTimer -= delta;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
					if (entity.shootTimer <= 0) {
						for (Cannon cannon : entity.cannons) {
							if (cannon.cooldownTimer <= 0) {
								entity.shootTimer = entity.shootCooldown;
								cannon.cooldownTimer = cannon.cooldown;
								Entity bullet = Entity.get();
								bullet.type = Entity.TYPE_PLAYER_BULLET;
								bullet.health = 2;
								bullet.asset = assets.bullet;
								bullet.b.x = entity.b.x + cannon.xOffset;
								bullet.b.y = entity.b.y + cannon.yOffset;
								bullet.b.width = bullet.asset.getRegionWidth();
								bullet.b.height = bullet.asset.getRegionHeight();
								bullet.vy = 25f;
								entities.add(bullet);
								break;
							}
						}
					}
				}
//				debugPos.set(entity.b.x, entity.b.y, 0);
//				camera.position.set((int)entity.b.x, (int)entity.b.y, 0);
//				camera.update();
			} break;
			case Entity.TYPE_PLAYER_BULLET: {
				entity.b.x += entity.vx * delta;
				entity.b.y += entity.vy * delta;
				if (!vb.overlaps(entity.b) || entity.delete) {
					entities.removeIndex(i);
					Entity.free(entity);
				}
			} break;
			case Entity.TYPE_ENEMY: {
				for (int j = 0; j < entities.size; j++) {
					Entity other = entities.get(j);
					if (other.type == Entity.TYPE_PLAYER_BULLET) {
						// TODO we probably want a proper hitbox
						if (!other.delete && other.b.overlaps(entity.b)) {
							entity.health -= other.health;
							other.delete = true;
						}
					}
				}
				if (entity.health <= 0) {
					entities.removeIndex(i);
					Entity.free(entity);
				}
//				if (!vb.overlaps(entity.b)) {
//					entities.removeIndex(i);
//					Entity.free(entity);
//				}
			} break;
			}
		}


		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glDisable(GL20.GL_BLEND);
		// draw bg
		renderer.setProjectionMatrix(camera.combined);
		renderer.setColor(Color.DARK_GRAY);
		renderer.begin(Filled);
		renderer.rect(camera.position.x - 32, camera.position.y - 32, 64, 64);
		renderer.end();
		// draw pixel grid
		if (drawGrid) {
			renderer.setColor(Color.GRAY);
			renderer.begin(Line);
			float cx = camera.position.x - camera.viewportWidth / 2;
			float cy = camera.position.y - camera.viewportHeight / 2;
			int width = (int)(camera.viewportWidth) + 2;
			int height = (int)(camera.viewportHeight) + 2;
			for (int x = -1; x <= width; x++) {
				renderer.line(cx + x, cy - 1, cx + x, cy + height);
			}
			for (int y = -1; y <= height; y++) {
				renderer.line(cx - 1, cy + y, cx + width, cy + y);
			}
			renderer.end();
		}
		// draw game
		batch.enableBlending();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for (Entity entity : entities) {
			switch (entity.type) {
			case Entity.TYPE_PLAYER: {
				batch.draw(entity.asset, (int)entity.b.x, (int)entity.b.y, entity.b.width, entity.b.height);
			} break;
			case Entity.TYPE_PLAYER_BULLET: {
				batch.draw(entity.asset, (int)entity.b.x, (int)entity.b.y, entity.b.width, entity.b.height);
			} break;
			case Entity.TYPE_ENEMY: {
				switch (entity.facing) {
				case NORTH:
					batch.draw(entity.asset,
						(int)entity.b.x, (int)entity.b.y,
						entity.b.width/2, entity.b.height/2,
						entity.b.width, entity.b.height,
						1, 1, 0);
					break;
				case EAST:
					batch.draw(entity.asset,
						(int)entity.b.x, (int)entity.b.y,
						entity.b.width/2, entity.b.height/2,
						entity.b.width, entity.b.height,
						1, 1, 90);
					break;
				case SOUTH:
					batch.draw(entity.asset,
						(int)entity.b.x, (int)entity.b.y,
						entity.b.width/2, entity.b.height/2,
						entity.b.width, entity.b.height,
						1, 1, 180);
					break;
				case WEST:
					batch.draw(entity.asset,
						(int)entity.b.x, (int)entity.b.y,
						entity.b.width/2, entity.b.height/2,
						entity.b.width, entity.b.height,
						1, 1, 270);
					break;
				}
			} break;
			case Entity.TYPE_ENEMY_BULLEt: {
				batch.draw(entity.asset, (int)entity.b.x, (int)entity.b.y, entity.b.width, entity.b.height);
			} break;
			}
		}

		batch.end();

		// draw some debug stuff
		camera.position.set(debugPos.x, debugPos.y, 0);
		camera.update();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(Line);
		renderer.setColor(Color.CYAN);
		renderer.circle(debugPos.x, debugPos.y, 0.25f, 16);
		renderer.setColor(Color.MAGENTA);
		renderer.circle((int)debugPos.x, (int)debugPos.y, 0.25f, 16);
		renderer.setColor(Color.VIOLET);
		renderer.rect(vb.x + .5f, vb.y + .5f, vb.width -1f, vb.height -1f);
		renderer.end();
	}

	@Override public void resize (int width, int height) {
		viewport.update(width, height, false);
	}

	@Override public void dispose () {

	}
}
