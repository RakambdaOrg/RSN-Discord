package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.TwitchIRCChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.irc.events.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import javax.annotation.Nonnull;
import java.util.Objects;

public class TwitchIRCListener extends AbstractIRCListener{
	private final Guild guild;
	private final String user;
	private final String ircChannel;
	private final TextChannel channel;
	private long lastMessage;
	
	TwitchIRCListener(@Nonnull final Guild guild, @Nonnull final String user, @Nonnull final String channel) throws NoValueDefinedException{
		this.guild = guild;
		this.user = user;
		this.ircChannel = channel;
		this.lastMessage = System.currentTimeMillis();
		final var config = new TwitchIRCChannelConfig(this.getGuild());
		this.channel = config.getObject().orElseThrow(() -> new NoValueDefinedException(config));
	}
	
	@Override
	protected void onIRCChannelJoined(@Nonnull final ChannelJoinedIRCEvent event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Actions.sendMessage(this.channel, "Joined %s", event.getChannel());
		}
	}
	
	@Override
	protected void onIRCChannelLeft(@Nonnull final ChannelLeftIRCEvent event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Actions.sendMessage(this.channel, "Left %s", event.getChannel());
		}
	}
	
	@Override
	protected void onIRCMessage(@Nonnull final ChannelMessageIRCEvent event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			this.lastMessage = System.currentTimeMillis();
			Actions.sendMessage(this.channel, "**`%s`** %s", event.getUser().toString(), event.getMessage());
		}
	}
	
	@Override
	protected void onPingIRC(@Nonnull final PingIRCEvent event){
	}
	
	@Override
	protected void onIRCUnknownEvent(@Nonnull final IRCEvent event){
	}
	
	@Override
	public boolean handlesChannel(@Nonnull final String channel){
		return Objects.equals(channel, this.ircChannel);
	}
	
	@Nonnull
	@Override
	public Guild getGuild(){
		return this.guild;
	}
	
	@Nonnull
	@Override
	public String getIRCChannel(){
		return this.ircChannel;
	}
	
	@Override
	public long getLastMessage(){
		return System.currentTimeMillis() - this.lastMessage;
	}
	
	@Nonnull
	@Override
	public String getUser(){
		return this.user;
	}
}
