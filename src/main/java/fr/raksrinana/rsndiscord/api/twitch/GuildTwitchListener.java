package fr.raksrinana.rsndiscord.api.twitch;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.command.impl.RandomKick;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.music.TrackConsumer;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.Getter;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.element.MessageTag;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.channel.ChannelNoticeEvent;
import org.kitteh.irc.client.library.feature.twitch.event.ClearChatEvent;
import org.kitteh.irc.client.library.feature.twitch.event.UserNoticeEvent;
import org.kitteh.irc.client.library.feature.twitch.messagetag.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.api.twitch.TwitchMessageId.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.kitteh.irc.client.library.feature.twitch.messagetag.Badges.KnownNames.*;

public class GuildTwitchListener{
	private static final Set<String> ACCEPTED_NOTICES = Set.of(
			FOLLOWERS_OFF.getValue(),
			FOLLOWERS_ON.getValue(),
			FOLLOWERS_ONZERO.getValue(),
			MSG_BAD_CHARACTERS.getValue(),
			MSG_BANNED.getValue(),
			MSG_CHANNEL_BLOCKED.getValue(),
			MSG_CHANNEL_SUSPENDED.getValue(),
			MSG_DUPLICATE.getValue(),
			MSG_FOLLOWERSONLY.getValue(),
			MSG_FOLLOWERSONLY_ZERO.getValue(),
			MSG_R9K.getValue(),
			MSG_RATELIMIT.getValue(),
			MSG_REJECTED.getValue(),
			MSG_REJECTED_MANDATORY.getValue(),
			MSG_ROOM_NOT_FOUND.getValue(),
			MSG_SLOWMODE.getValue(),
			MSG_SUBSONLY.getValue(),
			MSG_SUSPENDED.getValue(),
			MSG_TIMEDOUT.getValue(),
			R9K_OFF.getValue(),
			R9K_ON.getValue(),
			SLOW_OFF.getValue(),
			SLOW_ON.getValue(),
			SUBS_OFF.getValue(),
			SUBS_ON.getValue(),
			TOS_BAN.getValue(),
			WHISPER_BANNED.getValue(),
			WHISPER_BANNED_RECIPIENT.getValue(),
			WHISPER_LIMIT_PER_MIN.getValue(),
			WHISPER_LIMIT_PER_SEC.getValue(),
			WHISPER_RESTRICTED.getValue(),
			WHISPER_RESTRICTED_RECIPIENT.getValue()
	);
	@Getter
	private final long guildId;
	private final long channelId;
	private Long musicChannelId;
	
	public GuildTwitchListener(long guildId, long channelId){
		this.guildId = guildId;
		this.channelId = channelId;
	}
	
	public void onChannelMessageEvent(@NotNull ChannelMessageEvent event){
		var displayName = event.getTag(DisplayName.NAME)
				.flatMap(MessageTag::getValue)
				.orElse(event.getActor().getNick());
		
		var badges = event.getTag(Badges.NAME, Badges.class).stream()
				.map(Badges::getBadges)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		
		var isAdmin = badges.stream().anyMatch(b -> Objects.equals(b.getName(), ADMIN));
		var isMod = badges.stream().anyMatch(b -> Objects.equals(b.getName(), MODERATOR));
		
		var role = new StringBuilder();
		if(isAdmin){
			role.append("(boss)");
		}
		if(isMod){
			role.append("(mod)");
		}
		
		badges.stream()
				.filter(b -> Objects.equals(b.getName(), SUBSCRIBER))
				.forEach(sub -> role.append("(sub/").append(sub.getVersion()).append(")"));
		badges.stream()
				.filter(b -> Objects.equals(b.getName(), "sub-gifter"))
				.forEach(sub -> role.append("(subgifter/").append(sub.getVersion()).append(")"));
		if(event.getTag("partner").isPresent()){
			role.append("(partner)");
		}
		
		var content = "%s : **`%s`%s** %s".formatted(
				event.getChannel().getName(),
				displayName,
				role,
				event.getMessage());
		JDAWrappers.message(getChannel(), content)
				.allowedMentions(List.of())
				.submit();
		
		event.getTag("custom-reward-id")
				.flatMap(MessageTag::getValue)
				.ifPresent(rewardId -> onRewardClaimedEvent(event, rewardId));
		
		if(event.getMessage().startsWith("?")){
			try{
				onPotentialCommand(event);
			}
			catch(Exception e){
				Log.getLogger(getGuild()).error("Failed to process IRC command", e);
			}
		}
	}
	
