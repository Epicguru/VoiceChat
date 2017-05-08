# Voice Chat by James Billy
A simple voice chat system using LibGDX and Kryonet.
## How to install
There is no gradle/maven dependency for this project for two reasons:
1. It is so simple and compact that it is honestly not worthwhile :D.
2. Handing it to you as source files allows you to edit, improve and customise the code for you needs.

Instead to install in your project, just copy-paste the two files located in the *co.uk.epicguru.classes* package. You will need to change the package
declaration for it to compile.
## How to use
I assume that you have a good understanding and control of Kryonet and Client/Server architechture.
This is the code necessary for the client side of things.
```java
public static void main(String[] args){
  
  // This buffer size is used when sending data in kryonet.
  // If you game crashes due to Kryonet/VoiceChat try making this value larger.
  int bufferSize = 1024;
  
  // Create client using the buffer
  Client client = new Client(bufferSize, bufferSize);
  
  // Here you would connect to the server
  // CONNECTION CODE GOES HERE
  
  // Create a voice chat client, that can send audio data to the server.
  VoceChatClient voiceClient = new VoiceChatClient(client.getKryo()); // We need to pass the Kryo object of our client.
  
}

public void update(VoiceChatClient voice, Client client){
  // This is some sort of update method that is called periodically in you app.
  
  // This variable is the time, in seconds, between the calls to update()
  // You may have to calculate this youself, but libraries such as LibGDX have a deltaTime value built in.
  float deltaTime = 1f / 60f; // Assumes that the update method is called exactly 60 times per second.
  
  if(sendAudio){
    // Sends audio data to the server.
    voice.update(client);
  }
}
```
