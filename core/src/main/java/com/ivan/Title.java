package com.ivan;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

import java.awt.event.MouseAdapter;


public class Title extends ScreenAdapter {

    private final BattleOverHanover game;



    public Title(BattleOverHanover battleOverHanover) {
        game = battleOverHanover;
    }

        SpriteBatch batch;
        Texture img;
        Texture stuka;
        Texture[] usBomber = new Texture[4];
        Texture[] usMustang = new Texture[8];
        Texture sunBackground;
        Texture nightBackground;
        Texture ground;
        Texture groundBackground;
        Texture explosionDamage;
        Texture[] clouds = new Texture[4];
        Texture[] airExplosion = new Texture[26];
        Texture fence;
        Texture moon;
        Texture[] touchToContinue = new Texture[3];
        int x = 0;
        int airExplosionAnimation = 1;
        int airExplosionAnimation2 = 1;
        int airExplosionAnimation3 = 1;
        int breachX = 0;
        int y = 250;
        int X = 20;
        int groundPosition = 200;
        int bomberAnimation = 0;
        int fighterPlaneAnimation = 0;
        int[] cloudXPositions;
        int[] cloudYPositions;
        private double skyMinimum = 400;
        int[] airplaneFormation;
        int skyMaximum;
        int[] explosionY;
        int[] explosionX;
        public static Music backgroundMusic;
        public Sound explosionSound;
        private int continueAnimation = 0;
        private GlyphLayout glyphLayout;
        private GlyphLayout glyphLayoutSub;


        @Override
        public void show() {
            batch = new SpriteBatch();
            stuka = new Texture("GER_Ju87R.png");
            nightBackground = new Texture("sky.png");
            sunBackground = new Texture("sky_sun.png");
            ground = new Texture("road.png");
            groundBackground = new Texture("plainx2.png");
            moon = new Texture("moon.png");
            for (int i = 1; i < airExplosion.length; i++) {
                airExplosion[i] = new Texture("E" + i + ".png");
            }
            explosionDamage = new Texture("crater1.png");
            fence = new Texture("fence.png");
            for (int i = 0; i < usBomber.length; i++) {
                usBomber[i] = new Texture("B17_" + i + ".png");
            }
            for (int i = 0; i < clouds.length; i++) {
                clouds[i] = new Texture("cloud_" + i + ".png");
            }
            for (int i = 0; i < usMustang.length; i++) {
                usMustang[i] = new Texture("US_P51_" + i + ".png");
            }
            cloudYPositions = new int[]{calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY()};
            cloudXPositions = new int[]{calculateX(), calculateX(), calculateX(), calculateX(), calculateX(), calculateX(), calculateX(), calculateX(), calculateX(), calculateX(), calculateX(), calculateX(), calculateX(), calculateX(), calculateX()};
            airplaneFormation = new int[]{calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY()};
            skyMaximum = Gdx.graphics.getHeight() - 400;
            explosionY = new int[]{calculateY(), calculateY(), calculateY(), calculateY(), calculateY(), calculateY()};
            explosionX = new int[]{calculateX(), calculateX(), calculateX(), calculateX(), calculateX(), calculateX()};
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("AirBattleRaidSound.wav"));
            backgroundMusic.play();
            backgroundMusic.setVolume(0.25f);
            explosionSound = Gdx.audio.newSound(Gdx.files.internal("bomb.wav"));

            glyphLayout = new GlyphLayout();
            glyphLayout.setText(game.wargate, "battle over hanover", game.wargate.getColor(), 0, Align.center, false);

            glyphLayoutSub = new GlyphLayout();
            glyphLayoutSub.setText(game.wargateSubtitle, "Presiona espacio para continuar", game.wargateSubtitle.getColor(), 0, Align.center, false);

