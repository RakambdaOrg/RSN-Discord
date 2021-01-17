package fr.raksrinana.rsndiscord.api.irc;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

public interface IIRCListener{
	void onIRCMessage(@NotNull IIRCMessage event);
	
	boolean handlesChannel(@NotNull String channel);
	
	void timedOut();
	
	@NotNull
	Guild getGuild();
	
	@NotNull
	String getIrcChannel();
	
	long getLastMessage();
	
	@NotNull
	String getUser();
}
