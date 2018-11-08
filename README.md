# Voice Chat by James Billy
A simple voice chat system using Kryonet and LibGDX.

_If you are interested in C# or other voice chat projects, check out my voice chat client/server project [here](https://github.com/Epicguru/CChat2). It is more feature rich but is not a library, rather a standalone application._
## Info
You are allowed to use this mini-library in any way you wish. You may use it in any project, commercial or not, and feel free to edit the source code. Just please note that I put some time and effort into making this, so it would be great if you left this here so that people know that it was me that made this. Thanks. 
(Also if you are feeling generous or you appreciate what I have done, it would be great if you put me in the 'credits' section of you game or project.)
## How to install
There is no gradle/maven dependency for this project for two reasons:
1. It is so simple and compact that it is honestly not worthwhile :D.
2. Handing it to you as source files allows you to edit, improve and customise the code for you needs.

Instead to install in your project, just copy-paste the three files located **[here](https://github.com/Epicguru/VoiceChat/tree/master/Voice%20Chat/src/main/java/co/uk/epucguru/classes)**. You will need to change the package declaration for it to compile.
## How to use
I assume that you have a good understanding and control of Kryonet and Client/Server architechture. However here is an example of implementing the voice chat in both client and server side.
### Client Side
```java
public void start(){ 
  // A method called only once at the beginning of the the program.
  // In LibGDX it is called create().
  
  // This buffer size is used when sending data in kryonet.
  // If you game crashes due to Kryonet/VoiceChat try making this value larger.
  int bufferSize = 22050; // Recommended value.
  
  // Create KryoNet client using the buffer.
  Client client = new Client(bufferSize, bufferSize);
  
  // Here you would connect to the server.
  // CONNECTION CODE GOES HERE
  
  // Create a voice chat client, that can send audio data to the server.
  // We need to pass the Kryo object of our client.
  Kryo kryo = client.getKryo();
  VoceChatClient voiceClient = new VoiceChatClient(kryo);
  
  // Finally, allow the client to play audio recieved from the server.
  voiceClient.addReceiver(client);
}

public void update(VoiceChatClient voice, Client client){
  // This is some sort of update method that is called periodically in you app.
  // In LibGDX it is called render(). You know the drill.
  
  // This variable is the time, in seconds, between the calls to update().
  // LibGDX has done this for you!
  float deltaTime = Gdx.graphics.getDeltaTime();
  
  // This would be replaced with some sort of user input, such as pressing a button.
  boolean sendAudio = true;
  
  if(sendAudio){
    // Sends audio data to the server.
    voice.sendVoice(client, deltaTime);
  }
}
```

### Server Side
``` java
public void start(){
  // A method called only once at the beginning of the the program.
  // In LibGDX it is called create().
  
  // First determine buffer size.
  int bufferSize = 22050; // Recommened value.

  // Make a KryoNet server.
  Server server = new Server(bufferSize, bufferSize);
  
  // Now make voice chat server.
  // NOTE: This is not like a Teamspeak or a Discord style server, it is just a relay utility that 'bounces'
  //       voice chat back to clients.
  // We also need the Kryo object of the server.
  Kryo kryo = server.getKryo();
  VoiceChatServer voiceServer = new VoiceChatServer(kryo);
  
   // Now we need to enable the server to transmit audio data.
   server.addListener(new Listener(){
   	public void received(Connection connection, Object object) {
      // This 'bounces' back any audio data sent from clients.
		  relay.relayVoice(connection, object, server);
    }
   });
   
   // Done! No updating necessary for the server.
}
```

## Conclusion
Well I hope that the exmaples and implementation are clear enough. 
Please note that the examples above have not been compiled, and may contain mistakes. Kindly point them out so I can fix any, thanks!
The repository does also contain a crude testing project, which uses libgdx. You can run it but it is not user friendly at all. Soz.
