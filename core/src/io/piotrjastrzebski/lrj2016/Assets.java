package io.piotrjastrzebski.lrj2016;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by PiotrJ on 07/04/16.
 */
public class Assets implements Disposable {
	public TextureRegion ship;
	public TextureRegion bullet;
	public TextureRegion hp;

	public Assets () {
		ship = new TextureRegion(new Texture("ship.png"));
		bullet = new TextureRegion(new Texture("bullet.png"));
		hp = new TextureRegion(new Texture("hp.png"));
	}

	@Override public void dispose () {
		ship.getTexture().dispose();
		bullet.getTexture().dispose();
		hp.getTexture().dispose();
	}
}
