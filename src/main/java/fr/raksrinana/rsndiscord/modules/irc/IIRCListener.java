package fr.raksrinana.rsndiscord.modules.irc;

import fr.raksrinana.rsndiscord.modules.irc.messages.IIRCMessage;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;

public interface IIRCListener{
	void onIRCMessage(@NonNull IIRCMessage event);
	
	boolean handlesChannel(@NonNull String channel);
	
	void timedOut();
	
	@NonNull Guild getGuild();
	
	@NonNull String getIrcChannel();
	
	long getLastMessage();
	
	@NonNull String getUser();
}
