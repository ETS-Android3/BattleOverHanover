package com.ivan;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class BattleOverHanover extends Game {
    //Utilities

    public static final float WIDTH = 800;
    public static final float HEIGHT = 580;
    public OrthographicCamera camera;
    public FitViewport fitViewport;
    public SpriteBatch batch;

    //Constants
    public static final float TERRAIN_SPEED_PPS = 100f;
    public static final float BACKGROUND_SPEED_PPS = 50f;
    public static final float PLANE_TAP_SPEED = 200f;
    public static final float GRAVITY_SPEED = -9.81f;
    public static final float MIN_PILLAR_DISTANCE = WIDTH/8f;
    public static final float PILLAR_DISTANCE_RANGE = 100;
    public static final float NEW_PILLAR_CONTROLLER = WIDTH/4f;
    public static final int BOX_ADJUSTMENT = 10;


    //Main Tittle
    private TextureAtlas texturesTextureAtlas;
    private Animation<TextureRegion> bomberAnimation;
    private Animation<TextureRegion> fighterAnimation;
    private Animation<TextureRegion> bombingAnimation;

    private TextureRegion bomber0TextureRegion;
    private TextureRegion bomber1TextureRegion;
    private TextureRegion bomber2TextureRegion;
    private TextureRegion bomber3TextureRegion;

    private TextureRegion fighter0TextureRegion;
    private TextureRegion fighter1TextureRegion;
    private TextureRegion fighter2TextureRegion;
    private TextureRegion fighter3TextureRegion;
    private TextureRegion fighter4TextureRegion;
    private TextureRegion fighter5TextureRegion;
    private TextureRegion fighter6TextureRegion;
    private TextureRegion fighter7TextureRegion;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.position.set(WIDTH*0.5F, HEIGHT*0.5F, 0);
        fitViewport = new FitViewport(WIDTH, HEIGHT,camera);
        setScreen(new Title(this));
    }
}
