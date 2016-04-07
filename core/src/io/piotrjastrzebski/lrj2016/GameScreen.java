package io.piotrjastrzebski.lrj2016;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line;

/**
 * Created by PiotrJ on 07/04/16.
 */
public class GameScreen extends BaseScreen {
	private OrthographicCamera camera;
	private FitViewport viewport;
	private TextureRegion ship;

	public GameScreen (LRJGame game) {
		super(game);
		camera = new OrthographicCamera();
		viewport = new FitViewport(64, 64, camera);
		ship = assets.ship;
	}

	boolean drawGrid = true;
	float speed = 10f;
	Vector3 position = new Vector3();
	@Override public void render (float delta) {

		if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			position.x -= speed * delta;
		} else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			position.x += speed * delta;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
			position.y += speed * delta;
		} else if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			position.y -= speed * delta;
		}

		camera.position.set((int)position.x, (int)position.y, 0);
		camera.update();

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
		batch.draw(ship, 0, 0);
		batch.end();

		// draw some debug stuff
		camera.position.set(position.x, position.y, 0);
		camera.update();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(Line);
		renderer.setColor(Color.CYAN);
		renderer.circle(position.x, position.y, 0.25f, 16);
		renderer.setColor(Color.MAGENTA);
		renderer.circle((int)position.x, (int)position.y, 0.25f, 16);
		renderer.end();
	}

	@Override public void resize (int width, int height) {
		viewport.update(width, height, false);
	}

	@Override public void dispose () {

	}
}
