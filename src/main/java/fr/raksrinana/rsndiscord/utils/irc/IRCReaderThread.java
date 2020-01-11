package fr.raksrinana.rsndiscord.utils.irc;

import fr.raksrinana.rsndiscord.utils.irc.messages.PingIRCMessage;
import fr.raksrinana.rsndiscord.utils.irc.twitch.TwitchIRC;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Objects;

public class IRCReaderThread extends Thread implements Closeable{
	public static final double MESSAGE_TIMEOUT = 6.048e8;
	private final BufferedReader reader;
	private final IRCClient client;
	private boolean stop;
	private final IIRCMessageBuilder ircMessageBuilder;
	
	public IRCReaderThread(@NonNull final IRCClient client, @NonNull final IIRCMessageBuilder ircMessageBuilder, @NonNull final InputStream inputStream){
		this.client = client;
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
		this.stop = false;
		this.ircMessageBuilder = ircMessageBuilder;
	}
	
	@Override
	public void run(){
		while(!this.stop){
			try{
				if(this.reader.ready()){
					String line;
					while(Objects.nonNull(line = this.reader.readLine())){
						processLine(line);
					}
				}
				else{
					try{
						Thread.sleep(500);
					}
					catch(final InterruptedException e){
						Log.getLogger(null).error("Error while sleeping", e);
					}
				}
			}
			catch(final SocketTimeoutException e){
				Log.getLogger(null).error("Socket timed out");
				this.client.timedOut();
			}
			catch(final IOException e){
				Log.getLogger(null).error("Error reading stream", e);
			}
		}
	}
	
	private void processLine(@NonNull String line){
		try{
			ircMessageBuilder.buildEvent(line).ifPresent(event -> {
				if(event instanceof PingIRCMessage){
					Log.getLogger(null).debug("Replying to IRC ping message");
					this.client.sendMessage("PONG");
					final var iterator = this.client.getListeners().iterator();
					while(iterator.hasNext()){
						final var listener = iterator.next();
						if(listener.getLastMessage() > MESSAGE_TIMEOUT){
							iterator.remove();
							TwitchIRC.disconnect(listener.getGuild(), listener.getUser(), false);
						}
					}
				}
				Log.getLogger(null).debug("New IRC message of type {}", event.getClass().getSimpleName());
				for(final var ircListener : this.client.getListeners()){
					ircListener.onIRCMessage(event);
				}
			});
		}
		catch(final Exception e){
			Log.getLogger(null).error("Error handling IRC message: {}", line, e);
		}
	}
	
	@Override
	public void close() throws IOException{
		this.stop = true;
		this.reader.close();
	}
}
