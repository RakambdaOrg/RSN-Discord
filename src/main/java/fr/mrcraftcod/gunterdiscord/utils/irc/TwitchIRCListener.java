package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.irc.events.*;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import javax.annotation.Nonnull;
import java.util.Objects;

public class TwitchIRCListener extends AbstractIRCListener implements EventListener{
	private final Guild guild;
	private final String user;
	private final String ircChannel;
	private final TextChannel channel;
	private long lastMessage;
	
	TwitchIRCListener(@Nonnull final Guild guild, @Nonnull final String user, @Nonnull final String channel){
		this.guild = guild;
		this.user = user;
		this.ircChannel = channel;
		this.lastMessage = System.currentTimeMillis();
		this.channel = NewSettings.getConfiguration(guild).getTwitchChannel().orElseThrow().getChannel().orElseThrow();
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
	
	@Override
	public void onEvent(@Nonnull GenericEvent event){
		if(event instanceof GuildMessageReceivedEvent){
			final var evt = (GuildMessageReceivedEvent) event;
			try{
				if(!Objects.equals(((GuildMessageReceivedEvent) event).getAuthor().getIdLong(), event.getJDA().getSelfUser().getIdLong()) && Objects.equals(evt.getChannel(), this.channel)){
					TwitchIRC.sendMessage(evt.getGuild(), this.ircChannel, ((GuildMessageReceivedEvent) event).getAuthor().getName() + " -> " + evt.getMessage().getContentRaw());
				}
			}
			catch(Exception e){
				Log.getLogger(evt.getGuild()).error("Failed to transfer message", e);
			}
		}
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
