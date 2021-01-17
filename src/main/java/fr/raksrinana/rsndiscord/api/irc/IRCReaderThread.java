package fr.raksrinana.rsndiscord.api.irc;

import fr.raksrinana.rsndiscord.api.irc.messages.PingIRCMessage;
import fr.raksrinana.rsndiscord.api.irc.twitch.TwitchIRC;
import fr.raksrinana.rsndiscord.log.Log;
import org.jetbrains.annotations.NotNull;
import java.io.*;
import java.net.SocketTimeoutException;
import static java.util.Objects.nonNull;

public class IRCReaderThread extends Thread implements Closeable{
	public static final double MESSAGE_TIMEOUT = 6.048e8;
	
	private final BufferedReader reader;
	private final IRCClient client;
	private final IIRCMessageBuilder ircMessageBuilder;
	private boolean stop;
	
	public IRCReaderThread(@NotNull IRCClient client, @NotNull IIRCMessageBuilder ircMessageBuilder, @NotNull InputStream inputStream){
		this.client = client;
		this.ircMessageBuilder = ircMessageBuilder;
		reader = new BufferedReader(new InputStreamReader(inputStream));
		stop = false;
	}
	
	@Override
	public void run(){
		while(!stop){
			try{
				if(reader.ready()){
					String line;
					while(nonNull(line = reader.readLine())){
						processLine(line);
					}
				}
				else{
					try{
						Thread.sleep(500);
					}
					catch(InterruptedException e){
						Log.getLogger(null).error("Error while sleeping", e);
					}
				}
			}
			catch(SocketTimeoutException e){
				Log.getLogger(null).error("Socket timed out");
				client.timedOut();
			}
			catch(IOException e){
				Log.getLogger(null).error("Error reading stream", e);
			}
		}
	}
	
	private void processLine(@NotNull String line){
		try{
			ircMessageBuilder.buildEvent(line).ifPresent(event -> {
				if(event instanceof PingIRCMessage){
					Log.getLogger(null).debug("Replying to IRC ping message");
					client.sendMessage("PONG");
					var iterator = client.getListeners().iterator();
					while(iterator.hasNext()){
						var listener = iterator.next();
						if(listener.getLastMessage() > MESSAGE_TIMEOUT){
							iterator.remove();
							TwitchIRC.disconnect(listener.getGuild(), listener.getUser(), false);
						}
					}
				}
				Log.getLogger(null).debug("New IRC message of type {}", event.getClass().getSimpleName());
				for(var ircListener : client.getListeners()){
					ircListener.onIRCMessage(event);
				}
			});
		}
		catch(Exception e){
			Log.getLogger(null).error("Error handling IRC message: {}", line, e);
		}
	}
	
	@Override
	public void close() throws IOException{
		stop = true;
		reader.close();
	}
}
