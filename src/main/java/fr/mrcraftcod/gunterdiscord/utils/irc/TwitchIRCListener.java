package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.irc.messages.*;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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
	protected void onNotice(final NoticeIRCMessage event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Actions.sendMessage(this.channel, "__NOTICE__: %s", event.getMessage());
		}
	}
	
	@Override
	protected void onHostTarget(final HostTargetIRCMessage event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Log.getLogger(this.getGuild()).info("{} hosting {}", event.getChannel(), event.getInfos());
		}
	}
	
	@Override
	protected void onClearChat(final ClearChatIRCMessage event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Actions.sendMessage(this.channel, "__NOTICE__: %s banned for %s", event.getUser(), event.getTags().stream().filter(t -> Objects.equals("ban-duration", t.getKey())).map(IRCTag::getValue).map(Integer::parseInt).map(Duration::ofSeconds).map(Utilities::durationToString).findFirst().orElse("UNKNOWN"));
		}
	}
	
	@Override
	protected void onClearMessage(final ClearMessageIRCMessage event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Log.getLogger(this.getGuild()).info("Message from {} deleted: {}", event.getTags().stream().filter(t -> Objects.equals("login", t.getKey())).map(IRCTag::getValue).findFirst().orElse("UNKNOWN"), event.getMessage());
		}
	}
	
	@Override
	protected void onIRCChannelJoined(@Nonnull final ChannelJoinIRCMessage event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Log.getLogger(this.getGuild()).info("Joined {}", event.getChannel());
		}
	}
	
	@Override
	protected void onIRCChannelLeft(@Nonnull final ChannelLeftIRCMessage event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Log.getLogger(this.getGuild()).info("Left {}", event.getChannel());
		}
	}
	
	@Override
	protected void onIRCChannelMessage(@Nonnull final ChannelMessageIRCMessage event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			this.lastMessage = System.currentTimeMillis();
			var message = event.getMessage().replace("@everyone", "<everyone>").replace("@here", "<here>");
			if(message.chars().filter(Character::isUpperCase).sum() > 10){
				message = message.toLowerCase();
			}
			final var badges = event.getTags().stream().filter(t -> Objects.equals("badges", t.getKey())).map(IRCTag::getValue).flatMap(t -> Arrays.stream(t.split(",")).filter(v -> !v.isBlank()).map(v -> {
				final var split = v.split("/");
				return new TwitchBadge(split[0], split[1]);
			})).collect(Collectors.toList());
			final var displayName = event.getTags().stream().filter(t -> Objects.equals("display-name", t.getKey())).map(IRCTag::getValue).filter(Objects::nonNull).filter(t -> !t.isBlank()).findFirst().orElse(event.getUser().toString());
			var role = "";
			if(badges.stream().anyMatch(t -> Objects.equals("broadcaster", t.getName()))){
				role += "(boss)";
			}
			if(event.getTags().stream().anyMatch(t -> Objects.equals("moderator", t.getKey()))){
				role += "(mod)";
			}
			final var sub = badges.stream().filter(t -> Objects.equals("subscriber", t.getName())).findFirst();
			if(sub.isPresent()){
				role += "(sub/" + sub.get().getVersion() + ")";
			}
			final var subGifter = badges.stream().filter(t -> Objects.equals("sub-gifter", t.getName())).findFirst();
			if(subGifter.isPresent()){
				role += "(subgifter/" + subGifter.get().getVersion() + ")";
			}
			if(event.getTags().stream().anyMatch(t -> Objects.equals("partner", t.getKey()))){
				role += "(partner)";
			}
			Actions.sendMessage(this.channel, "**`%s`%s** %s", displayName, role, message);
		}
	}
	
	@Override
	protected void onPingIRC(@Nonnull final PingIRCMessage event){
	}
	
	@Override
	protected void onInfoMessage(final InfoMessageIRCMessage event){
		Log.getLogger(this.getGuild()).info("IRC Info: {}", event.getMessage());
	}
	
	@Override
	protected void onUserNotice(final UserNoticeIRCMessage event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Actions.sendMessage(this.channel, "__NOTICE__: %s", event.getTags().stream().filter(t -> Objects.equals("system-msg", t.getKey())).map(IRCTag::getValue).map(v -> v.replace("\\s", " ").trim()).findFirst().orElse("UNKNOWN"));
		}
	}
	
	@Override
	protected void onIRCUnknownEvent(@Nonnull final IRCMessage event){
	}
	
	@Override
	public boolean handlesChannel(@Nonnull final String channel){
		return Objects.equals(channel, this.ircChannel);
	}
	
	@Override
	public void onEvent(@Nonnull final GenericEvent event){
		if(event instanceof GuildMessageReceivedEvent){
			final var evt = (GuildMessageReceivedEvent) event;
			try{
				if(!Objects.equals(((GuildMessageReceivedEvent) event).getAuthor().getIdLong(), event.getJDA().getSelfUser().getIdLong()) && Objects.equals(evt.getChannel(), this.channel)){
					TwitchIRC.sendMessage(evt.getGuild(), this.ircChannel, ((GuildMessageReceivedEvent) event).getAuthor().getName() + " -> " + evt.getMessage().getContentRaw());
				}
			}
			catch(final Exception e){
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
