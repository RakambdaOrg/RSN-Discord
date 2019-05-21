package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.utils.irc.events.IRCEvent;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
public interface IRCListener{
	void onIRCEvent(IRCEvent event);
	
	boolean handlesChannel(String channel);
	
	Guild getGuild();
	
	String getIRCChannel();
	
	long getLastMessage();
	
	String getUser();
}
