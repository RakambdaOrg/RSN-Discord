package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.settings.configs.TwitchIRCChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.irc.events.ChannelJoinedIRCEvent;
import fr.mrcraftcod.gunterdiscord.utils.irc.events.ChannelMessageIRCEvent;
import fr.mrcraftcod.gunterdiscord.utils.irc.events.IRCEvent;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

public class TwitchIRCListener extends AbstractIRCListener{
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitchIRCListener.class);
	private final Guild guild;
	private final String user;
	private final String ircChannel;
	
	public TwitchIRCListener(Guild guild, String user, String channel){
		this.guild = guild;
		this.user = user;
		this.ircChannel = channel;
	}
	
	@Override
	protected void onIRCChannelJoined(ChannelJoinedIRCEvent event){
	
	}
	
	@Override
	protected void onIRCMessage(ChannelMessageIRCEvent event){
		final var channel = new TwitchIRCChannelConfig(this.getGuild()).getObject(null);
		if(Objects.nonNull(channel)){
			Actions.sendMessage(channel, "%s => %s", event.getUser().toString(), event.getMessage());
		}
	}
	
	@Override
	protected void onIRCUnknownEvent(IRCEvent event){
	
	}
	
	public Guild getGuild(){
		return this.guild;
	}
	
	@Override
	public boolean handlesChannel(String channel){
		return Objects.equals(channel, this.ircChannel);
	}
	
	public String getUser(){
		return user;
	}
}
