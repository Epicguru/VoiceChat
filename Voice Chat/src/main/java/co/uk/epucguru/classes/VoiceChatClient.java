package co.uk.epucguru.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class VoiceChatClient {

	private AudioRecorder recorder;
	private AudioDevice player;
	private int sampleRate = 22050; // Default and standard.
	private float sendRate = 20f;
	private short[] data;
	private float timer;
	private boolean ready = true;
	
	public VoiceChatClient(Kryo kryo, int sampleRate){
		this(kryo);
		
		this.sampleRate = sampleRate;
	}	
	
	public VoiceChatClient(Kryo kryo){
		this.registerNetObjects(kryo);
	}
	
	public int getSampleRate(){
		return this.sampleRate;
	}
	
	public float getSendRate(){
		return this.sendRate;
	}
	
	private void createRecorder(){
		this.recorder = Gdx.audio.newAudioRecorder(this.getSampleRate(), true);
	}
	
	private void createPlayer(){
		this.player = Gdx.audio.newAudioDevice(this.getSampleRate(), true);
	}
	
	protected void registerNetObjects(Kryo kryo){
		kryo.register(short[].class);
		kryo.register(VoiceNetData.class);
	}
	
	public void addReciever(Client client){
		
		if(this.player == null)
			this.createPlayer();
		AudioDevice player = this.player;
		
		client.addListener(new Listener(){
			public void received(Connection connection, Object object) {
				if(object instanceof VoiceNetData){
					
					// Read data
					VoiceNetData message = (VoiceNetData)object;					
					short[] data = message.getData();
					
					// Play audio
					player.writeSamples(data, 0, data.length);
				}
			}			
		});
	}
	
	public void addReciever(Server server){
		
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
					player.writeSamples(data, 0, data.length);
				}
			}			
		});
	}
	
	public void update(Client client, float delta){
		
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
				
				client.sendUDP(new VoiceNetData(data));
				
				ready = true;
			});
			thread.start();			
		}		
	}	
}
