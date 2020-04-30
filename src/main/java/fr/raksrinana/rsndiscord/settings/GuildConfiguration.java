package fr.raksrinana.rsndiscord.settings;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.guild.*;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserRoleConfiguration;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class GuildConfiguration implements CompositeConfiguration{
	@JsonProperty("schedules")
	@JsonAlias({"reminders"})
	private List<ScheduleConfiguration> schedules = new ArrayList<>();
	@JsonProperty("aniList")
	@Getter
	private AniListConfiguration aniListConfiguration = new AniListConfiguration();
	@JsonProperty("trakt")
	@Getter
	private TraktConfiguration traktConfiguration = new TraktConfiguration();
	@JsonProperty("warns")
	@Getter
	private WarnsConfiguration warnsConfiguration = new WarnsConfiguration();
	@JsonProperty("participation")
	@Getter
	private ParticipationConfig participationConfig = new ParticipationConfig();
	@JsonProperty("nickname")
	@Getter
	private NicknameConfiguration nicknameConfiguration = new NicknameConfiguration();
	@JsonProperty("ircForward")
	@Getter
	@Deprecated
	private boolean ircForward = false;
	@JsonProperty("questions")
	@Getter
	private QuestionsConfiguration questionsConfiguration = new QuestionsConfiguration();
	@JsonProperty("trombinoscope")
	@Getter
	private TrombinoscopeConfiguration trombinoscopeConfiguration = new TrombinoscopeConfiguration();
	@JsonProperty("addBackRoles")
	@Getter
	private Set<UserRoleConfiguration> addBackRoles = new HashSet<>();
	@JsonProperty("twitchAutoConnectUsers")
	@Getter
	@Deprecated
	private Set<String> twitchAutoConnectUsers = new HashSet<>();
	@JsonProperty("overwatchLeague")
	@Getter
	private OverwatchLeagueConfiguration overwatchLeagueConfiguration = new OverwatchLeagueConfiguration();
	@JsonProperty("rainbow6ProLeague")
	@Getter
	private Rainbow6ProLeagueConfiguration rainbow6ProLeagueConfiguration = new Rainbow6ProLeagueConfiguration();
	@JsonProperty("twitchConfiguration")
	@Getter
	private TwitchConfiguration twitchConfiguration = new TwitchConfiguration();
	@JsonProperty("hermitcraft")
	@Getter
	private HermitcraftConfiguration hermitcraftConfiguration = new HermitcraftConfiguration();
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
	@JsonProperty("djRole")
	@Setter
	private RoleConfiguration djRole;
	@JsonProperty("ideaChannels")
	@Getter
	@Setter
	private Set<ChannelConfiguration> ideaChannels = new HashSet<>();
	@JsonProperty("npXpChannels")
	@Getter
	@Setter
	private Set<ChannelConfiguration> noXpChannels = new HashSet<>();
	@JsonProperty("reportChannel")
	@Setter
	private ChannelConfiguration reportChannel;
	@JsonProperty("quizChannel")
	@Setter
	private ChannelConfiguration quizChannel;
	@JsonProperty("guildId")
	@Getter
	private long guildId;
	@JsonProperty("leaverRole")
	@Setter
	private RoleConfiguration leaverRole;
	@JsonProperty("poopRole")
	@Setter
	private RoleConfiguration poopRole;
	@JsonProperty("announceStartChannel")
	@Setter
	private ChannelConfiguration announceStartChannel;
	@JsonProperty("musicVolume")
	@Getter
	@Setter
	private int musicVolume = 100;
	@JsonProperty("christmasRole")
	@Setter
	private RoleConfiguration christmasRole;
	@JsonProperty("newYearRole")
	@Setter
	private RoleConfiguration newYearRole;
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
	@JsonProperty("twitchChannel")
	@Getter
	@Deprecated
	private ChannelConfiguration twitchChannel;
	
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
		return new HashSet<>(this.messagesAwaitingReaction).stream().filter(reaction -> Objects.equals(reaction.getTag(), tag)).collect(Collectors.toSet());
	}
	
	public void removeSchedule(ScheduleConfiguration schedule){
		this.schedules.remove(schedule);
	}
	
	public void addAddBackRole(@NonNull final UserRoleConfiguration userRoleConfiguration){
		this.addBackRoles.add(userRoleConfiguration);
	}
	
	@NonNull
	public List<RoleConfiguration> getAutoRolesAndAddBackRoles(@NonNull final Member member){
		return Stream.concat(this.getAutoRoles().stream(), this.getAddBackRoles().stream().filter(c -> Objects.equals(c.getUser().getUserId(), member.getIdLong())).map(UserRoleConfiguration::getRole)).collect(Collectors.toList());
	}
	
	public void addSchedule(@NonNull ScheduleConfiguration schedule){
		this.schedules.add(schedule);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getAnnounceStartChannel(){
		return Optional.ofNullable(this.announceStartChannel);
	}
	
	public Optional<CategoryConfiguration> getArchiveCategory(){
		return Optional.ofNullable(archiveCategory);
	}
	
	public Optional<RoleConfiguration> getChristmasRole(){
		return Optional.ofNullable(christmasRole);
	}
	
	@NonNull
	public Optional<RoleConfiguration> getDjRole(){
		return Optional.ofNullable(this.djRole);
	}
	
	@NonNull
	public Optional<RoleConfiguration> getLeaverRole(){
		return Optional.ofNullable(this.leaverRole);
	}
	
	public Iterator<WaitingReactionMessageConfiguration> getMessagesAwaitingReaction(){
		return this.messagesAwaitingReaction.iterator();
	}
	
	public Optional<RoleConfiguration> getNewYearRole(){
		return Optional.ofNullable(newYearRole);
	}
	
	@NonNull
	public Optional<RoleConfiguration> getPoopRole(){
		return Optional.ofNullable(this.poopRole);
	}
	
	@NonNull
	public Optional<String> getPrefix(){
		return Optional.ofNullable(this.prefix);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getQuizChannel(){
		return Optional.ofNullable(this.quizChannel);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getReportChannel(){
		return Optional.ofNullable(this.reportChannel);
	}
	
	public List<ScheduleConfiguration> getSchedules(){
		return new LinkedList<>(this.schedules);
	}
}
