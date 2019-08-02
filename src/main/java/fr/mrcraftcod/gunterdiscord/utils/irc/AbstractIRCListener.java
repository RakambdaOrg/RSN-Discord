package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.utils.irc.messages.*;
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
	public void onIRCMessage(@Nonnull final IRCMessage event){
		if(event instanceof ChannelJoinIRCMessage){
			this.onIRCChannelJoined((ChannelJoinIRCMessage) event);
		}
		else if(event instanceof ChannelLeftIRCMessage){
			this.onIRCChannelLeft((ChannelLeftIRCMessage) event);
		}
		else if(event instanceof ChannelMessageIRCMessage){
			this.onIRCChannelMessage((ChannelMessageIRCMessage) event);
		}
		else if(event instanceof PingIRCMessage){
			this.onPingIRC((PingIRCMessage) event);
		}
		else if(event instanceof InfoMessageIRCMessage){
			this.onInfoMessage((InfoMessageIRCMessage) event);
		}
		else if(event instanceof UserNoticeIRCMessage){
			this.onUserNotice((UserNoticeIRCMessage) event);
		}
		else{
			this.onIRCUnknownEvent(event);
		}
	}
	
	protected abstract void onIRCChannelJoined(@Nonnull ChannelJoinIRCMessage event);
	
	protected abstract void onIRCChannelLeft(@Nonnull ChannelLeftIRCMessage event);
	
	protected abstract void onIRCChannelMessage(@Nonnull ChannelMessageIRCMessage event);
	
	protected abstract void onPingIRC(@Nonnull PingIRCMessage event);
	
	protected abstract void onInfoMessage(InfoMessageIRCMessage event);
	
	protected abstract void onUserNotice(UserNoticeIRCMessage event);
	
	protected abstract void onIRCUnknownEvent(@Nonnull IRCMessage event);
}
