package com.ivan;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.BufferedReader;
import java.io.File;
import java.util.Locale;


public class EndingScreen extends ScreenAdapter {

    private final BattleOverHanover game;
    private int gamePointAnInts;
    private float gameTimeAFloat;
    private TextureRegion groundBackground;
    private FileHandle fileHandle;
    private String[] scoreStringArray;
    private BufferedReader bufferedReader;
    private String line;


    public EndingScreen(BattleOverHanover _game, float _gameTimeAFloat, int _gamePointAnInts) {
        game = _game;
        gamePointAnInts = _gamePointAnInts;
        gameTimeAFloat = Float.parseFloat(String.format(Locale.ROOT,"%.2f", _gameTimeAFloat).replace(",", "."));
    }

    @Override
    public void show() {
        groundBackground = new TextureRegion(new Texture("plainx2.png"));
        fileHandle = Gdx.files.local("data.txt");
        if (fileHandle.exists()){
            String[] tempValuesArray = fileHandle.readString().split(" | ");
            if (Integer.parseInt(tempValuesArray[2]) <= gamePointAnInts){
                //if (Float.parseFloat(tempValuesArray[0]) > gameTimeAFloat){
                    fileHandle.writeString(String.format(Locale.ROOT,"%.2f | %d", gameTimeAFloat, gamePointAnInts), false);
                //}

            }
        } else {
            fileHandle.writeString(String.format(Locale.ROOT,"%.2f | %d", gameTimeAFloat, gamePointAnInts), false);
        }
        /*if (fileHandle.exists()){
            fileHandle.writeString(String.format(Locale.e,"\n%.2f | %d", gameTimeAFloat, gamePointAnInts), true);
            scoreStringArray = fileHandle.readString().split("\n");
            bubbleSortingMethod(scoreStringArray);
            fileHandle.writeString("", false);
            for (int i = 0; i < 10; i++) {
                try {
                    fileHandle.writeString(scoreStringArray[i] +"\n", true);
                } catch (ArrayIndexOutOfBoundsException e){

                }
            }
        } else {
            fileHandle.writeString(String.format("%.2f | %d", gameTimeAFloat, gamePointAnInts), false);
        }*/

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                game.setScreen(new Main(game));
                return true;
            }
        });
    }
    @Override
    public void resize(int width, int height) {
        game.fitViewport.update(width, height);
    }

    public static void bubbleSortingMethod(String[] score) {
        boolean sorted = false;
        String scoreTemp;
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < score.length -1; i++) {
                if (Integer.parseInt(score[i].split(" | ")[2]) > Integer.parseInt(score[i+1].split(" | ")[2])) {
                    if (Float.parseFloat(score[i].split(" | ")[0]) < Float.parseFloat(score[i+1].split(" | ")[0])) {
                        scoreTemp = score[i];
                        score[i+1] = score[i];
                        score[i] = scoreTemp;
                        sorted = false;
                    }
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 0.2f);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();
        game.batch.draw(groundBackground, 0 ,0 );
        game.wargate.draw(game.batch, String.format("Top Score\nTime: %s\nScore: %s", fileHandle.readString().split(" | ")[0], fileHandle.readString().split(" | ")[2]), 0, game.HEIGHT - 50, game.WIDTH, Align.center, true);
        game.wargateSubtitle.draw(game.batch, "Toca la pantalla para continuar", 0, 3*game.HEIGHT/4 - 200, game.WIDTH - 40, Align.center, true);
        game.batch.end();
    }

    @Override
    public void dispose() {

    }



    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }
}
