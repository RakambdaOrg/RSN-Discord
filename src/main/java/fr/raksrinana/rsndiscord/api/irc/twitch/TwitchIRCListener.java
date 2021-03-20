package fr.raksrinana.rsndiscord.api.irc.twitch;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.api.irc.messages.*;
import fr.raksrinana.rsndiscord.api.irc.twitch.messages.*;
import fr.raksrinana.rsndiscord.command.impl.RandomKick;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.music.TrackConsumer;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.api.irc.twitch.TwitchMessageId.*;
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
	
	TwitchIRCListener(@NotNull Guild guild, @NotNull String user, @NotNull String channel){
		this.guild = guild;
		this.user = user;
		this.channel = Settings.get(guild).getTwitchConfiguration().getTwitchChannel().orElseThrow().getChannel().orElseThrow();
		ircChannel = channel;
		lastMessage = System.currentTimeMillis();
	}
	
	@Override
	protected void onIRCChannelJoined(@NotNull ChannelJoinIRCMessage event){
		var channel = event.getChannel();
		if(Objects.equals(channel, ircChannel)){
			Log.getLogger(getGuild()).info("Joined {}", channel);
		}
	}
	
	@Override
	protected void onIRCChannelLeft(@NotNull ChannelLeftIRCMessage event){
		var channel = event.getChannel();
		if(Objects.equals(channel, ircChannel)){
			Log.getLogger(getGuild()).info("Left {}", channel);
		}
	}
	
	@Override
	protected void onIRCChannelMessage(@NotNull ChannelMessageIRCMessage event){
		if(Objects.equals(event.getChannel(), ircChannel)){
			lastMessage = System.currentTimeMillis();
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
			
			channel.sendMessage(MessageFormat.format("{0} : **`{1}`{2}** {3}", twitchMessage.getParent().getChannel(), displayName, role, event.getMessage()))
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
						.ifPresentOrElse(voiceChannel -> RSNAudioManager.play(Main.getJda().getSelfUser(), voiceChannel, new TrackConsumer(){
							@Override
							public void onPlaylist(@NotNull List<AudioTrack> tracks){
								TwitchIRC.sendMessage(getGuild(), getIrcChannel(), "Track added by " + event.getUser().getNick() + ": " + tracks.stream()
										.map(track -> track.getInfo().title)
										.collect(Collectors.joining(", ")));
							}
							
							@Override
							public void onTrack(@NotNull AudioTrack track){
								TwitchIRC.sendMessage(getGuild(), getIrcChannel(), "Track added by " + event.getUser().getNick() + ": " + track.getInfo().title);
							}
							
							@Override
							public void onFailure(@NotNull String message){
								TwitchIRC.sendMessage(getGuild(), getIrcChannel(), "Failed to add track");
							}
						}, 0, 1, link), () -> TwitchIRC.sendMessage(getGuild(), getIrcChannel(), MessageFormat.format("Requests are currently disabled {0}", musicChannelId)));
			}
			if(twitchMessage.isModerator() && event.getMessage().startsWith("?rskip")){
				RSNAudioManager.skip(getGuild());
				TwitchIRC.sendMessage(getGuild(), getIrcChannel(), event.getUser().getNick() + " skipped the music");
			}
		}
	}
	
	private void randomKickClaimed(@NotNull ChannelMessageIRCMessage event){
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
	protected void onPingIRC(@NotNull PingIRCMessage event){
	}
	
	@Override
	protected void onInfoMessage(@NotNull InfoMessageIRCMessage event){
		Log.getLogger(getGuild()).info("IRC Info: {}", event.getMessage());
	}
	
	@Override
	protected void onUserNotice(@NotNull UserNoticeIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), ircChannel)){
			var message = translate(channel.getGuild(), "irc.notice",
					event.getTags().stream()
							.filter(t -> Objects.equals("system-msg", t.getKey()))
							.map(IRCTag::getValue)
							.map(v -> v.replace("\\s", " ").trim())
							.findFirst()
							.orElse("UNKNOWN"));
			channel.sendMessage(message).submit();
		}
	}
	
	@Override
	protected void onClearChat(@NotNull ClearChatIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), ircChannel)){
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
			channel.sendMessage(message).submit();
		}
	}
	
	@Override
	protected void onClearMessage(@NotNull ClearMessageIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), ircChannel)){
			Log.getLogger(getGuild()).info("Message from {} deleted: {}", event.getTags().stream()
							.filter(t -> Objects.equals("login", t.getKey()))
							.map(IRCTag::getValue)
							.findFirst()
							.orElse("UNKNOWN"),
					event.getMessage());
		}
	}
	
	@Override
	protected void onNotice(@NotNull NoticeIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), ircChannel)){
			if(ACCEPTED_NOTICES.contains(event.getMessageId().orElse(UNKNOWN))){
				channel.sendMessage(translate(channel.getGuild(), "irc.notice", event.getMessage())).submit();
			}
			else{
				Log.getLogger(getGuild()).debug("Unhandled notice: {}", event.getMessage());
			}
		}
	}
	
	@Override
	protected void onHostTarget(@NotNull HostTargetIRCMessage event){
		if(Objects.equals(event.getIrcChannel(), ircChannel)){
			Log.getLogger(getGuild()).info("{} hosting {}", event.getIrcChannel(), event.getInfos());
		}
	}
	
	@Override
	protected void onIRCUnknownEvent(@NotNull IIRCMessage event){
	}
	
	@Override
	public boolean handlesChannel(@NotNull String channel){
		return Objects.equals(channel, ircChannel);
	}
	
	@Override
	public void timedOut(){
		TwitchIRC.disconnect(getGuild(), getUser());
		try{
			TwitchIRC.connect(getGuild(), getUser());
		}
		catch(IOException e){
			Log.getLogger(null).warn("Failed to reconnect {} to user {}", getGuild(), getUser());
		}
	}
	
	@Override
	public long getLastMessage(){
		return System.currentTimeMillis() - lastMessage;
	}
	
	@Override
	public void onEvent(@NotNull GenericEvent event){
		if(event instanceof GuildMessageReceivedEvent evt){
			try{
				if(!evt.getAuthor().isBot() && Objects.equals(evt.getChannel(), channel)){
					TwitchIRC.sendMessage(evt.getGuild(), ircChannel, evt.getAuthor().getName() + " -> " + evt.getMessage().getContentRaw());
				}
			}
			catch(Exception e){
				Log.getLogger(evt.getGuild()).error("Failed to transfer message", e);
			}
		}
	}
}
