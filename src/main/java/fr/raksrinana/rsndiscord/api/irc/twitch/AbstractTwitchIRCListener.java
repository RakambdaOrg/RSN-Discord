package fr.raksrinana.rsndiscord.api.irc.twitch;

import fr.raksrinana.rsndiscord.api.irc.IIRCListener;
import fr.raksrinana.rsndiscord.api.irc.messages.*;
import fr.raksrinana.rsndiscord.api.irc.twitch.messages.*;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractTwitchIRCListener implements IIRCListener{
	@Override
	public void onIRCMessage(@NotNull IIRCMessage event){
		if(event instanceof ChannelJoinIRCMessage){
			onIRCChannelJoined((ChannelJoinIRCMessage) event);
		}
		else if(event instanceof ChannelLeftIRCMessage){
			onIRCChannelLeft((ChannelLeftIRCMessage) event);
		}
		else if(event instanceof ChannelMessageIRCMessage){
			onIRCChannelMessage((ChannelMessageIRCMessage) event);
		}
		else if(event instanceof PingIRCMessage){
			onPingIRC((PingIRCMessage) event);
		}
		else if(event instanceof InfoMessageIRCMessage){
			onInfoMessage((InfoMessageIRCMessage) event);
		}
		else if(event instanceof UserNoticeIRCMessage){
			onUserNotice((UserNoticeIRCMessage) event);
		}
		else if(event instanceof ClearChatIRCMessage){
			onClearChat((ClearChatIRCMessage) event);
		}
		else if(event instanceof ClearMessageIRCMessage){
			onClearMessage((ClearMessageIRCMessage) event);
		}
		else if(event instanceof NoticeIRCMessage){
			onNotice((NoticeIRCMessage) event);
		}
		else if(event instanceof HostTargetIRCMessage){
			onHostTarget((HostTargetIRCMessage) event);
		}
		else{
			onIRCUnknownEvent(event);
		}
	}
	
	protected abstract void onIRCChannelJoined(@NotNull ChannelJoinIRCMessage event);
	
	protected abstract void onIRCChannelLeft(@NotNull ChannelLeftIRCMessage event);
	
	protected abstract void onIRCChannelMessage(@NotNull ChannelMessageIRCMessage event);
	
	protected abstract void onPingIRC(@NotNull PingIRCMessage event);
	
	protected abstract void onInfoMessage(@NotNull InfoMessageIRCMessage event);
	
	protected abstract void onUserNotice(@NotNull UserNoticeIRCMessage event);
	
	protected abstract void onClearChat(@NotNull ClearChatIRCMessage event);
	
	protected abstract void onClearMessage(@NotNull ClearMessageIRCMessage event);
	
	protected abstract void onNotice(@NotNull NoticeIRCMessage event);
	
	protected abstract void onHostTarget(@NotNull HostTargetIRCMessage event);
	
	protected abstract void onIRCUnknownEvent(@NotNull IIRCMessage event);
}
