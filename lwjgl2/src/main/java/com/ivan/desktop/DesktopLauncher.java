package com.ivan.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ivan.BattleOverHanover;
import com.ivan.Main;

/** Launches the desktop (LWJGL) application. */
public class DesktopLauncher {
	public static void main(String[] args) {
		createApplication();
	}

	private static LwjglApplication createApplication() {
		return new LwjglApplication(new BattleOverHanover(), getDefaultConfiguration());
	}

	private static LwjglApplicationConfiguration getDefaultConfiguration() {
		LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
		configuration.title = "Battle Over Hanover";
		configuration.width = (int) BattleOverHanover.WIDTH;
		configuration.height = (int) BattleOverHanover.HEIGHT;
		//// This prevents a confusing error that would appear after exiting normally.
		configuration.forceExit = false;

		for (int size : new int[] { 128, 64, 32, 16 }) {
			configuration.addIcon("libgdx" + size + ".png", FileType.Internal);
		}
		return configuration;
	}
}