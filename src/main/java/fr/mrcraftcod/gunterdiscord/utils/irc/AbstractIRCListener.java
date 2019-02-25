package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.utils.irc.events.ChannelJoinedIRCEvent;
import fr.mrcraftcod.gunterdiscord.utils.irc.events.ChannelLeftIRCEvent;
import fr.mrcraftcod.gunterdiscord.utils.irc.events.ChannelMessageIRCEvent;
import fr.mrcraftcod.gunterdiscord.utils.irc.events.IRCEvent;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
public abstract class AbstractIRCListener implements IRCListener{
	@Override
	public void onIRCEvent(IRCEvent event){
		if(event instanceof ChannelJoinedIRCEvent){
			onIRCChannelJoined((ChannelJoinedIRCEvent) event);
		}
		else if(event instanceof ChannelLeftIRCEvent){
			onIRCChannelLeft((ChannelLeftIRCEvent) event);
		}
		else if(event instanceof ChannelMessageIRCEvent){
			onIRCMessage((ChannelMessageIRCEvent) event);
		}
		else{
			onIRCUnknownEvent(event);
		}
	}
	
	protected abstract void onIRCChannelJoined(ChannelJoinedIRCEvent event);
	
	protected abstract void onIRCChannelLeft(ChannelLeftIRCEvent event);
	
	protected abstract void onIRCMessage(ChannelMessageIRCEvent event);
	
	protected abstract void onIRCUnknownEvent(IRCEvent event);
}
