package com.ivan;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Main extends ScreenAdapter {
	private final BattleOverHanover game;
	private float gameTimeAFloat;
	private float terrainOffset = 0;
	private float planeAnimTime;
	private float backgroundOffset = 0;
	private float damping = 0.99f;
	private TextureRegion backgroundTextureRegion;
	private FPSLogger fpsLogger;
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
		fpsLogger = new FPSLogger();


		usMustang = new TextureAtlas("plane.pack");

		textureAtlas = new TextureAtlas("ThrustCopter.pack");
		backgroundTextureRegion = new TextureRegion(new Texture("background.png"));
		belowGrassTexture = new TextureRegion(new Texture("road.png"));
		aboveGrassTexture = new TextureRegion(belowGrassTexture);
		aboveGrassTexture.flip(true, true);

		pillarUp = new TextureRegion(new Texture("pillar.png"));
		pillarDown = new TextureRegion(pillarUp);
		pillarDown.flip(true, true);

		planeTexture0 = usMustang.findRegion("plane0");
		planeTexture0.flip(true, false);
		planeTexture1 = usMustang.findRegion("plane1");
		planeTexture1.flip(true, false);
		planeTexture2 = usMustang.findRegion("plane2");
		planeTexture2.flip(true, false);
		planeAnimation = new Animation(0.05f, planeTexture0, planeTexture1, planeTexture2, planeTexture1);
		planeAnimation.setPlayMode(Animation.PlayMode.LOOP);

		defaultPlanePositionVector = new Vector2(
				game.WIDTH/4*3-planeTexture0.getRegionWidth()/2,
				game.HEIGHT/2-planeTexture0.getRegionHeight()/2);
		planePositionVector = new Vector2(defaultPlanePositionVector);
		gravity = new Vector2(0, game.GRAVITY_SPEED);
		planeVelocity = new Vector2();

		addPillar();

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				planeVelocity.add(0, game.PLANE_TAP_SPEED);
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
		updateScene(delta);
		drawScene();
	}

	private void updateScene(float delta){
		gameTimeAFloat+= delta;
		planeBoundingBox.set(planePositionVector.x+ game.BOX_ADJUSTMENT, planePositionVector.y+ game.BOX_ADJUSTMENT, planeTexture0.getRegionWidth()-(2*game.BOX_ADJUSTMENT), planeTexture0.getRegionHeight()-(2*game.BOX_ADJUSTMENT));

		if (planePositionVector.y > (game.HEIGHT - aboveGrassTexture.getRegionHeight()) || planePositionVector.y < (belowGrassTexture.getRegionHeight())/2){
			gameOver();
		}
		for (Vector2 pillar : pillarPosition){
			pillar.x += game.TERRAIN_SPEED_PPS * Gdx.graphics.getDeltaTime();
			if (pillar.y == 1){
				pillarBoundingBox.set(pillar.x+ game.BOX_ADJUSTMENT, 0, pillarUp.getRegionWidth()-(2*game.BOX_ADJUSTMENT), pillarUp.getRegionHeight());
			} else {
				pillarBoundingBox.set(pillar.x, game.HEIGHT - pillarUp.getRegionHeight(), pillarUp.getRegionWidth(), pillarUp.getRegionHeight());
			}
			if (planeBoundingBox.overlaps(pillarBoundingBox)){
				//Gameover
				gameOver();
			}
			if (pillar.x > game.WIDTH + pillarUp.getRegionWidth()){
				pillarPosition.removeValue(pillar, false);
				System.out.println("deleted"); //Log for deleted pillars
			}
		}
		if (lastPillarPosition.x > game.NEW_PILLAR_CONTROLLER){
			addPillar();
			System.out.println("addPillar"); //Log for created pillars
		}

		planeVelocity.add(gravity);
		planeVelocity.scl(damping);
		planePositionVector.mulAdd(planeVelocity, Gdx.graphics.getDeltaTime());
		planeAnimTime += Gdx.graphics.getDeltaTime();
		terrainOffset+= game.TERRAIN_SPEED_PPS * Gdx.graphics.getDeltaTime();
		if (terrainOffset >= belowGrassTexture.getRegionWidth()){
			terrainOffset = 0;
		}
		backgroundOffset+= game.BACKGROUND_SPEED_PPS * Gdx.graphics.getDeltaTime();
		if (backgroundOffset >= backgroundTextureRegion.getRegionWidth()){
			backgroundOffset = 0;
		}
	}
	private  void drawScene(){
		Gdx.gl.glClearColor(20,147,146,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.camera.update();
		game.batch.setProjectionMatrix(game.camera.combined);
		game.batch.begin();
		game.batch.draw(backgroundTextureRegion, backgroundOffset, 0);
		game.batch.draw(backgroundTextureRegion, backgroundOffset - backgroundTextureRegion.getRegionWidth(), 0);

		for (Vector2 pillar : pillarPosition){
			if (pillar.y == 1){
				game.batch.draw(pillarUp, pillar.x, 0);
			} else {
				game.batch.draw(pillarDown, pillar.x, game.HEIGHT - pillarDown.getRegionHeight());
			}
		}

		game.batch.draw(belowGrassTexture, terrainOffset, 0);
		game.batch.draw(belowGrassTexture, terrainOffset - belowGrassTexture.getRegionWidth(), 0);
		game.batch.draw(aboveGrassTexture, terrainOffset, game.HEIGHT-aboveGrassTexture.getRegionHeight());
		game.batch.draw(aboveGrassTexture, terrainOffset - aboveGrassTexture.getRegionWidth(), game.HEIGHT-aboveGrassTexture.getRegionHeight());

		game.batch.draw(planeAnimation.getKeyFrame(planeAnimTime), planePositionVector.x, planePositionVector.y);


		game.batch.end();
	}

	@Override
	public void dispose() {
		game.batch.dispose();
		textureAtlas.dispose();
	}


	private void addPillar(){
		Vector2 tmpPosition = new Vector2();
		if (pillarPosition.size == 0){
			tmpPosition.x =  -(game.MIN_PILLAR_DISTANCE + (float) (game.PILLAR_DISTANCE_RANGE *  Math.random()));
		} else {
			tmpPosition.x = -(lastPillarPosition.x +  game.MIN_PILLAR_DISTANCE + (float) (game.PILLAR_DISTANCE_RANGE *  Math.random()));
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