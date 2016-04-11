package io.piotrjastrzebski.lrj2016;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.piotrjastrzebski.lrj2016.game.Asset;
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

		createPlayer();

		int x = -32;
		for (Entity.Facing facing : Entity.Facing.values()) {
			createEnemy(x, 16, facing);
			x += 16;
		}
	}

	protected void createPlayer () {
		Entity player = new Entity();
		player.type = Entity.TYPE_PLAYER;
		player.b.set(-3, -24, 7, 7);
		player.asset = Asset.get();
		player.asset.region = ship;
		player.asset.xOffset = -3;
		player.asset.yOffset = -3;
		player.asset.width = ship.getRegionWidth();
		player.asset.height = ship.getRegionHeight();
		player.health = 4.5f;
		player.speed = 25;
		player.shootCooldown = .25f;
		player.dmgOnHit = 2;
		player.spawnTimer = 1f;
		player.cannons = new Array<>();
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
	}

	private void createRandomEnemy () {
		createEnemy(MathUtils.random(-30, 24), MathUtils.random(12, 25), Entity.Facing.SOUTH);
	}

	protected void createEnemy (int x, int y, Entity.Facing facing) {
		Entity enemy = new Entity();
		enemy.type = Entity.TYPE_ENEMY;
		enemy.b.set(x, y, 7, 7);
		enemy.asset = Asset.get();
		enemy.asset.region = ship;
		enemy.asset.xOffset = -3;
		enemy.asset.yOffset = -3;
		enemy.asset.width = ship.getRegionWidth();
		enemy.asset.height = ship.getRegionHeight();
		enemy.health = 5;
		enemy.dmgOnHit = .33f;
		enemy.facing = facing;
		entities.add(enemy);
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
				if (entity.spawnTimer >= 0) {
					entity.spawnTimer -= delta;
				} else {
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

					// shoot
					entity.shootTimer -= delta;
					entity.health -= delta * .5f;

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
									bullet.health = 1;
									bullet.asset = Asset.get();
									bullet.asset.region = assets.bullet;
									bullet.asset.xOffset = -1;
									bullet.asset.yOffset = -1;
									bullet.asset.width = assets.bullet.getRegionWidth();
									bullet.asset.height = assets.bullet.getRegionHeight();
									bullet.b.x = entity.b.x + cannon.xOffset;
									bullet.b.y = entity.b.y + cannon.yOffset;
									bullet.b.width = 1;
									bullet.b.height = 1;
									bullet.vy = 25f;
									bullet.dmgOnHit = 1;
									entities.add(bullet);
									break;
								}
							}
						}
					}
				}
				vb.set(camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight - 5);
				if (entity.health <= 0) {
					entities.removeIndex(i);
					Entity.free(entity);
					createPlayer();
				}
