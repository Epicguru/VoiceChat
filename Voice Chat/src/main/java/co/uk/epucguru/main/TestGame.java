package co.uk.epucguru.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

import co.uk.epucguru.classes.VoiceChatClient;

public class TestGame extends Game {

	private Color backgroundColour = new Color(.9f, .9f, .9f, 1);
	private Server server;
	private Client client;
	private VoiceChatClient sender, reciever;
	
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
		try{
			this.server = new Server(22050, 22050);
			server.bind(7777, 7777);
			server.start();
			
			this.client = new Client(22050, 22050);
			client.start();
			client.connect(5000, "localhost", 7777, 7777);
			
			this.sender = new VoiceChatClient(client.getKryo());
			this.reciever = new VoiceChatClient(server.getKryo());
			
			reciever.addReciever(server);
			
		}catch(Exception e){
			e.printStackTrace();
			Gdx.app.exit();
		}		
	}

	public void render(){
		Gdx.gl.glClearColor(backgroundColour.r, backgroundColour.g, backgroundColour.b, backgroundColour.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		this.server.getConnections()[0].updateReturnTripTime();
		Gdx.graphics.setTitle("Voice Chat Test by James Billy - " + Gdx.graphics.getFramesPerSecond() + "fps, " + this.server.getConnections()[0].getReturnTripTime() + " ping.");
		
		if(Gdx.input.isKeyPressed(Keys.SPACE)){			
			this.sender.update(this.client, Gdx.graphics.getDeltaTime());	
		}
	}
}