package fr.raksrinana.rsndiscord.modules.irc.twitch;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.RandomKick;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.irc.messages.*;
import fr.raksrinana.rsndiscord.modules.irc.twitch.messages.*;
import fr.raksrinana.rsndiscord.modules.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.modules.music.TrackConsumer;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.modules.irc.twitch.TwitchMessageId.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

public class TwitchIRCListener extends AbstractTwitchIRCListener implements EventListener{
	private static final Set<TwitchMessageId> ACCEPTED_NOTICES = Set.of(
			FOLLOWERS_OFF,
			FOLLOWERS_ON,
			FOLLOWERS_ONZERO,
			MSG_BAD_CHARACTERS,
			MSG_BANNED,
			MSG_CHANNEL_BLOCKED,
			MSG_CHANNEL_SUSPENDED,
			MSG_DUPLICATE,
			MSG_FOLLOWERSONLY,
			MSG_FOLLOWERSONLY_ZERO,
			MSG_R9K,
			MSG_RATELIMIT,
			MSG_REJECTED,
			MSG_REJECTED_MANDATORY,
			MSG_ROOM_NOT_FOUND,
			MSG_SLOWMODE,
			MSG_SUBSONLY,
			MSG_SUSPENDED,
			MSG_TIMEDOUT,
			R9K_OFF,
			R9K_ON,
			SLOW_OFF,
			SLOW_ON,
			SUBS_OFF,
			SUBS_ON,
			TOS_BAN,
			WHISPER_BANNED,
			WHISPER_BANNED_RECIPIENT,
			WHISPER_LIMIT_PER_MIN,
			WHISPER_LIMIT_PER_SEC,
			WHISPER_RESTRICTED,
			WHISPER_RESTRICTED_RECIPIENT
	);
	@Getter
	private final Guild guild;
	@Getter
	private final String user;
	@Getter
	private final String ircChannel;
	private final TextChannel channel;
	private long lastMessage;
	private Long musicChannelId;
	
	TwitchIRCListener(@NonNull final Guild guild, @NonNull final String user, @NonNull final String channel){
		this.guild = guild;
		this.user = user;
		this.ircChannel = channel;
		this.lastMessage = System.currentTimeMillis();
		this.channel = Settings.get(guild).getTwitchConfiguration().getTwitchChannel().orElseThrow().getChannel().orElseThrow();
	}
	
	@Override
	protected void onIRCChannelJoined(@NonNull final ChannelJoinIRCMessage event){
		var channel = event.getChannel();
		if(Objects.equals(channel, this.ircChannel)){
			Log.getLogger(this.getGuild()).info("Joined {}", channel);
		}
	}
	
	@Override
	protected void onIRCChannelLeft(@NonNull final ChannelLeftIRCMessage event){
		var channel = event.getChannel();
		if(Objects.equals(channel, this.ircChannel)){
			Log.getLogger(this.getGuild()).info("Left {}", channel);
		}
	}
	
