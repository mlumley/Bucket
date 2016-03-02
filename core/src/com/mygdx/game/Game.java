package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Game extends ApplicationAdapter {

	// Assets
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;

	// Camera
	private OrthographicCamera camera;
	private SpriteBatch batch;

	// Objects
	private Rectangle bucket;
	private Array<Rectangle> rainDrops;
	private long lastDropTime;

	// FPS
	private FPSLogger log;

	private void spawnRainDrop() {
		Rectangle rainDrop = new Rectangle();
		rainDrop.x = MathUtils.random(0, 800 - 64);
		rainDrop.y = 480;
		rainDrop.width = 64;
		rainDrop.height = 64;
		rainDrops.add(rainDrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void create() {
		// Images
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// Sounds
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		// Play Music
		rainMusic.setLooping(true);
		rainMusic.play();

		// Camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 400);

		// SpriteBatch
		batch = new SpriteBatch();

		// Objects
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		rainDrops = new Array<Rectangle>();
		spawnRainDrop();

		// FPS
		log = new FPSLogger();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for (Rectangle rainDrop : rainDrops) {
			batch.draw(dropImage, rainDrop.x, rainDrop.y);
		}
		batch.end();

		// FPS
		log.log();

		// Movement
		if ((Gdx.input.isKeyPressed(Keys.LEFT) || (Gdx.input.isKeyPressed(Keys.A))) && bucket.x > 0)
			bucket.x -= 400 * Gdx.graphics.getDeltaTime();
		if ((Gdx.input.isKeyPressed(Keys.RIGHT) || (Gdx.input.isKeyPressed(Keys.D))) && bucket.x < 800 - 64)
			bucket.x += 400 * Gdx.graphics.getDeltaTime();

		// RainDrops
		if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
			spawnRainDrop();

		Iterator<Rectangle> iter = rainDrops.iterator();
		while (iter.hasNext()) {
			Rectangle rainDrop = iter.next();
			rainDrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (rainDrop.y + 64 < 0)
				iter.remove();
			if (rainDrop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
			}
		}
	}

	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
}
