package co.uk.epucguru.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

public class TestGame extends Game {

	private Color backgroundColour = new Color(.9f, .9f, .9f, 1);
	
	public static void main(String... args){
		// Create game instance
		TestGame game = new TestGame();

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Voice Chat Test");
		config.useVsync(false);
		
		new Lwjgl3Application(game, config);

		// Done
		System.gc();
		System.exit(0);
	}


	@Override
	public void create() {

	}

	public void render(){
		Gdx.gl.glClearColor(backgroundColour.r, backgroundColour.g, backgroundColour.b, backgroundColour.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}