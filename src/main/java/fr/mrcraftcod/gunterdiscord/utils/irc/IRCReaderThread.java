package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.utils.irc.events.PingIRCEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.io.*;
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
								if(event instanceof PingIRCEvent){
									LOGGER.debug("Replying to IRC ping message");
									this.client.sendMessage("PONG");
									final var iterator = this.client.getListeners().iterator();
									while(iterator.hasNext()){
										final var listener = iterator.next();
										if(listener.getLastMessage() > 1.8e6){
											iterator.remove();
											TwitchIRC.disconnect(listener.getGuild(), listener.getUser(), false);
										}
									}
								}
								for(final var ircListener : this.client.getListeners()){
									ircListener.onIRCEvent(event);
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
