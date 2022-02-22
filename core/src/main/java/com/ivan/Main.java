package com.ivan;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends ScreenAdapter {
	public static final float WIDTH = 800;
	public static final float HEIGHT = 580;
	private static final float TERRAIN_SPEED_PPS = 100f;
	private static final float BACKGROUND_SPEED_PPS = 50f;
	public static final float PLANE_TAP_SPEED = 200f;
	public static final float GRAVITY_SPEED = -9.81f;
	private static final float MIN_PILLAR_DISTANCE = WIDTH/8f;
	private static final float PILLAR_DISTANCE_RANGE = 100;
	private static final float NEW_PILLAR_CONTROLLER = WIDTH/4f;
	public static final int BOX_ADJUSTMENT = 10;
	private final BattleOverHanover game;
	private float terrainOffset = 0;
	private float planeAnimTime;
	private float backgroundOffset = 0;
	private float damping = 0.99f;
	private SpriteBatch batch;
	private TextureRegion backgroundTextureRegion;
	private FPSLogger fpsLogger;
	private FitViewport fitViewport;
	private TextureRegion belowGrassTexture;
	private TextureRegion aboveGrassTexture;

	private Animation<TextureRegion> planeAnimation;
	private TextureRegion planeTexture0;
	private TextureRegion planeTexture1;
	private TextureRegion planeTexture2;
	private TextureAtlas textureAtlas;

	private Vector2 gravity;
	private Vector2 planePositionVector;
	private Vector2 defaultPlanePositionVector;
	private Vector2 planeVelocity;
	private TextureRegion pillarUp;
	private TextureRegion pillarDown;
	private TextureAtlas usMustang;
	private final Array<Vector2> pillarPosition = new Array<>();
	private Vector2 lastPillarPosition;

	private final Rectangle planeBoundingBox = new Rectangle();
	private final Rectangle pillarBoundingBox = new Rectangle();

	public Main(BattleOverHanover _game) {
		game = _game;
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		fpsLogger = new FPSLogger();


		usMustang = new TextureAtlas("plane.pack");

		textureAtlas = new TextureAtlas("ThrustCopter.pack");
		backgroundTextureRegion = textureAtlas.findRegion("background");
		belowGrassTexture = textureAtlas.findRegion("groundGrass");
		aboveGrassTexture = new TextureRegion(belowGrassTexture);
		aboveGrassTexture.flip(true, true);

		pillarUp = textureAtlas.findRegion("rockGrassUp");
		pillarDown = textureAtlas.findRegion("rockGrassDown");

		planeTexture0 = usMustang.findRegion("plane0");
		planeTexture0.flip(true, false);
		planeTexture1 = usMustang.findRegion("plane1");
		planeTexture1.flip(true, false);
		planeTexture2 = usMustang.findRegion("plane2");
		planeTexture2.flip(true, false);
		planeAnimation = new Animation(0.05f, planeTexture0, planeTexture1, planeTexture2, planeTexture1);
		planeAnimation.setPlayMode(Animation.PlayMode.LOOP);

		defaultPlanePositionVector = new Vector2(
				WIDTH/4*3-planeTexture0.getRegionWidth()/2,
				HEIGHT/2-planeTexture0.getRegionHeight()/2);
		planePositionVector = new Vector2(defaultPlanePositionVector);
		gravity = new Vector2(0, GRAVITY_SPEED);
		planeVelocity = new Vector2();

		addPillar();

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				planeVelocity.add(0, PLANE_TAP_SPEED);
				return true;
			}
		});

	}

	@Override
	public void resize(int width, int height) {
		game.fitViewport.update(width, height);
	}

	@Override
	public void render(float delta) {
		fpsLogger.log();
		updateScene();
		drawScene();
	}

	private void updateScene(){
		planeBoundingBox.set(planePositionVector.x+ BOX_ADJUSTMENT, planePositionVector.y+ BOX_ADJUSTMENT, planeTexture0.getRegionWidth()-(2*BOX_ADJUSTMENT), planeTexture0.getRegionHeight()-(2*BOX_ADJUSTMENT));

		if (planePositionVector.y > (HEIGHT - aboveGrassTexture.getRegionHeight()) || planePositionVector.y < (belowGrassTexture.getRegionHeight())/2){
			gameOver();
		}
		for (Vector2 pillar : pillarPosition){
			pillar.x += TERRAIN_SPEED_PPS * Gdx.graphics.getDeltaTime();
			if (pillar.y == 1){
				pillarBoundingBox.set(pillar.x+ BOX_ADJUSTMENT, 0, pillarUp.getRegionWidth()-(2*BOX_ADJUSTMENT), pillarUp.getRegionHeight());
			} else {
				pillarBoundingBox.set(pillar.x, HEIGHT - pillarUp.getRegionHeight(), pillarUp.getRegionWidth(), pillarUp.getRegionHeight());
			}
			if (planeBoundingBox.overlaps(pillarBoundingBox)){
				//Gameover
				gameOver();
			}
			if (pillar.x > WIDTH + pillarUp.getRegionWidth()){
				pillarPosition.removeValue(pillar, false);
				System.out.println("deleted"); //Log for deleted pillars
			}
		}
		if (lastPillarPosition.x > NEW_PILLAR_CONTROLLER){
			addPillar();
			System.out.println("addPillar"); //Log for created pillars
		}

		planeVelocity.add(gravity);
		planeVelocity.scl(damping);
		planePositionVector.mulAdd(planeVelocity, Gdx.graphics.getDeltaTime());
		planeAnimTime += Gdx.graphics.getDeltaTime();
		terrainOffset+= TERRAIN_SPEED_PPS * Gdx.graphics.getDeltaTime();
		if (terrainOffset >= belowGrassTexture.getRegionWidth()){
			terrainOffset = 0;
		}
		backgroundOffset+= BACKGROUND_SPEED_PPS * Gdx.graphics.getDeltaTime();
		if (backgroundOffset >= backgroundTextureRegion.getRegionWidth()){
			backgroundOffset = 0;
		}
	}
	private  void drawScene(){
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.camera.update();
		batch.setProjectionMatrix(game.camera.combined);
		batch.begin();
		batch.draw(backgroundTextureRegion, backgroundOffset, 0);
		batch.draw(backgroundTextureRegion, backgroundOffset - backgroundTextureRegion.getRegionWidth(), 0);

		for (Vector2 pillar : pillarPosition){
			if (pillar.y == 1){
				batch.draw(pillarUp, pillar.x, 0);
			} else {
				batch.draw(pillarDown, pillar.x, HEIGHT - pillarDown.getRegionHeight());
			}
		}

		batch.draw(belowGrassTexture, terrainOffset, 0);
		batch.draw(belowGrassTexture, terrainOffset - belowGrassTexture.getRegionWidth(), 0);
		batch.draw(aboveGrassTexture, terrainOffset, HEIGHT-aboveGrassTexture.getRegionHeight());
		batch.draw(aboveGrassTexture, terrainOffset - aboveGrassTexture.getRegionWidth(), HEIGHT-aboveGrassTexture.getRegionHeight());

		batch.draw(planeAnimation.getKeyFrame(planeAnimTime), planePositionVector.x, planePositionVector.y);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		textureAtlas.dispose();
	}


	private void addPillar(){
		Vector2 tmpPosition = new Vector2();
		if (pillarPosition.size == 0){
			tmpPosition.x =  -(MIN_PILLAR_DISTANCE + (float) (PILLAR_DISTANCE_RANGE *  Math.random()));
		} else {
			tmpPosition.x = -(lastPillarPosition.x +  MIN_PILLAR_DISTANCE + (float) (PILLAR_DISTANCE_RANGE *  Math.random()));
		}
		if (MathUtils.randomBoolean()){
			tmpPosition.y = 1;
		} else {
			tmpPosition.y = -1;
		}

		lastPillarPosition = tmpPosition;

		pillarPosition.add(tmpPosition);
	}




	private void gameOver(){
	 	Gdx.app.log("Game", "Over");
	 	game.setScreen(new Title(game));
	}


}