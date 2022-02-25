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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class Main extends ScreenAdapter {

	private final BattleOverHanover game;
	private float gameTimeAFloat;
	private float terrainOffset = 0;
	private float planeAnimTime;
	private float backgroundOffset = 0;
	private float damping = 0.99f;
	private TextureRegion backgroundTextureRegion;
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
	private TextureRegion shield;
	private TextureAtlas usMustang;
	private final Array<Vector2> pillarPosition = new Array<>();
	private Vector2 lastPillarPosition;
	private final Array<Vector2> shieldPosition = new Array<>();
	private Vector2 shieldLastPosition;

	private final Rectangle planeBoundingBox = new Rectangle();
	private final Rectangle pillarBoundingBox = new Rectangle();
	private final Rectangle pointBoundingBox = new Rectangle();
	private final Rectangle shieldBoundingBox = new Rectangle();
	private float shieldTimeStamp;
	private int overlapsedPillarTime;
	private int overlapsedPillarPoints = 0;
	private int gamePointAnInts = 0;

	public Main(BattleOverHanover _game) {
		game = _game;
	}

	@Override
	public void show() {
		usMustang = new TextureAtlas("plane.pack");

		textureAtlas = new TextureAtlas("ThrustCopter.pack");
		backgroundTextureRegion = new TextureRegion(new Texture("background.png"));
		belowGrassTexture = new TextureRegion(new Texture("road.png"));
		aboveGrassTexture = new TextureRegion(belowGrassTexture);
		aboveGrassTexture.flip(true, true);

		pillarUp = new TextureRegion(new Texture("pillar.png"));
		pillarDown = new TextureRegion(pillarUp);
		pillarDown.flip(true, true);
		overlapsedPillarTime = pillarDown.getRegionWidth();
		shield = new TextureRegion(new Texture("health.png"));

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
		addShield();

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
				pillarBoundingBox.set(pillar.x+ game.BOX_ADJUSTMENT, 0, pillarUp.getRegionWidth()-(2*game.BOX_ADJUSTMENT), pillarUp.getRegionHeight() - 20);
				pointBoundingBox.set(pillar.x, 0, 1, game.HEIGHT);
			} else {
				pillarBoundingBox.set(pillar.x+ game.BOX_ADJUSTMENT, game.HEIGHT - pillarUp.getRegionHeight(), pillarUp.getRegionWidth(), pillarUp.getRegionHeight() -20);
				pointBoundingBox.set(pillar.x, 0, 1, game.HEIGHT);
			}
			if (planeBoundingBox.overlaps(pillarBoundingBox)){
				if (shieldTimeStamp+ game.SHIELD_HEALTH_TIME < gameTimeAFloat){
					gameOver();
				}
			}
			if (planeBoundingBox.overlaps(pointBoundingBox)){
				overlapsedPillarPoints +=1;
				gamePointAnInts = overlapsedPillarPoints/overlapsedPillarTime;
			}
			if (pillar.x > game.WIDTH + pillarUp.getRegionWidth()){
				pillarPosition.removeValue(pillar, false);
			}
		}
		if (lastPillarPosition.x > game.NEW_PILLAR_CONTROLLER){
			addPillar();
		}

		for (Vector2 shields : shieldPosition){
			shields.x += game.TERRAIN_SPEED_PPS * Gdx.graphics.getDeltaTime();
			if (shields.y == 1){
				shieldBoundingBox.set(shields.x, game.HEIGHT/2, shield.getRegionWidth() - game.BOX_ADJUSTMENT, shield.getRegionHeight() - game.BOX_ADJUSTMENT);
			} else {
				shieldBoundingBox.set(shields.x, game.HEIGHT/2, shield.getRegionWidth() - game.BOX_ADJUSTMENT, shield.getRegionHeight() - game.BOX_ADJUSTMENT);
			}
			if (planeBoundingBox.overlaps(shieldBoundingBox)){
				shieldTimeStamp = gameTimeAFloat;
				shieldPosition.removeValue(shields, false);
			}
			if (shields.x > game.WIDTH + shield.getRegionWidth()){
				shieldPosition.removeValue(shields, false);
			}
		}
		if (shieldLastPosition.x > game.NEW_SHIELD_CONTROLLER){
			addShield();
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
		game.batch.draw(backgroundTextureRegion, backgroundOffset - backgroundTextureRegion.getRegionWidth() + game.WIDTH, 0);
		game.batch.draw(backgroundTextureRegion, backgroundOffset - backgroundTextureRegion.getRegionWidth(), 0);

		for (Vector2 pillar : pillarPosition){
			if (pillar.y == 1){
				game.batch.draw(pillarUp, pillar.x, 0);
			} else {
				game.batch.draw(pillarDown, pillar.x, game.HEIGHT - pillarDown.getRegionHeight());
			}
		}for (Vector2 shields : shieldPosition){
			if (shields.y == 1){
				game.batch.draw(shield, shields.x, game.HEIGHT/2);
			} else {
				game.batch.draw(shield, shields.x, game.HEIGHT/2 - 20);
			}
		}

		game.batch.draw(belowGrassTexture, terrainOffset, 0);
		game.batch.draw(belowGrassTexture, terrainOffset - belowGrassTexture.getRegionWidth(), 0);
		game.batch.draw(aboveGrassTexture, terrainOffset, game.HEIGHT-aboveGrassTexture.getRegionHeight());
		game.batch.draw(aboveGrassTexture, terrainOffset - aboveGrassTexture.getRegionWidth(), game.HEIGHT-aboveGrassTexture.getRegionHeight());


		game.batch.draw(planeAnimation.getKeyFrame(planeAnimTime), planePositionVector.x, planePositionVector.y);

		game.wargateSubtitle.draw(game.batch, String.format("Time: %.2f", gameTimeAFloat), 0, game.HEIGHT -5 );

		game.wargateSubtitle.draw(game.batch, String.format("Points: %d", gamePointAnInts), game.WIDTH -350, game.HEIGHT -5 );

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
	private void addShield(){
		Vector2 tmpPosition = new Vector2();
		if (shieldPosition.size == 0){
			tmpPosition.x =  -(10 + (float) (game.SHIELD_DISTANCE_RANGE *  Math.random()));
		} else {
			tmpPosition.x = -(shieldLastPosition.x +  game.MIN_SHIELD_DISTANCE + (float) (game.SHIELD_DISTANCE_RANGE *  Math.random()));
		}
		if (MathUtils.randomBoolean()){
			tmpPosition.y = 1;
		} else {
			tmpPosition.y = -1;
		}
		shieldLastPosition = tmpPosition;
		System.out.println(shieldLastPosition.toString());

		shieldPosition.add(tmpPosition);
	}

	private void gameOver(){
	 	game.setScreen(new EndingScreen(game, gameTimeAFloat, gamePointAnInts));
	}


}