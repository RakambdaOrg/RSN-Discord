package fr.raksrinana.rsndiscord.utils.irc.twitch;

import fr.raksrinana.rsndiscord.utils.irc.IRCListener;
import fr.raksrinana.rsndiscord.utils.irc.messages.*;
import fr.raksrinana.rsndiscord.utils.irc.twitch.messages.*;
import lombok.NonNull;

public abstract class AbstractTwitchIRCListener implements IRCListener{
	@Override
	public void onIRCMessage(@NonNull final IRCMessage event){
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
		else if(event instanceof ClearChatIRCMessage){
			this.onClearChat((ClearChatIRCMessage) event);
		}
		else if(event instanceof ClearMessageIRCMessage){
			this.onClearMessage((ClearMessageIRCMessage) event);
		}
		else if(event instanceof NoticeIRCMessage){
			this.onNotice((NoticeIRCMessage) event);
		}
		else if(event instanceof HostTargetIRCMessage){
			this.onHostTarget((HostTargetIRCMessage) event);
		}
		else{
			this.onIRCUnknownEvent(event);
		}
	}
	
	protected abstract void onIRCChannelJoined(@NonNull ChannelJoinIRCMessage event);
	
	protected abstract void onIRCChannelLeft(@NonNull ChannelLeftIRCMessage event);
	
	protected abstract void onIRCChannelMessage(@NonNull ChannelMessageIRCMessage event);
	
	protected abstract void onPingIRC(@NonNull PingIRCMessage event);
	
	protected abstract void onInfoMessage(InfoMessageIRCMessage event);
	
	protected abstract void onUserNotice(UserNoticeIRCMessage event);
	
	protected abstract void onClearChat(ClearChatIRCMessage event);
	
	protected abstract void onClearMessage(ClearMessageIRCMessage event);
	
	protected abstract void onNotice(NoticeIRCMessage event);
	
	protected abstract void onHostTarget(HostTargetIRCMessage event);
	
	protected abstract void onIRCUnknownEvent(@NonNull IRCMessage event);
}
