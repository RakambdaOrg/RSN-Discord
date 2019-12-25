package fr.raksrinana.rsndiscord.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.guild.*;
import fr.raksrinana.rsndiscord.settings.types.*;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuildConfiguration{
	@JsonProperty("guildId")
	private long guildId;
	@JsonProperty("prefix")
	private String prefix;
	@JsonProperty("aniList")
	private AniListConfiguration aniListConfiguration = new AniListConfiguration();
	@JsonProperty("trakt")
	private TraktConfiguration traktConfiguration = new TraktConfiguration();
	@JsonProperty("autoRoles")
	private Set<RoleConfiguration> autoRoles = new HashSet<>();
	@JsonProperty("moderatorRoles")
	private Set<RoleConfiguration> moderatorRoles = new HashSet<>();
	@JsonProperty("djRole")
	private RoleConfiguration djRole;
	@JsonProperty("warns")
	private WarnsConfiguration warnsConfiguration = new WarnsConfiguration();
	@JsonProperty("ideaChannels")
	private Set<ChannelConfiguration> ideaChannels = new HashSet<>();
	@JsonProperty("npXpChannels")
	private Set<ChannelConfiguration> noXpChannels = new HashSet<>();
	@JsonProperty("participation")
	private ParticipationConfig participationConfig = new ParticipationConfig();
	@JsonProperty("reportChannel")
	private ChannelConfiguration reportChannel;
	@JsonProperty("nickname")
	private NicknameConfiguration nicknameConfiguration = new NicknameConfiguration();
	@JsonProperty("twitchChannel")
	private ChannelConfiguration twitchChannel;
	@JsonProperty("quizChannel")
	private ChannelConfiguration quizChannel;
	@JsonProperty("questions")
	private QuestionsConfiguration questionsConfiguration = new QuestionsConfiguration();
	@JsonProperty("removeRoles")
	private Set<RemoveRoleConfiguration> removeRoles = new HashSet<>();
	@JsonProperty("trombinoscope")
	private TrombinoscopeConfiguration trombinoscopeConfiguration = new TrombinoscopeConfiguration();
	@JsonProperty("addBackRoles")
	private Set<UserRoleConfiguration> addBackRoles = new HashSet<>();
	@JsonProperty("leaverRole")
	private RoleConfiguration leaverRole;
	@JsonProperty("poopRole")
	private RoleConfiguration poopRole;
	@JsonProperty("ircForward")
	private boolean ircForward = false;
	@JsonProperty("overwatchLeague")
	private OverwatchLeagueConfiguration overwatchLeagueConfiguration = new OverwatchLeagueConfiguration();
	@JsonProperty("announceStartChannel")
	private ChannelConfiguration announceStartChannel;
	@JsonProperty("musicVolume")
	private int musicVolume = 100;
	@JsonProperty("todoMessages")
	private ConcurrentLinkedQueue<TodoConfiguration> todos = new ConcurrentLinkedQueue<>();
	@JsonProperty("twitchAutoConnectUsers")
	private Set<String> twitchAutoConnectUsers = new HashSet<>();
	@JsonProperty("reminders")
	private List<ReminderConfiguration> reminders = new ArrayList<>();
	@JsonProperty("christmasRole")
	@Setter
	private RoleConfiguration christmasRole;
	@JsonProperty("newYearRole")
	@Setter
	private RoleConfiguration newYearRole;
	
	public GuildConfiguration(){
	}
	
	GuildConfiguration(final long guildId){
		this.guildId = guildId;
	}
	
	public Optional<RoleConfiguration> getChristmasRole(){
		return Optional.ofNullable(christmasRole);
	}
	
	public Optional<RoleConfiguration> getNewYearRole(){
		return Optional.ofNullable(newYearRole);
	}
	
	public void removeTodo(TodoConfiguration todoConfiguration){
		this.todos.remove(todoConfiguration);
	}
	
	public void addTodoMessage(@NonNull TodoConfiguration todoConfiguration){
		this.todos.add(todoConfiguration);
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
	
	public TraktConfiguration getTraktConfiguration(){
		return this.traktConfiguration;
	}
	
	public List<ReminderConfiguration> getReminders(){
		return this.reminders;
	}
	
	@NonNull
	public Set<RoleConfiguration> getAutoRoles(){
		return this.autoRoles;
	}
	
	@NonNull
	private Set<UserRoleConfiguration> getAddBackRoles(){
		return this.addBackRoles;
	}
	
	public void setAutoRoles(@NonNull final Set<RoleConfiguration> autoRoles){
		this.autoRoles = autoRoles;
	}
	
	@NonNull
	public AniListConfiguration getAniListConfiguration(){
		return this.aniListConfiguration;
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getAnnounceStartChannel(){
		return Optional.ofNullable(this.announceStartChannel);
	}
	
	public void setAnnounceStartChannel(final ChannelConfiguration channel){
		this.announceStartChannel = channel;
	}
	
	@NonNull
	public Optional<RoleConfiguration> getDjRole(){
		return Optional.ofNullable(this.djRole);
	}
	
	public void setDjRole(final RoleConfiguration djRole){
		this.djRole = djRole;
	}
	
	@NonNull
	public Set<ChannelConfiguration> getIdeaChannels(){
		return this.ideaChannels;
	}
	
	public void setIdeaChannels(@NonNull final Set<ChannelConfiguration> ideaChannels){
		this.ideaChannels = ideaChannels;
	}
	
	public boolean getIrcForward(){
		return this.ircForward;
	}
	
	@NonNull
	public Optional<RoleConfiguration> getLeaverRole(){
		return Optional.ofNullable(this.leaverRole);
	}
	
	public void setLeaverRole(final RoleConfiguration role){
		this.leaverRole = role;
	}
	
	@NonNull
	public Set<RoleConfiguration> getModeratorRoles(){
		return this.moderatorRoles;
	}
	
	public void setModeratorRoles(@NonNull final Set<RoleConfiguration> moderatorRoles){
		this.moderatorRoles = moderatorRoles;
	}
	
	@NonNull
	public NicknameConfiguration getNicknameConfiguration(){
		return this.nicknameConfiguration;
	}
	
	public int getMusicVolume(){
		return musicVolume;
	}
	
	public void setMusicVolume(int musicVolume){
		this.musicVolume = musicVolume;
	}
	
	@NonNull
	public Set<ChannelConfiguration> getNoXpChannels(){
		return this.noXpChannels;
	}
	
	public void setNoXpChannels(@NonNull final Set<ChannelConfiguration> noXpChannels){
		this.noXpChannels = noXpChannels;
	}
	
	@NonNull
	public OverwatchLeagueConfiguration getOverwatchLeagueConfiguration(){
		return this.overwatchLeagueConfiguration;
	}
	
	@NonNull
	public ParticipationConfig getParticipationConfiguration(){
		return this.participationConfig;
	}
	
	@NonNull
	public Optional<RoleConfiguration> getPoopRole(){
		return Optional.ofNullable(this.poopRole);
	}
	
	public void setPoopRole(final RoleConfiguration value){
		this.poopRole = value;
	}
	
	@NonNull
	public Optional<String> getPrefix(){
		return Optional.ofNullable(this.prefix);
	}
	
	public void setPrefix(final String prefix){
		this.prefix = prefix;
	}
	
	@NonNull
	public QuestionsConfiguration getQuestionsConfiguration(){
		return this.questionsConfiguration;
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getQuizChannel(){
		return Optional.ofNullable(this.quizChannel);
	}
	
	public void setQuizChannel(final ChannelConfiguration channel){
		this.quizChannel = channel;
	}
	
	@NonNull
	public Set<RemoveRoleConfiguration> getRemoveRoles(){
		return this.removeRoles;
	}
	
	public Set<String> getTwitchAutoConnectUsers(){
		return this.twitchAutoConnectUsers;
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getReportChannel(){
		return Optional.ofNullable(this.reportChannel);
	}
	
	public void setReportChannel(final ChannelConfiguration channel){
		this.reportChannel = channel;
	}
	
	@NonNull
	public Collection<TodoConfiguration> getTodos(){
		return this.todos;
	}
	
	@NonNull
	public TrombinoscopeConfiguration getTrombinoscopeConfiguration(){
		return this.trombinoscopeConfiguration;
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getTwitchChannel(){
		return Optional.ofNullable(this.twitchChannel);
	}
	
	public void setTwitchChannel(final ChannelConfiguration channel){
		this.twitchChannel = channel;
	}
	
	@NonNull
	public WarnsConfiguration getWarnsConfiguration(){
		return this.warnsConfiguration;
	}
	
	public void setIrcForward(@NonNull final Boolean value){
		this.ircForward = value;
	}
	
	public void setTwitchAutoConnectUsers(@NonNull Set<String> users){
		this.twitchAutoConnectUsers = users;
	}
}
