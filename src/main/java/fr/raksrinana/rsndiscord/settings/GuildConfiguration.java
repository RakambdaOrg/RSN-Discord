package fr.raksrinana.rsndiscord.settings;

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
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class GuildConfiguration implements CompositeConfiguration{
	@JsonProperty("guildId")
	private long guildId;
	@JsonProperty("prefix")
	@Setter
	private String prefix;
	@JsonProperty("aniList")
	@Getter
	private final AniListConfiguration aniListConfiguration = new AniListConfiguration();
	@JsonProperty("trakt")
	@Getter
	private final TraktConfiguration traktConfiguration = new TraktConfiguration();
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
	@JsonProperty("warns")
	@Getter
	private final WarnsConfiguration warnsConfiguration = new WarnsConfiguration();
	@JsonProperty("ideaChannels")
	@Getter
	@Setter
	private Set<ChannelConfiguration> ideaChannels = new HashSet<>();
	@JsonProperty("npXpChannels")
	@Getter
	@Setter
	private Set<ChannelConfiguration> noXpChannels = new HashSet<>();
	@JsonProperty("participation")
	@Getter
	private final ParticipationConfig participationConfig = new ParticipationConfig();
	@JsonProperty("reportChannel")
	@Setter
	private ChannelConfiguration reportChannel;
	@JsonProperty("nickname")
	@Getter
	private final NicknameConfiguration nicknameConfiguration = new NicknameConfiguration();
	@JsonProperty("twitchChannel")
	@Setter
	private ChannelConfiguration twitchChannel;
	@JsonProperty("quizChannel")
	@Setter
	private ChannelConfiguration quizChannel;
	@JsonProperty("questions")
	@Getter
	private final QuestionsConfiguration questionsConfiguration = new QuestionsConfiguration();
	@JsonProperty("removeRoles")
	@Getter
	@Setter
	private Set<RemoveRoleConfiguration> removeRoles = new HashSet<>();
	@JsonProperty("trombinoscope")
	@Getter
	private final TrombinoscopeConfiguration trombinoscopeConfiguration = new TrombinoscopeConfiguration();
	@JsonProperty("addBackRoles")
	@Getter
	private final Set<UserRoleConfiguration> addBackRoles = new HashSet<>();
	@JsonProperty("leaverRole")
	@Setter
	private RoleConfiguration leaverRole;
	@JsonProperty("poopRole")
	@Setter
	private RoleConfiguration poopRole;
	@JsonProperty("ircForward")
	@Getter
	@Setter
	private boolean ircForward = false;
	@JsonProperty("overwatchLeague")
	@Getter
	private final OverwatchLeagueConfiguration overwatchLeagueConfiguration = new OverwatchLeagueConfiguration();
	@JsonProperty("rainbow6ProLeague")
	@Getter
	private final Rainbow6ProLeagueConfiguration rainbow6ProLeagueConfiguration = new Rainbow6ProLeagueConfiguration();
	@JsonProperty("announceStartChannel")
	@Setter
	private ChannelConfiguration announceStartChannel;
	@JsonProperty("musicVolume")
	@Getter
	@Setter
	private int musicVolume = 100;
	@JsonProperty("twitchAutoConnectUsers")
	@Getter
	@Setter
	private Set<String> twitchAutoConnectUsers = new HashSet<>();
	@JsonProperty("reminders")
	@Getter
	private final List<ReminderConfiguration> reminders = new ArrayList<>();
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
	@JsonProperty("amazonTrackings")
	@Getter
	@Setter
	private Set<AmazonTrackingConfiguration> amazonTrackings = new HashSet<>();
	@JsonProperty("autoTodoChannels")
	@Getter
	@Setter
	private Set<ChannelConfiguration> autoTodoChannels = new HashSet<>();
	
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
	
	public Iterator<WaitingReactionMessageConfiguration> getMessagesAwaitingReaction(){
		return this.messagesAwaitingReaction.iterator();
	}
	
	public Optional<RoleConfiguration> getChristmasRole(){
		return Optional.ofNullable(christmasRole);
	}
	
	public Optional<RoleConfiguration> getNewYearRole(){
		return Optional.ofNullable(newYearRole);
	}
	
	public Optional<CategoryConfiguration> getArchiveCategory(){
		return Optional.ofNullable(archiveCategory);
	}
	
	public void addRemoveRole(@NonNull final RemoveRoleConfiguration value){
		this.removeRoles.add(value);
	}
	
	public void addAddBackRole(@NonNull final UserRoleConfiguration userRoleConfiguration){
		this.addBackRoles.add(userRoleConfiguration);
	}
	
	public Optional<RemoveRoleConfiguration> getRemoveRole(final User user, final Role role){
		return this.removeRoles.stream().filter(r -> Objects.equals(r.getUser().getUserId(), user.getIdLong()) && Objects.equals(r.getRole().getRoleId(), role.getIdLong())).findFirst();
	}
	
	@NonNull
	public List<RoleConfiguration> getAutoRolesAndAddBackRoles(@NonNull final Member member){
		return Stream.concat(this.getAutoRoles().stream(), this.getAddBackRoles().stream().filter(c -> Objects.equals(c.getUser().getUserId(), member.getIdLong())).map(UserRoleConfiguration::getRole)).collect(Collectors.toList());
	}
	
	public void addReminder(@NonNull ReminderConfiguration reminder){
		this.reminders.add(reminder);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getAnnounceStartChannel(){
		return Optional.ofNullable(this.announceStartChannel);
	}
	
	@NonNull
	public Optional<RoleConfiguration> getDjRole(){
		return Optional.ofNullable(this.djRole);
	}
	
	@NonNull
	public Optional<RoleConfiguration> getLeaverRole(){
		return Optional.ofNullable(this.leaverRole);
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
	
	@NonNull
	public Optional<ChannelConfiguration> getTwitchChannel(){
		return Optional.ofNullable(this.twitchChannel);
	}
}
