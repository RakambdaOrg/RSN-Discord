package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.TwitchIRCChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.irc.events.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import java.util.Objects;

public class TwitchIRCListener extends AbstractIRCListener{
	private final Guild guild;
	private final String user;
	private final String ircChannel;
	private final TextChannel channel;
	private long lastMessage;
	
	TwitchIRCListener(final Guild guild, final String user, final String channel) throws NoValueDefinedException{
		this.guild = guild;
		this.user = user;
		this.ircChannel = channel;
		this.lastMessage = System.currentTimeMillis();
		this.channel = new TwitchIRCChannelConfig(this.getGuild()).getObject();
	}
	
	@Override
	protected void onPingIRC(final PingIRCEvent event){
	}
	
	@Override
	protected void onIRCChannelJoined(final ChannelJoinedIRCEvent event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Actions.sendMessage(this.channel, "Joined %s", event.getChannel());
		}
	}
	
	@Override
	protected void onIRCChannelLeft(final ChannelLeftIRCEvent event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Actions.sendMessage(this.channel, "Left %s", event.getChannel());
		}
	}
	
	@Override
	protected void onIRCMessage(final ChannelMessageIRCEvent event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			this.lastMessage = System.currentTimeMillis();
			Actions.sendMessage(this.channel, "**`%s`** %s", event.getUser().toString(), event.getMessage());
		}
	}
	
	@Override
	protected void onIRCUnknownEvent(final IRCEvent event){
	
	}
	
	@Override
	public Guild getGuild(){
		return this.guild;
	}
	
	@Override
	public String getIRCChannel(){
		return this.ircChannel;
	}
	
	@Override
	public boolean handlesChannel(final String channel){
		return Objects.equals(channel, this.ircChannel);
	}
	
	@Override
	public long getLastMessage(){
		return System.currentTimeMillis() - this.lastMessage;
	}
	
	@Override
	public String getUser(){
		return this.user;
	}
}
