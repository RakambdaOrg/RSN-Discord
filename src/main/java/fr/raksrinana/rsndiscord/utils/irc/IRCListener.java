package fr.raksrinana.rsndiscord.utils.irc;

import fr.raksrinana.rsndiscord.utils.irc.messages.IRCMessage;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
interface IRCListener{
	void onIRCMessage(@Nonnull IRCMessage event);
	
	boolean handlesChannel(@Nonnull String channel);
	
	@Nonnull
	Guild getGuild();
	
	@Nonnull
	String getIRCChannel();
	
	long getLastMessage();
	
	@Nonnull
	String getUser();
}
