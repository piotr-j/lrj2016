package io.piotrjastrzebski.lrj2016;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

/**
 * Created by PiotrJ on 07/04/16.
 */
public class MenuScreen extends BaseScreen {
	private float timer;
	public MenuScreen (LRJGame game) {
		super(game);

	}

	@Override public void render (float delta) {
		Gdx.gl.glClearColor(0, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		timer += delta;
		if (timer > 0.1f) {
			// TODO show some menu stuff
			game.setScreen(new GameScreen(game));
		}
	}

	@Override public void dispose () {

	}
}
