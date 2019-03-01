package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.TwitchIRCChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.irc.events.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

public class TwitchIRCListener extends AbstractIRCListener{
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitchIRCListener.class);
	private final Guild guild;
	private final String user;
	private final String ircChannel;
	private final TextChannel channel;
	private long lastMessage;
	
	public TwitchIRCListener(Guild guild, String user, String channel) throws NoValueDefinedException{
		this.guild = guild;
		this.user = user;
		this.ircChannel = channel;
		this.lastMessage = System.currentTimeMillis();
		this.channel = new TwitchIRCChannelConfig(this.getGuild()).getObject();
	}
	
	@Override
	protected void onPingIRC(PingIRCEvent event){
		if(System.currentTimeMillis() - lastMessage > 300000){
			TwitchIRC.disconnect(getGuild(), getUser());
		}
	}
	
	@Override
	protected void onIRCChannelJoined(ChannelJoinedIRCEvent event){
		if(Objects.equals(event.getChannel(), ircChannel)){
			Actions.sendMessage(channel, "Joined %s", event.getChannel());
		}
	}
	
	@Override
	protected void onIRCChannelLeft(ChannelLeftIRCEvent event){
		if(Objects.equals(event.getChannel(), ircChannel)){
			Actions.sendMessage(channel, "Left %s", event.getChannel());
		}
	}
	
	@Override
	protected void onIRCMessage(ChannelMessageIRCEvent event){
		if(Objects.equals(event.getChannel(), ircChannel)){
			lastMessage = System.currentTimeMillis();
			Actions.sendMessage(channel, "**`%s`** %s", event.getUser().toString(), event.getMessage());
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
