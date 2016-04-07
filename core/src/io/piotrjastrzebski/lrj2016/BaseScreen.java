
package io.piotrjastrzebski.lrj2016;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by PiotrJ on 07/04/16.
 */
public class BaseScreen implements Screen, InputProcessor {
	protected LRJGame game;
	protected SpriteBatch batch;
	protected ShapeRenderer renderer;
	protected Assets assets;

	public BaseScreen (LRJGame game) {
		this.game = game;
		batch = game.batch;
		renderer = game.renderer;
		assets = game.assets;
	}

	@Override public void show () {

	}

	@Override public void render (float delta) {

	}

	@Override public void resize (int width, int height) {

	}

	@Override public void pause () {

	}

	@Override public void resume () {

	}

	@Override public void hide () {
		dispose();
	}

	@Override public void dispose () {

	}

	@Override public boolean keyDown (int keycode) {
		return false;
	}

	@Override public boolean keyUp (int keycode) {
		return false;
	}

	@Override public boolean keyTyped (char character) {
		return false;
	}

	@Override public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override public boolean touchDragged (int screenX, int screenY, int pointer) {
		return false;
	}

	@Override public boolean mouseMoved (int screenX, int screenY) {
		return false;
	}

	@Override public boolean scrolled (int amount) {
		return false;
	}
}
