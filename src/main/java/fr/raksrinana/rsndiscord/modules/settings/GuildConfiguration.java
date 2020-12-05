package fr.raksrinana.rsndiscord.modules.settings;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.modules.anilist.config.AniListConfiguration;
import fr.raksrinana.rsndiscord.modules.autoroles.config.LeavingRolesConfiguration;
import fr.raksrinana.rsndiscord.modules.birthday.config.BirthdaysConfiguration;
import fr.raksrinana.rsndiscord.modules.externaltodos.config.ExternalTodosConfiguration;
import fr.raksrinana.rsndiscord.modules.hermitcraft.config.HermitcraftConfiguration;
import fr.raksrinana.rsndiscord.modules.irc.config.TwitchConfiguration;
import fr.raksrinana.rsndiscord.modules.joinleave.config.JoinLeaveConfiguration;
import fr.raksrinana.rsndiscord.modules.participation.config.ParticipationConfiguration;
import fr.raksrinana.rsndiscord.modules.permission.config.PermissionsConfiguration;
import fr.raksrinana.rsndiscord.modules.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.modules.reaction.config.ReactionsConfiguration;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.modules.schedule.config.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.modules.series.config.TraktConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.guild.NicknameConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.guild.RandomKickConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.modules.trombinoscope.config.TrombinoscopeConfiguration;
import fr.raksrinana.rsndiscord.modules.twitter.config.TwitterConfiguration;
import fr.raksrinana.rsndiscord.utils.json.DurationDeserializer;
import fr.raksrinana.rsndiscord.utils.json.DurationSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import java.time.Duration;
import java.util.*;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class GuildConfiguration implements ICompositeConfiguration{
	@JsonProperty("locale")
	@Setter
	private Locale locale;
	@JsonProperty("schedules")
	@JsonAlias({"reminders"})
	private final List<ScheduleConfiguration> schedules = new ArrayList<>();
	@JsonProperty("aniList")
	@Getter
	private final AniListConfiguration aniListConfiguration = new AniListConfiguration();
	@JsonProperty("trakt")
	@Getter
	private final TraktConfiguration traktConfiguration = new TraktConfiguration();
	@JsonProperty("nickname")
	@Getter
	private final NicknameConfiguration nicknameConfiguration = new NicknameConfiguration();
	@JsonProperty("twitchConfiguration")
	@Getter
	private final TwitchConfiguration twitchConfiguration = new TwitchConfiguration();
	@JsonProperty("hermitcraft")
	@Getter
	private final HermitcraftConfiguration hermitcraftConfiguration = new HermitcraftConfiguration();
	@JsonProperty("prefix")
	@Setter
	private String prefix;
	@JsonProperty("autoRoles")
	@Getter
	@Setter
	private Set<RoleConfiguration> autoRoles = new HashSet<>();
	@JsonProperty("moderatorRoles")
	@Getter
	@Setter
	private Set<RoleConfiguration> moderatorRoles = new HashSet<>();
	@JsonProperty("autoThumbChannels")
	@JsonAlias("ideaChannels")
	@Getter
	@Setter
	private Set<ChannelConfiguration> autoThumbsChannels = new HashSet<>();
	@JsonProperty("autoReactionsChannels")
	@Getter
	@Setter
	private Set<ChannelConfiguration> autoReactionsChannels = new HashSet<>();
	@JsonProperty("guildId")
	@Getter
	private long guildId;
	@JsonProperty("announceStartChannel")
	@Setter
	private ChannelConfiguration announceStartChannel;
	@JsonProperty("musicVolume")
	@Getter
	@Setter
	private int musicVolume = 100;
	@JsonProperty("archiveCategory")
	@Setter
	private CategoryConfiguration archiveCategory;
	@JsonProperty("messagesAwaitingReaction")
	@Setter
	private Set<WaitingReactionMessageConfiguration> messagesAwaitingReaction = new HashSet<>();
	@JsonProperty("reactions")
	@Getter
	@Setter
	private ReactionsConfiguration reactionsConfiguration = new ReactionsConfiguration();
	@JsonProperty("participation")
	@Getter
	private final ParticipationConfiguration participationConfiguration = new ParticipationConfiguration();
	@JsonProperty("trombinoscope")
	@Getter
	private final TrombinoscopeConfiguration trombinoscope = new TrombinoscopeConfiguration();
	@JsonProperty("externalTodos")
	@Getter
	private final ExternalTodosConfiguration externalTodos = new ExternalTodosConfiguration();
	@JsonProperty("leavingRoles")
	@Getter
	private final LeavingRolesConfiguration leavingRolesConfiguration = new LeavingRolesConfiguration();
	@JsonProperty("onlyMediaChannels")
	@Getter
	@Setter
	private Set<ChannelConfiguration> onlyMediaChannels = new HashSet<>();
	@JsonProperty("generalChannel")
	@Setter
	private ChannelConfiguration generalChannel;
	@JsonProperty("logChannel")
	@Setter
	private ChannelConfiguration logChannel;
	@JsonProperty("randomKick")
	@Getter
	private final RandomKickConfiguration randomKick = new RandomKickConfiguration();
	@JsonProperty("birthdays")
	@Getter
	@Setter
	private BirthdaysConfiguration birthdays = new BirthdaysConfiguration();
	@JsonProperty("permissions")
	@Getter
	@Setter
	private PermissionsConfiguration permissionsConfiguration = new PermissionsConfiguration();
	@JsonProperty("eventWinnerRole")
	@Setter
	private RoleConfiguration eventWinnerRole;
	@JsonProperty("discordIncidentsChannel")
	@Setter
	private ChannelConfiguration discordIncidentsChannel;
	@JsonProperty("leaveServerBanDuration")
	@Setter
	@JsonDeserialize(using = DurationDeserializer.class)
	@JsonSerialize(using = DurationSerializer.class)
	private Duration leaveServerBanDuration;
	@JsonProperty("twitter")
	@Getter
	@Setter
	private TwitterConfiguration twitterConfiguration = new TwitterConfiguration();
	@JsonProperty("joinLeave")
	@Getter
	@Setter
	private JoinLeaveConfiguration joinLeaveConfiguration = new JoinLeaveConfiguration();
	
	GuildConfiguration(final long guildId){
		this.guildId = guildId;
	}
	
	public void addMessagesAwaitingReaction(@NonNull WaitingReactionMessageConfiguration reaction){
		this.messagesAwaitingReaction.add(reaction);
	}
	
	public void removeMessagesAwaitingReaction(@NonNull WaitingReactionMessageConfiguration reaction){
		this.messagesAwaitingReaction.remove(reaction);
	}
	
	public Collection<WaitingReactionMessageConfiguration> getMessagesAwaitingReaction(@NonNull ReactionTag tag){
		return new HashSet<>(this.messagesAwaitingReaction).stream()
				.filter(reaction -> Objects.equals(reaction.getTag(), tag))
				.collect(toSet());
	}
	
	public void removeSchedule(ScheduleConfiguration schedule){
		this.schedules.remove(schedule);
	}
	
	public void addSchedule(@NonNull ScheduleConfiguration schedule){
		this.schedules.add(schedule);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getAnnounceStartChannel(){
		return ofNullable(this.announceStartChannel);
	}
	
	public Optional<CategoryConfiguration> getArchiveCategory(){
		return ofNullable(archiveCategory);
	}
	
	public Optional<ChannelConfiguration> getDiscordIncidentsChannel(){
		return ofNullable(this.discordIncidentsChannel);
	}
	
	public Optional<RoleConfiguration> getEventWinnerRole(){
		return ofNullable(this.eventWinnerRole);
	}
	
	public Optional<ChannelConfiguration> getGeneralChannel(){
		return Optional.of(generalChannel);
	}
	
	public Optional<Duration> getLeaveServerBanDuration(){
		return ofNullable(leaveServerBanDuration);
	}
	
	public Iterator<WaitingReactionMessageConfiguration> getMessagesAwaitingReaction(){
		return this.messagesAwaitingReaction.iterator();
	}
	
	public Optional<Locale> getLocale(){
		return ofNullable(this.locale);
	}
	
	public Optional<ChannelConfiguration> getLogChannel(){
		return ofNullable(this.logChannel);
	}
	
	@NonNull
	public Optional<String> getPrefix(){
		return ofNullable(this.prefix);
	}
	
	public List<ScheduleConfiguration> getSchedules(){
		return new LinkedList<>(this.schedules);
	}
}
