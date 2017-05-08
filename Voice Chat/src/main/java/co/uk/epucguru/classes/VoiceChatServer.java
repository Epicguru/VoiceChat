package co.uk.epucguru.classes;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

/**
 * A class that is used receive and bounce back audio from client. Works using KryoNet and LibGDX.
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
public class VoiceChatServer {
	
	/**
	 * Creates a new {@link #VoiceChatServer}. This is NOT a standalone server like {@link Server} or like a Teamspeak server.
	 * This is simple a utility class for an existing KryoNet server.
	 * @param kryo The Kryo object from a server.
	 */
	public VoiceChatServer(Kryo kryo){
		this.registerClasses(kryo);
	}
			
	protected void registerClasses(Kryo kryo){	
		kryo.register(short[].class);
		kryo.register(VoiceNetData.class);
	}
	
	/**
	 * Bounces back any audio received to all clients except the one that sent it. By overriding this you can make muting systems, or filter voice chat.
	 * Please note that this will only bounce audio back and will not have any effect on other messages. See the return value for more info.
	 * @param connection The connection that sent the data.
	 * @param message The data that they sent.
	 * @param server The server to send audio to clients on.
	 * @return True if any data was relayed, false if the data was not audio.
	 */
	public boolean relayVoice(Connection connection, Object message, Server server){
		
		if(message instanceof VoiceNetData){
			
			server.sendToAllExceptUDP(connection.getID(), message);
			
			return true;
		}else{
			return false;
		}		
	}
	
}