	@Override
	protected void onIRCChannelMessage(@NonNull final ChannelMessageIRCMessage event){
		if(Objects.equals(event.getChannel(), this.ircChannel)){
			this.lastMessage = System.currentTimeMillis();
			var twitchMessage = new TwitchChannelMessageIRCMessage(event);
			
			var displayName = event.getTag("display-name")
					.map(IRCTag::getValue)
					.filter(t -> !t.isBlank())
					.orElse(event.getUser().toString());
			
			var role = new StringBuilder();
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
			
			this.channel.sendMessage(MessageFormat.format("{0} : **`{1}`{2}** {3}", twitchMessage.getParent().getChannel(), displayName, role, event.getMessage()))
					.allowedMentions(List.of())
					.submit();
			
			event.getCustomRewardId().flatMap(rewardId -> Settings.get(getGuild())
					.getTwitchConfiguration()
					.getRandomKickRewardId()
					.filter(expectedId -> Objects.equals(rewardId, expectedId)))
					.ifPresent(expectedId -> randomKickClaimed(event));
			
			if(twitchMessage.isBroadcaster()){
				if(event.getMessage().startsWith("?ron")){
					var id = event.getMessage().split(" ", 2)[1];
					musicChannelId = Long.parseLong(id);
				}
				else if(event.getMessage().startsWith("?roff")){
					musicChannelId = null;
				}
			}
			if(event.getMessage().startsWith("?request")){
				var link = event.getMessage().split(" ", 2)[1];
				Optional.ofNullable(musicChannelId)
						.map(getGuild()::getVoiceChannelById)
						.ifPresent(voiceChannel -> {
							RSNAudioManager.play(Main.getJda().getSelfUser(), voiceChannel, new TrackConsumer(){
								@Override
								public void onPlaylist(List<AudioTrack> tracks){
									TwitchIRC.sendMessage(getGuild(), getIrcChannel(), "Track added by " + event.getUser().getNick() + ": " + tracks.stream()
											.map(track -> track.getInfo().title)
											.collect(Collectors.joining(", ")));
								}
								
								@Override
								public void onTrack(AudioTrack track){
									TwitchIRC.sendMessage(getGuild(), getIrcChannel(), "Track added " + event.getUser().getNick() + ": " + track.getInfo().title);
								}
								
								@Override
								public void onFailure(String message){
									TwitchIRC.sendMessage(getGuild(), getIrcChannel(), "Failed to add track");
								}
							}, 0, 1, link);
						});
			}
			if(twitchMessage.isModerator() && event.getMessage().startsWith("?rskip")){
				RSNAudioManager.skip(getGuild());
				TwitchIRC.sendMessage(getGuild(), getIrcChannel(), event.getUser().getNick() + " skipped the music");
			}
		}
	}
	
	private void randomKickClaimed(@NonNull ChannelMessageIRCMessage event){
		Settings.get(getGuild()).getGeneralChannel()
				.flatMap(ChannelConfiguration::getChannel)
				.ifPresent(channel -> {
					var pings = Settings.get(getGuild()).getRandomKick()
							.getRandomKickRolesPing().stream()
							.flatMap(pingRole -> pingRole.getRole().stream())
							.collect(toSet());
					var pingsStr = pings.stream().map(Role::getAsMention)
							.collect(joining(" "));
					var targetRole = RandomKick.getRandomRole(getGuild());
					
					var messageContent = pingsStr + " => " + translate(getGuild(), "random-kick.bought",
							event.getUser().getNick(),
							targetRole.map(Role::getAsMention).orElse("@everyone"));
					channel.sendMessage(messageContent)
							.allowedMentions(List.of(Message.MentionType.ROLE))
							.mention(pings)
							.submit()
							.thenAccept(message -> Main.getExecutorService().schedule(() -> {
								var reason = translate(getGuild(), "random-kick.bought-reason",
										event.getUser().getNick(),
										getUser(),
										event.getMessage());
								
								RandomKick.randomKick(channel, targetRole.orElse(null), reason, false);
							}, 30, SECONDS));
				});
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
			var message = translate(channel.getGuild(), "irc.notice",
					event.getTags().stream()
							.filter(t -> Objects.equals("system-msg", t.getKey()))
							.map(IRCTag::getValue)
							.map(v -> v.replace("\\s", " ").trim())
							.findFirst()
							.orElse("UNKNOWN"));
			this.channel.sendMessage(message).submit();
		}
	}
	
	@Override
	protected void onClearChat(final ClearChatIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), this.ircChannel)){
			var message = translate(channel.getGuild(), "irc.banned",
					event.getUser(),
					event.getTags().stream()
							.filter(t -> Objects.equals("ban-duration", t.getKey()))
							.map(IRCTag::getValue)
							.map(Integer::parseInt)
							.map(Duration::ofSeconds)
							.map(Utilities::durationToString)
							.findFirst()
							.orElse("UNKNOWN"));
			this.channel.sendMessage(message).submit();
		}
	}
	
	@Override
	protected void onClearMessage(final ClearMessageIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), this.ircChannel)){
			Log.getLogger(this.getGuild()).info("Message from {} deleted: {}", event.getTags().stream()
							.filter(t -> Objects.equals("login", t.getKey()))
							.map(IRCTag::getValue)
							.findFirst()
							.orElse("UNKNOWN"),
					event.getMessage());
		}
	}
	
	@Override
	protected void onNotice(final NoticeIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), this.ircChannel)){
			if(ACCEPTED_NOTICES.contains(event.getMessageId().orElse(UNKNOWN))){
				this.channel.sendMessage(translate(channel.getGuild(), "irc.notice", event.getMessage())).submit();
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