            Gdx.input.setInputProcessor(new InputAdapter() {
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    backgroundMusic.stop();
                    game.setScreen(new Main(game));
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
            ScreenUtils.clear(0, 0, 0, 0.2f);
            game.camera.update();
            batch.setProjectionMatrix(game.camera.combined);
            batch.begin();
            batch.draw(groundBackground, 0, 0);
            batch.draw(airExplosion[airExplosionAnimation], explosionX[0], explosionY[0]);
            batch.draw(usMustang[fighterPlaneAnimation], x + 250, airplaneFormation[0]);
            batch.draw(usMustang[fighterPlaneAnimation], x - 300, airplaneFormation[1]);
            batch.draw(airExplosion[airExplosionAnimation], explosionX[1], explosionY[1]);
            batch.draw(usMustang[fighterPlaneAnimation], x - 370, airplaneFormation[2]);
            batch.draw(usMustang[fighterPlaneAnimation], x - 200, airplaneFormation[3]);

            batch.draw(airExplosion[airExplosionAnimation], explosionX[2], explosionY[2]);
            batch.draw(usBomber[bomberAnimation], x, airplaneFormation[4]);
            batch.draw(usMustang[fighterPlaneAnimation], x - 750, airplaneFormation[5]);

            batch.draw(airExplosion[airExplosionAnimation2], explosionX[3], explosionY[3]);
            drawClouds(batch);
            batch.draw(usBomber[bomberAnimation], x + 120, airplaneFormation[6]);

            batch.draw(airExplosion[airExplosionAnimation2], explosionX[4], explosionY[4]);
            batch.draw(usMustang[fighterPlaneAnimation], x - 110, airplaneFormation[7]);
            batch.draw(usBomber[bomberAnimation], x - 600, airplaneFormation[8]);

            batch.draw(airExplosion[airExplosionAnimation3], explosionX[5], explosionY[5]);
           // wargate.draw(batch, glyphLayout, game.fitViewport.getWorldWidth()/2, game.fitViewport.getWorldHeight()/2 + glyphLayout.height, game.WIDTH -10, glyphLayout.height);
            game.wargate.draw(batch, "Battle over hanover", 0, 3*game.HEIGHT/4, game.WIDTH - 15, Align.center, true);
            game.wargateSubtitle.draw(batch, "Toca la pantalla para continuar", 0, 3*game.HEIGHT/4 - 200, game.WIDTH - 20, Align.center, true);
            //wargateSubtitle.draw(batch, glyphLayoutSub, game.fitViewport.getWorldWidth()/2  , (game.fitViewport.getWorldHeight()/2 - glyphLayout.height + glyphLayoutSub.height/2));
            x++;
            if (x % 2 == 0) {
                groundPosition--;
                breachX = groundPosition;
                if (groundPosition == -Gdx.graphics.getWidth()) {
                    groundPosition = 200;
                }
            }
            if (bomberAnimation == 3) {
                bomberAnimation = 0;
            } else {
                bomberAnimation++;
            }
            if (fighterPlaneAnimation == 7) {
                fighterPlaneAnimation = 0;
            } else {
                fighterPlaneAnimation++;
            }

            if (x == Gdx.graphics.getWidth() + +750) {
                //Si se sale del borde los aviones se generan a la izquierda
                for (int i = 0; i < airplaneFormation.length; i++) {
                    airplaneFormation[i] = calculateY();
                }
                x = -750;
            }
            if (x % 6 == 0) {
                if (airExplosionAnimation2 == airExplosion.length - 1) {
                    for (int i = 2; i < 4; i++) {
                        explosionY[i] = calculateY();
                    }
                    for (int i = 2; i < 4; i++) {
                        explosionX[i] = calculateX();
                    }

                    explosionSound.play(0.2f);
                    airExplosionAnimation2 = 1;
                } else {
                    airExplosionAnimation2++;
                }


                for (int i = 0; i < cloudXPositions.length; i++) {
                    if (cloudXPositions[i] == -clouds[3].getWidth()) {
                        cloudXPositions[i] = Gdx.graphics.getWidth();
                        cloudYPositions[i] = calculateY();
                    } else {
                        cloudXPositions[i]--;
                    }
                }
            }
            if (x % 4 == 0) {

                if (airExplosionAnimation3 == airExplosion.length - 1) {
                    for (int i = 4; i < 5; i++) {
                        explosionY[i] = calculateY();
                    }
                    for (int i = 4; i < 5; i++) {
                        explosionX[i] = calculateX();
                    }

                    explosionSound.play(0.2f);
                    airExplosionAnimation3 = 1;
                } else {
                    airExplosionAnimation3++;
                }
            }
            if (x % 5 == 0) {
                if (airExplosionAnimation == airExplosion.length - 1) {
                    for (int i = 0; i < 2; i++) {
                        explosionY[i] = calculateY();
                    }
                    for (int i = 0; i < 2; i++) {
                        explosionX[i] = calculateX();
                    }
                    explosionSound.play(0.2f);
                    airExplosionAnimation = 1;
                } else {
                    airExplosionAnimation++;
                }
            }
            batch.end();
        }

        @Override
        public void dispose() {
            batch.dispose();
            img.dispose();
            backgroundMusic.dispose();
            game.wargate.dispose();
            game.wargateSubtitle.dispose();
        }

        public int calculateY() {

            return (int) (Math.random() * Gdx.graphics.getHeight());

        }

        public int calculateX() {
            return (int) (Math.random() * (Gdx.graphics.getWidth()));
        }

        public void drawClouds(SpriteBatch batch) {
            batch.draw(clouds[1], cloudXPositions[0], cloudYPositions[0]);
            batch.draw(clouds[3], cloudXPositions[1], cloudYPositions[1]);
            batch.draw(clouds[0], cloudXPositions[2], cloudYPositions[2]);
            batch.draw(clouds[2], cloudXPositions[3], cloudYPositions[3]);
            batch.draw(clouds[1], cloudXPositions[4], cloudYPositions[4]);
            batch.draw(clouds[3], cloudXPositions[5], cloudYPositions[5]);
            batch.draw(clouds[2], cloudXPositions[6], cloudYPositions[6]);
            batch.draw(clouds[0], cloudXPositions[7], cloudYPositions[7]);
            batch.draw(clouds[3], cloudXPositions[8], cloudYPositions[8]);
            batch.draw(clouds[0], cloudXPositions[9], cloudYPositions[9]);
            batch.draw(clouds[2], cloudXPositions[10], cloudYPositions[10]);
            batch.draw(clouds[1], cloudXPositions[11], cloudYPositions[11]);
            batch.draw(clouds[3], cloudXPositions[12], cloudYPositions[12]);
            batch.draw(clouds[2], cloudXPositions[13], cloudYPositions[13]);
            batch.draw(clouds[0], cloudXPositions[14], cloudYPositions[14]);

        }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }
}
