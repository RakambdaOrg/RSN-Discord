package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.utils.irc.events.PingIRCEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.Objects;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
public class IRCReaderThread extends Thread implements Closeable{
	private static final Logger LOGGER = LoggerFactory.getLogger(IRCReaderThread.class);
	private final BufferedReader reader;
	private final IRCClient client;
	private boolean stop;
	
	public IRCReaderThread(IRCClient client, InputStream inputStream){
		this.client = client;
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
		this.stop = false;
	}
	
	@Override
	public void run(){
		while(!stop){
			try{
				if(reader.ready()){
					final var line = reader.readLine();
					LOGGER.debug("{}", line);
					try{
						final var event = IRCUtils.buildEvent(line);
						if(event instanceof PingIRCEvent){
							client.sendMessage("PONG");
							LOGGER.debug("PP");
						}
						else if(Objects.nonNull(event)){
							LOGGER.debug("LL");
							client.getListeners().forEach(l -> l.onIRCEvent(event));
						}
					}
					catch(Exception e){
						LOGGER.error("Error handling IRC message: {}", line, e);
					}
				}
				else{
					try{
						Thread.sleep(500);
					}
					catch(InterruptedException e){
						LOGGER.error("Error while sleeping", e);
					}
				}
			}
			catch(IOException e){
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
