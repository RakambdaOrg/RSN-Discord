package fr.raksrinana.rsndiscord.modules.irc.twitch;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.RandomKick;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.irc.messages.*;
import fr.raksrinana.rsndiscord.modules.irc.twitch.messages.*;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class TwitchIRCListener extends AbstractTwitchIRCListener implements EventListener{
	private static final Set<TwitchMessageId> ACCEPTED_NOTICES = Set.of(
			TwitchMessageId.FOLLOWERS_OFF,
			TwitchMessageId.FOLLOWERS_ON,
			TwitchMessageId.FOLLOWERS_ONZERO,
			TwitchMessageId.MSG_BANNED,
			TwitchMessageId.MSG_BAD_CHARACTERS,
			TwitchMessageId.MSG_CHANNEL_BLOCKED,
			TwitchMessageId.MSG_CHANNEL_SUSPENDED,
			TwitchMessageId.MSG_DUPLICATE,
			TwitchMessageId.MSG_FOLLOWERSONLY,
			TwitchMessageId.MSG_FOLLOWERSONLY_ZERO,
			TwitchMessageId.MSG_R9K,
			TwitchMessageId.MSG_RATELIMIT,
			TwitchMessageId.MSG_REJECTED,
			TwitchMessageId.MSG_REJECTED_MANDATORY,
			TwitchMessageId.MSG_ROOM_NOT_FOUND,
			TwitchMessageId.MSG_SLOWMODE,
			TwitchMessageId.MSG_SUBSONLY,
			TwitchMessageId.MSG_SUSPENDED,
			TwitchMessageId.MSG_TIMEDOUT,
			TwitchMessageId.R9K_OFF,
			TwitchMessageId.R9K_ON,
			TwitchMessageId.SLOW_OFF,
			TwitchMessageId.SLOW_ON,
			TwitchMessageId.SUBS_OFF,
			TwitchMessageId.SUBS_ON,
			TwitchMessageId.TOS_BAN,
			TwitchMessageId.WHISPER_BANNED,
			TwitchMessageId.WHISPER_BANNED_RECIPIENT,
			TwitchMessageId.WHISPER_LIMIT_PER_MIN,
			TwitchMessageId.WHISPER_LIMIT_PER_SEC,
			TwitchMessageId.WHISPER_RESTRICTED,
			TwitchMessageId.WHISPER_RESTRICTED_RECIPIENT
	);
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
		this.channel = Settings.get(guild).getTwitchConfiguration().getTwitchChannel().orElseThrow().getChannel().orElseThrow();
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
			event.getCustomRewardId().flatMap(rewardId -> Settings.get(getGuild())
					.getTwitchConfiguration()
					.getRandomKickRewardId()
					.filter(expectedId -> Objects.equals(rewardId, expectedId)))
					.flatMap(expectedId -> Settings.get(getGuild()).getGeneralChannel()
							.flatMap(ChannelConfiguration::getChannel))
					.ifPresent(channel -> {
						var pings = Settings.get(getGuild()).getRandomKick()
								.getRandomKickRolesPing()
								.stream()
								.flatMap(pingRole -> pingRole.getRole().stream())
								.collect(Collectors.toSet());
						var pingsStr = pings.stream().map(Role::getAsMention)
								.collect(Collectors.joining(" "));
						var targetRole = RandomKick.getRandomRole(getGuild());
						Actions.sendMessage(channel,
								pingsStr + " => " + translate(getGuild(), "random-kick.bought", event.getUser().getNick(), targetRole.map(Role::getAsMention).orElse("@everyone")),
								null,
								false,
								messageAction -> messageAction.allowedMentions(List.of(Message.MentionType.ROLE))
										.mention(pings)
						).thenAcceptAsync(message1 -> Main.getExecutorService().schedule(() -> RandomKick.randomKick(channel,
								targetRole.orElse(null),
								translate(getGuild(), "random-kick.bought-reason", event.getUser().getNick(), getUser(), event.getMessage()),
								false), 30, TimeUnit.SECONDS));
					});
		}
	}
	
	@Override
	protected void onPingIRC(@NonNull final PingIRCMessage event){
	}
	
	@Override
	protected void onInfoMessage(final InfoMessageIRCMessage event){
		Log.getLogger(this.getGuild()).info("IRC Info: {}", event.getMessage());
	}
	
	@Override
	protected void onUserNotice(final UserNoticeIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), this.ircChannel)){
			Actions.sendMessage(this.channel, translate(channel.getGuild(), "irc.notice", event.getTags().stream().filter(t -> Objects.equals("system-msg", t.getKey())).map(IRCTag::getValue).map(v -> v.replace("\\s", " ").trim()).findFirst().orElse("UNKNOWN")), null);
		}
	}
	
	@Override
	protected void onClearChat(final ClearChatIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), this.ircChannel)){
			Actions.sendMessage(this.channel, translate(channel.getGuild(), "irc.banned", event.getUser(), event.getTags().stream().filter(t -> Objects.equals("ban-duration", t.getKey())).map(IRCTag::getValue).map(Integer::parseInt).map(Duration::ofSeconds).map(Utilities::durationToString).findFirst().orElse("UNKNOWN")), null);
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
			if(ACCEPTED_NOTICES.contains(event.getMessageId().orElse(TwitchMessageId.UNKNOWN))){
				Actions.sendMessage(this.channel, translate(channel.getGuild(), "irc.notice", event.getMessage()), null);
			}
			else{
				Log.getLogger(this.getGuild()).debug("Unhandled notice: {}", event.getMessage());
			}
		}
	}
	
	@Override
	protected void onHostTarget(final HostTargetIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), this.ircChannel)){
			Log.getLogger(this.getGuild()).info("{} hosting {}", event.getIrcChannel(), event.getInfos());
		}
	}
	
	@Override
	protected void onIRCUnknownEvent(@NonNull final IIRCMessage event){
	}
	
	@Override
	public boolean handlesChannel(@NonNull final String channel){
		return Objects.equals(channel, this.ircChannel);
	}
	
	@Override
	public void timedOut(){
		TwitchIRC.disconnect(getGuild(), getUser());
		try{
			TwitchIRC.connect(getGuild(), getUser());
		}
		catch(final IOException e){
			Log.getLogger(null).warn("Failed to reconnect {} to user {}", getGuild(), getUser());
		}
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
				if(!evt.getAuthor().isBot() && Objects.equals(evt.getChannel(), this.channel)){
					TwitchIRC.sendMessage(evt.getGuild(), this.ircChannel, evt.getAuthor().getName() + " -> " + evt.getMessage().getContentRaw());
				}
			}
			catch(final Exception e){
				Log.getLogger(evt.getGuild()).error("Failed to transfer message", e);
			}
		}
	}
}