	private void onPotentialCommand(@NotNull ChannelMessageEvent event){
		var message = event.getMessage();
		
		var badges = event.getTag(Badges.NAME, Badges.class).stream()
				.map(Badges::getBadges)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		
		var isAdmin = badges.stream().anyMatch(b -> Objects.equals(b.getName(), ADMIN));
		var isMod = badges.stream().anyMatch(b -> Objects.equals(b.getName(), MODERATOR));
		
		var parts = message.split(" ", 2);
		switch(parts[0]){
			case "?ron":
				if(isAdmin){
					var id = message.split(" ", 2)[1];
					musicChannelId = Long.parseLong(id);
				}
				break;
			case "?roff":
				if(isAdmin){
					musicChannelId = null;
				}
				break;
			case "?request":
				getMusicChannel().ifPresentOrElse(voiceChannel -> RSNAudioManager.play(Main.getJda().getSelfUser(), voiceChannel, new TrackConsumer(){
							@Override
							public void onPlaylist(@NotNull List<AudioTrack> tracks){
								var titles = tracks.stream()
										.map(track -> track.getInfo().title)
										.collect(Collectors.joining(", "));
								event.getChannel().sendMessage("Track added by %s: %s".formatted(event.getActor().getNick(), titles));
							}
							
							@Override
							public void onTrack(@NotNull AudioTrack track){
								event.getChannel().sendMessage("Track added by %s: %s".formatted(event.getActor().getNick(), track.getInfo().title));
							}
							
							@Override
							public void onFailure(@NotNull String message){
								event.getChannel().sendMessage("Failed to add track");
							}
						}, 0, 1, parts[1]),
						() -> event.getChannel().sendMessage("Requests are currently disabled"));
				break;
			case "?rskip":
				if(isMod){
					RSNAudioManager.skip(getGuild());
					event.getChannel().sendMessage("%s skipped the music".formatted(event.getActor().getNick()));
				}
				break;
		}
	}
	
	private void onRewardClaimedEvent(@NotNull ChannelMessageEvent event, @NotNull String rewardId){
		var randomKickRewardId = Settings.get(getGuild())
				.getTwitchConfiguration()
				.getRandomKickRewardId();
		
		if(randomKickRewardId.equals(Optional.of(rewardId))){
			randomKickClaimed(event);
		}
	}
	
	private void randomKickClaimed(@NotNull ChannelMessageEvent event){
		Settings.get(getGuild()).getGeneralChannel()
				.flatMap(ChannelConfiguration::getChannel)
				.ifPresent(channel -> {
					var pings = Settings.get(getGuild()).getRandomKick()
							.getRandomKickRolesPing().stream()
							.flatMap(pingRole -> pingRole.getRole().stream())
							.collect(toSet());
					var pingsStr = pings.stream().map(Role::getAsMention).collect(joining(" "));
					var targetRole = RandomKick.getRandomRole(getGuild());
					
					var messageContent = pingsStr + " => " + translate(getGuild(), "random-kick.bought",
							event.getActor().getNick(),
							targetRole.map(Role::getAsMention).orElse("@everyone"));
					JDAWrappers.message(channel, messageContent)
							.allowedMentions(List.of(Message.MentionType.ROLE))
							.mention(pings)
							.submit()
							.thenAccept(message -> Main.getExecutorService().schedule(() -> {
								var reason = translate(getGuild(), "random-kick.bought-reason",
										event.getActor().getNick(),
										event.getChannel().getName(),
										event.getMessage());
								
								RandomKick.randomKick(channel, targetRole.orElse(null), reason, false);
							}, 30, SECONDS));
				});
	}
	
	public void onUserNoticeEvent(@NotNull UserNoticeEvent event){
		var message = translate(getGuild(), "irc.notice", event.getMessage());
		
		JDAWrappers.message(getChannel(), message).submit();
	}
	
	public void onClearChatEvent(@NotNull ClearChatEvent event){
		var message = translate(getGuild(), "irc.banned",
				event.getTag(BanReason.NAME)
						.flatMap(MessageTag::getValue)
						.orElse("UNKNOWN"),
				event.getTag(BanDuration.NAME, BanDuration.class)
						.map(BanDuration::getDuration)
						.map(Duration::ofSeconds)
						.map(Utilities::durationToString)
						.orElse("UNKNOWN"));
		
		JDAWrappers.message(getChannel(), message).submit();
	}
	
	public void onChannelNoticeEvent(@NotNull ChannelNoticeEvent event){
		var msgId = event.getTag(MsgId.NAME).flatMap(MessageTag::getValue);
		if(msgId.map(ACCEPTED_NOTICES::contains).orElse(false)){
			var message = translate(getGuild(), "irc.notice", event.getMessage());
			JDAWrappers.message(getChannel(), message).submit();
		}
	}
	
	@Nullable
	private Guild getGuild(){
		return Main.getJda().getGuildById(guildId);
	}
	
	@Nullable
	private TextChannel getChannel(){
		return Main.getJda().getTextChannelById(channelId);
	}
	
	@NotNull
	private Optional<VoiceChannel> getMusicChannel(){
		return Optional.ofNullable(musicChannelId)
				.map(getGuild()::getVoiceChannelById);
	}
}
