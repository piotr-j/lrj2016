package io.piotrjastrzebski.lrj2016.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by PiotrJ on 10/04/16.
 */
public class Asset {
	private static Pool<Asset> pool = new Pool<Asset>() {
		@Override protected Asset newObject () {
			return new Asset();
		}
	};

	public TextureRegion region;
	public float xOffset;
	public float yOffset;
	public float width;
	public float height;

	public static Asset get () {
		return pool.obtain();
	}
	public static void free (Asset asset) {
		pool.free(asset);
		asset.region = null;
		asset.xOffset = 0;
		asset.yOffset = 0;
		asset.width = 0;
		asset.height = 0;
	}
}
