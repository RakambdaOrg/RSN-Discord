package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.utils.irc.events.*;
import javax.annotation.Nonnull;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
@SuppressWarnings("EmptyMethod")
public abstract class AbstractIRCListener implements IRCListener{
	@Override
	public void onIRCEvent(@Nonnull final IRCEvent event){
		if(event instanceof ChannelJoinedIRCEvent){
			onIRCChannelJoined((ChannelJoinedIRCEvent) event);
		}
		else if(event instanceof ChannelLeftIRCEvent){
			onIRCChannelLeft((ChannelLeftIRCEvent) event);
		}
		else if(event instanceof ChannelMessageIRCEvent){
			onIRCMessage((ChannelMessageIRCEvent) event);
		}
		else if(event instanceof PingIRCEvent){
			onPingIRC((PingIRCEvent) event);
		}
		else{
			onIRCUnknownEvent(event);
		}
	}
	
	protected abstract void onIRCChannelJoined(@Nonnull ChannelJoinedIRCEvent event);
	
	protected abstract void onIRCChannelLeft(@Nonnull ChannelLeftIRCEvent event);
	
	protected abstract void onIRCMessage(@Nonnull ChannelMessageIRCEvent event);
	
	protected abstract void onPingIRC(@Nonnull PingIRCEvent event);
	
	protected abstract void onIRCUnknownEvent(@Nonnull IRCEvent event);
}
