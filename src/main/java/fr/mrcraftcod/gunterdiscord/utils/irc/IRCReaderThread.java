package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.utils.irc.messages.PingIRCMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Objects;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
@SuppressWarnings("WeakerAccess")
public class IRCReaderThread extends Thread implements Closeable{
	private static final Logger LOGGER = LoggerFactory.getLogger(IRCReaderThread.class);
	public static final double MESSAGE_TIMEOUT = 6.048e8;
	private final BufferedReader reader;
	private final IRCClient client;
	private boolean stop;
	
	public IRCReaderThread(@Nonnull final IRCClient client, @Nonnull final InputStream inputStream){
		this.client = client;
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
		this.stop = false;
	}
	
	@Override
	public void run(){
		while(!this.stop){
			try{
				if(this.reader.ready()){
					String line;
					while(Objects.nonNull(line = this.reader.readLine())){
						try{
							IRCUtils.buildEvent(line).ifPresent(event -> {
								if(event instanceof PingIRCMessage){
									LOGGER.debug("Replying to IRC ping message");
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
								LOGGER.debug("New IRC message of type {}", event.getClass().getSimpleName());
								for(final var ircListener : this.client.getListeners()){
									ircListener.onIRCMessage(event);
								}
							});
						}
						catch(final Exception e){
							LOGGER.error("Error handling IRC message: {}", line, e);
						}
					}
				}
				else{
					try{
						Thread.sleep(500);
					}
					catch(final InterruptedException e){
						LOGGER.error("Error while sleeping", e);
					}
				}
			}
			catch(final SocketTimeoutException e){
				LOGGER.error("Socket timed out");
				this.client.timedOut();
			}
			catch(final IOException e){
				LOGGER.error("Error reading stream", e);
			}
		}
	}
	
	@Override
	public void close() throws IOException{
		this.stop = true;
		this.reader.close();
	}
}
