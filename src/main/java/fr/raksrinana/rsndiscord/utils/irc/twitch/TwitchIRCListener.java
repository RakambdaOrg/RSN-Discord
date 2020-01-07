package fr.raksrinana.rsndiscord.utils.irc.twitch;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.irc.AbstractIRCListener;
import fr.raksrinana.rsndiscord.utils.irc.IRCTag;
import fr.raksrinana.rsndiscord.utils.irc.messages.*;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Objects;

public class TwitchIRCListener extends AbstractIRCListener implements EventListener{
	@Getter
	private final Guild guild;
	@Getter
	private final String user;
	@Getter
	private final String ircChannel;
	private final TextChannel channel;
	private long lastMessage;
	
	TwitchIRCListener(@NonNull final Guild guild, @NonNull final String user, @NonNull final String channel){
		this.guild = guild;
		this.user = user;
		this.ircChannel = channel;
		this.lastMessage = System.currentTimeMillis();
		this.channel = Settings.get(guild).getTwitchChannel().orElseThrow().getChannel().orElseThrow();
	}
	
	@Override
	protected void onIRCChannelJoined(@NonNull final ChannelJoinIRCMessage event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Log.getLogger(this.getGuild()).info("Joined {}", event.getChannel());
		}
	}
	
	@Override
	protected void onIRCChannelLeft(@NonNull final ChannelLeftIRCMessage event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			Log.getLogger(this.getGuild()).info("Left {}", event.getChannel());
		}
	}
	
	@Override
	protected void onIRCChannelMessage(@NonNull final ChannelMessageIRCMessage event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			this.lastMessage = System.currentTimeMillis();
			final var twitchMessage = new TwitchChannelMessageIRCMessage(event);
			var message = twitchMessage.getParent().getMessage().replace("@everyone", "<everyone>").replace("@here", "<here>");
			if(message.chars().filter(Character::isUpperCase).sum() > 10){
				message = message.toLowerCase();
			}
			final var displayName = event.getTags().stream().filter(t -> Objects.equals("display-name", t.getKey())).map(IRCTag::getValue).filter(Objects::nonNull).filter(t -> !t.isBlank()).findFirst().orElse(event.getUser().toString());
			final var role = new StringBuilder();
			if(twitchMessage.isBroadcaster()){
				role.append("(boss)");
			}
			if(twitchMessage.isModerator()){
				role.append("(mod)");
			}
			twitchMessage.getSub().ifPresent(sub -> role.append("(sub/").append(sub.getVersion()).append(")"));
			twitchMessage.getSubGifter().ifPresent(subGifter -> role.append("(subgifter/").append(subGifter.getVersion()).append(")"));
			if(twitchMessage.isPartner()){
				role.append("(partner)");
			}
			Actions.sendMessage(this.channel, MessageFormat.format("**`{0}`{1}** {2}", displayName, role, message), null);
		}
	}
	
	@Override
	protected void onPingIRC(@NonNull final PingIRCMessage event){
	}
	
	@Override
	protected void onUserNotice(final UserNoticeIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), this.ircChannel)){
			Actions.sendMessage(this.channel, MessageFormat.format("__NOTICE__: {0}", event.getTags().stream().filter(t -> Objects.equals("system-msg", t.getKey())).map(IRCTag::getValue).map(v -> v.replace("\\s", " ").trim()).findFirst().orElse("UNKNOWN")), null);
		}
	}
	
	@Override
	protected void onClearChat(final ClearChatIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), this.ircChannel)){
			Actions.sendMessage(this.channel, MessageFormat.format("__NOTICE__: {0} banned for {1}", event.getUser(), event.getTags().stream().filter(t -> Objects.equals("ban-duration", t.getKey())).map(IRCTag::getValue).map(Integer::parseInt).map(Duration::ofSeconds).map(Utilities::durationToString).findFirst().orElse("UNKNOWN")), null);
		}
	}
	
	@Override
	protected void onClearMessage(final ClearMessageIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), this.ircChannel)){
			Log.getLogger(this.getGuild()).info("Message from {} deleted: {}", event.getTags().stream().filter(t -> Objects.equals("login", t.getKey())).map(IRCTag::getValue).findFirst().orElse("UNKNOWN"), event.getMessage());
		}
	}
	
	@Override
	protected void onNotice(final NoticeIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), this.ircChannel)){
			Actions.sendMessage(this.channel, MessageFormat.format("__NOTICE__: {0}", event.getMessage()), null);
		}
	}
	
	@Override
	protected void onInfoMessage(final InfoMessageIRCMessage event){
		Log.getLogger(this.getGuild()).info("IRC Info: {}", event.getMessage());
	}
	
	@Override
	protected void onHostTarget(final HostTargetIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), this.ircChannel)){
			Log.getLogger(this.getGuild()).info("{} hosting {}", event.getIrcChannel(), event.getInfos());
		}
	}
	
	@Override
	protected void onIRCUnknownEvent(@NonNull final IRCMessage event){
	}
	
	@Override
	public boolean handlesChannel(@NonNull final String channel){
		return Objects.equals(channel, this.ircChannel);
	}
	
	@Override
	public long getLastMessage(){
		return System.currentTimeMillis() - this.lastMessage;
	}
	
	@Override
	public void onEvent(@NonNull final GenericEvent event){
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
}