//				debugPos.set(entity.b.x, entity.b.y, 0);
//				camera.position.set((int)entity.b.x, (int)entity.b.y, 0);
//				camera.update();
			} break;
			case Entity.TYPE_PLAYER_BULLET: {
				entity.b.x += entity.vx * delta;
				entity.b.y += entity.vy * delta;
				if (!vb.overlaps(entity.b) || entity.delete || entity.health <= 0) {
					entities.removeIndex(i);
					Entity.free(entity);
				}
			} break;
			case Entity.TYPE_ENEMY: {
				for (int j = 0; j < entities.size; j++) {
					Entity other = entities.get(j);
					if (other.type == Entity.TYPE_PLAYER_BULLET) {
						if (!other.delete && other.b.overlaps(entity.b)) {
							entity.health -= other.dmgOnHit;
							other.delete = true;
						}
					} else if (other.type == Entity.TYPE_PLAYER) {
						if (!other.delete && other.b.overlaps(entity.b)) {
							entity.health -= other.dmgOnHit;
							// need dmg per hit or something?
							other.health -= entity.dmgOnHit;
							if (other.health < 0) other.health = 0;
							entity.delete = true;
						}
					}
				}
				if (entity.health <= 0) {
					entities.removeIndex(i);
					Entity.free(entity);
					createRandomEnemy();
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
				if (entity.spawnTimer > 0) {
					batch.setColor(1, 1, 1, .66f + MathUtils.sin(entity.spawnTimer*24)/3);
					batch.draw(entity.asset.region, (int)(entity.b.x + entity.asset.xOffset), (int)(entity.b.y + entity.asset.yOffset),
						entity.asset.width, entity.asset.height);
					batch.setColor(1, 1, 1, 1);
				} else if (entity.health > 0) {
					batch.draw(entity.asset.region, (int)(entity.b.x + entity.asset.xOffset), (int)(entity.b.y + entity.asset.yOffset),
						entity.asset.width, entity.asset.height);
					int hp = MathUtils.floor(entity.health);
					float cap = entity.health - hp;
					for (int i = 0; i <= hp; i++) {
						float a = hp -i + cap;
						if (a > 1) a = 1;
						if (a < 0) a = 0;
						batch.setColor(1, 1, 1, a);
						batch.draw(assets.hp, (int)vb.x + 1+ i * 9, (int)vb.y + 1, 7, 6);
					}
					batch.setColor(1, 1, 1, 1);
				}
			} break;
			case Entity.TYPE_PLAYER_BULLET: {
				batch.draw(entity.asset.region,
					(int)(entity.b.x + entity.asset.xOffset), (int)(entity.b.y + entity.asset.yOffset), entity.asset.width, entity.asset.height);
			} break;
			case Entity.TYPE_ENEMY: {
				switch (entity.facing) {
				case NORTH:
					batch.draw(entity.asset.region,
						(int)(entity.b.x + entity.asset.xOffset), (int)(entity.b.y + entity.asset.xOffset),
						entity.asset.width/2, entity.asset.height/2,
						entity.asset.width, entity.asset.height,
						1, 1, 0);
					break;
				case EAST:
					batch.draw(entity.asset.region,
						(int)(entity.b.x + entity.asset.xOffset), (int)(entity.b.y + entity.asset.xOffset),
						entity.asset.width/2, entity.asset.height/2,
						entity.asset.width, entity.asset.height,
						1, 1, 90);
					break;
				case SOUTH:
					batch.draw(entity.asset.region,
						(int)(entity.b.x + entity.asset.xOffset), (int)(entity.b.y + entity.asset.xOffset),
						entity.asset.width/2, entity.asset.height/2,
						entity.asset.width, entity.asset.height,
						1, 1, 180);
					break;
				case WEST:
					batch.draw(entity.asset.region,
						(int)(entity.b.x + entity.asset.xOffset), (int)(entity.b.y + entity.asset.xOffset),
						entity.asset.width/2, entity.asset.height/2,
						entity.asset.width, entity.asset.height,
						1, 1, 270);
					break;
				}
			} break;
			case Entity.TYPE_ENEMY_BULLEt: {
				batch.draw(entity.asset.region,
					(int)(entity.b.x + entity.asset.xOffset), (int)(entity.b.y + entity.asset.yOffset), entity.asset.width, entity.asset.height);
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
		renderer.setColor(Color.CYAN);
		for (Entity entity : entities) {
			switch (entity.type) {
			case Entity.TYPE_PLAYER: {
				renderer.setColor(Color.CYAN);
				renderer.rect(entity.b.x, entity.b.y, entity.b.width, entity.b.height);
			}
			break;
			case Entity.TYPE_PLAYER_BULLET: {
				renderer.setColor(Color.CYAN);
				renderer.rect(entity.b.x, entity.b.y, entity.b.width, entity.b.height);
			}
			break;
			case Entity.TYPE_ENEMY: {
				renderer.setColor(Color.RED);
				switch (entity.facing) {
				case NORTH:
				case SOUTH: {
					renderer.rect(entity.b.x, entity.b.y, entity.b.width, entity.b.height);
				}
				break;
				case EAST:
				case WEST:
					renderer.rect(entity.b.x, entity.b.y, entity.b.height, entity.b.width);
					break;
				}
			}
			break;
			case Entity.TYPE_ENEMY_BULLEt: {
				renderer.setColor(Color.RED);
				renderer.rect(entity.b.x, entity.b.y, entity.b.width, entity.b.height);
			}
			break;
			}
		}
		renderer.end();
	}


	@Override public void resize (int width, int height) {
		viewport.update(width, height, false);
	}

	@Override public void dispose () {

	}
}
