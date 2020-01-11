package fr.raksrinana.rsndiscord.utils.irc;

import fr.raksrinana.rsndiscord.utils.irc.messages.IRCMessage;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;

public interface IRCListener{
	void onIRCMessage(@NonNull IRCMessage event);
	
	boolean handlesChannel(@NonNull String channel);
	
	void timedOut();
	
	@NonNull Guild getGuild();
	
	@NonNull String getIrcChannel();
	
	long getLastMessage();
	
	@NonNull String getUser();
}
