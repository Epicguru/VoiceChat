package co.uk.epucguru.classes;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class VoiceChatServer {
	
	public VoiceChatServer(Kryo kryo){
		this.registerClasses(kryo);
	}
			
	protected void registerClasses(Kryo kryo){	
		kryo.register(short[].class);
		kryo.register(VoiceNetData.class);
	}
	
	public boolean relayVoice(Connection connection, Object message, Server server){
		
		if(message instanceof VoiceNetData){
			
			server.sendToAllExceptUDP(connection.getID(), message);
			
			return true;
		}else{
			return false;
		}		
	}
	
}
