package co.uk.epucguru.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioRecorder;
import com.esotericsoftware.kryo.Kryo;

public class VoiceChatClient {

	private AudioRecorder recorder;
	private int sampleRate = 22050; // Default and standard.
	private float sendRate = 5f;
	
	public VoiceChatClient(Kryo kryo, int sampleRate){
		this(kryo);
		
		this.sampleRate = sampleRate;
	}	
	
	public VoiceChatClient(Kryo kryo){
		this.registerNetObjects(kryo);
		this.createRecorder();
	}
	
	public int getSampleRate(){
		return this.sampleRate;
	}
	
	private void createRecorder(){
		this.recorder = Gdx.audio.newAudioRecorder(this.getSampleRate(), true);
	}
	
	protected void registerNetObjects(Kryo kryo){
		kryo.register(short[].class);
		kryo.register(VoiceNetData.class);
	}
	
	public void update(){
		
		// We will send 
		
	}
	
}
