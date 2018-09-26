package co.uk.epucguru.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

/**
 * A class that is used to send and receive voice chat. Works using KryoNet and LibGDX.
 * See <a href="https://github.com/Epicguru/VoiceChat">the github page</a> for the project and for my other projects.
 * <li>IMPORTANT:
 * You are allowed to use this mini-library in any way you wish. You may use it in any project, commercial or not, 
 * and feel free to edit the source code. Just please note that I put some time and effort into making this, so it would be great if
 * you left this here so that people know that it was me that made this. Thanks. 
 * (Also if you are feeling generous or you appreciate what I have done, it would be great if you put me in the 'credits' section of you game or project.)
 * 
 * @author James Billy, 2017
 *
 */
public class VoiceChatClient implements Disposable{

	/**
	 * The default sampling rate for audio. 22050.
	 */
	private static final int DEFAULT_SAMPLE_RATE = 22050;
	private AudioRecorder recorder;
	private AudioDevice player;
	private int sampleRate = DEFAULT_SAMPLE_RATE; // Default and standard.
	private float sendRate = 20f;
	private short[] data;
	private float timer;
	private boolean ready = true;
	
	/**
	 * Creates a new {@link VoiceChatClient} and registers net objects.
	 * @param kryo The {@link Kryo} object that exists in KryoNet Clients and Servers.
	 * <li>See <code>client.getKryo()</code> and <code>server.getKryo()</code>.
	 * @param sampleRate The audio sampling rate, as in samples per second. This defaults to {@link #DEFAULT_SAMPLE_RATE}.
	 * @see 
	 * <li> {@link #addReceiver(Client)} to allow this voice client to play audio sent from other clients.
	 * <li> {@link #sendVoice(Client, float)} to send the users voice to other clients, through the server.
	 */
	public VoiceChatClient(Kryo kryo, int sampleRate){
		this(kryo);
		
		this.sampleRate = sampleRate;
	}	
	
	/**
	 * Creates a new {@link VoiceChatClient} and registers net objects.
	 * @param kryo The {@link Kryo} object that exists in KryoNet Clients and Servers.
	 * <li>See <code>client.getKryo()</code> and <code>server.getKryo()</code>.
	 * @see 
	 * <li> {@link #addReceiver(Client)} to allow this voice client to play audio sent from other clients.
	 * <li> {@link #sendVoice(Client, float)} to send the users voice to other clients, through the server.
	 */
	public VoiceChatClient(Kryo kryo){
		this.registerNetObjects(kryo);
	}
	
	/**
	 * Gets the audio recording sample rate. Default value is {@link #DEFAULT_SAMPLE_RATE}.
	 * @return The current sampling rate. This can be changed at runtime, but is not recommended.
	 */
	public int getSampleRate(){
		return this.sampleRate;
	}
	
	/**
	 * Gets the time per second that the audio is sent to the server. This can be changed at runtime, and will work at all values from 3 to 50.
	 * A lower value means that more data will be sent per package, but it will also result in more latency. A higher value means less data per package,
	 * which is generally worse than more, heavy packages, but also gives less latency. A good value for this is between 5 and 10.
	 * @return The current amount of times that audio data is sent per second.
	 */
	public float getSendRate(){
		return this.sendRate;
	}
	
	private void createRecorder(){
		this.recorder = Gdx.audio.newAudioRecorder(this.getSampleRate(), true);
	}
	
	private void createPlayer(){
		this.player = Gdx.audio.newAudioDevice(this.getSampleRate(), true);
	}
	
	/**
	 * Gets the recommended value for the buffer size of clients and servers.
	 * Setting buffer size to this will ensure that the client and server always has a buffer large enough for the audio data.
	 * The return value is:
	 * <li> (Sample Rate / Send Rate) * 2
	 */
	public int getRecommendedBufferSize(){
		return (int) (this.getSampleRate() / (float)this.getSendRate() * 2f);
	}
	
	protected void registerNetObjects(Kryo kryo){
		kryo.register(short[].class);
		kryo.register(VoiceNetData.class);
	}
	
	/**
	 * Makes this chat client process and respond to audio sent from the server. If this message is not called, you will not hear anything
	 * from the server!
	 * @param client The client that audio data will be sent to from the server. Just use the normal client.
	 */
	public void addReceiver(Client client){
		
		if(this.player == null)
			this.createPlayer();
		
		client.addListener(new Listener(){
			public void received(Connection connection, Object object) {
				
				// Only read objects of the correct type.
				if(object instanceof VoiceNetData){
					
					// Read data
					VoiceNetData message = (VoiceNetData)object;					
					short[] data = message.getData();
					
					// Play audio
					processAudio(data, connection, message);
				}
			}			
		});
	}
	
	/**
	 * Plays audio received from the server.
	 * @param samples The samples of audio received.
	 * @param connection The connection to the server.
	 * @param message The message received.
	 */
	public void processAudio(short[] samples, Connection connection, VoiceNetData message){
		Thread thread = new Thread(() -> {
			short[] received = message.getData();
			player.writeSamples(received, 0, received.length);
		});
		thread.start();
	}
	
	@Deprecated
	public void addReceiver(Server server){
		
		if(this.player == null)
			this.createPlayer();
		AudioDevice player = this.player;
		
		server.addListener(new Listener(){
			public void received(Connection connection, Object object) {
				if(object instanceof VoiceNetData){
					
					// Read data
					VoiceNetData message = (VoiceNetData)object;					
					short[] data = message.getData();
					
					// Play audio
					Thread thread = new Thread(() -> {						
						player.writeSamples(data, 0, data.length);
					});
					thread.start();
				}
			}			
		});
	}
	
	/**
	 * Sends what you are saying to the other connections! This method should be called whenever the client wants to send audio, 
	 * such as when he/she presses a button. This method does not block at all, as recording audio is done in another thread.
	 * The minimum time recorded is equal to:
	 * <code>((SampleRate / SendRate) / SampleRate)</code> in seconds. The maximum time recorded is infinite.
	 * @param client The client to send the data on.
	 * @param delta The time, in seconds, between concurrent calls to this method.
	 * If this method is called 60 times per second, this value should be (1/60). In LibGDX, use <code>Gdx.graphics.getDeltaTime()</code>.
	 */
	public void sendVoice(Client client, float delta){
		
		float interval = 1f / this.getSendRate();
		timer += delta;
		if(timer >= interval){
			
			if(!ready){
				timer = interval; // Keep 'on-edge'
				return;
			}
			timer -= interval;
			
			// Make new thread
			ready = false;
			Thread thread = new Thread(() -> {				
				// Need to check if data needs sending. TODO		
				int packetSize = (int) (this.getSampleRate() / this.getSendRate());
				if(data == null){
					data = new short[packetSize];
				}
				
				// This will block! We need to do this in a separate thread!
				if(this.recorder == null) this.createRecorder();
				this.recorder.read(data, 0, packetSize);
				
				// Send to server, this will not block but may affect networking...
				client.sendUDP(new VoiceNetData(data));
				
				ready = true;
			});
			thread.start();			
		}		
	}	
	
	/**
	 * Disposes this voice chat client, which releases all resources but also makes this object unusable. 
	 * Any calls to methods after this will NOT work and will crash.
	 */
	public void dispose(){
		this.data = null;
		this.player.dispose();
		this.player = null;
		this.recorder.dispose();
		this.recorder = null;
	}
}
