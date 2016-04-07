package io.piotrjastrzebski.lrj2016;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * We need 64x64 viewport + controls on android
 * we want to do this as procedural as possible
 */
public class LRJGame extends Game {
	public SpriteBatch batch;
	public ShapeRenderer renderer;
	public Assets assets;

	@Override
	public void create () {
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();
		assets = new Assets();
		setScreen(new MenuScreen(this));
	}

	@Override public void dispose () {
		assets.dispose();
		renderer.dispose();
		batch.dispose();
	}
}
