package fr.mrcraftcod.gunterdiscord.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.settings.guild.*;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.RemoveRoleConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.RoleConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.UserRoleConfiguration;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
@SuppressWarnings("FieldMayBeFinal")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuildConfiguration{
	@JsonProperty("guildId")
	private long guildId;
	@JsonProperty("prefix")
	private String prefix;
	@JsonProperty("aniList")
	private AniListConfiguration aniListConfiguration = new AniListConfiguration();
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
	
	public GuildConfiguration(){
	}
	
	GuildConfiguration(final long guildId){
		this.guildId = guildId;
	}
	
	public void addRemoveRole(@Nonnull final RemoveRoleConfiguration value){
		this.removeRoles.add(value);
	}
	
	public void addAddBackRole(@Nonnull final UserRoleConfiguration userRoleConfiguration){
		this.addBackRoles.add(userRoleConfiguration);
	}
	
	public Optional<RemoveRoleConfiguration> getRemoveRole(final User user, final Role role){
		return this.removeRoles.stream().filter(r -> Objects.equals(r.getUser().getUserId(), user.getIdLong()) && Objects.equals(r.getRole().getRoleId(), role.getIdLong())).findFirst();
	}
	
	@Nonnull
	public Optional<RoleConfiguration> getPoopRole(){
		return Optional.ofNullable(this.poopRole);
	}
	
	@Nonnull
	public List<RoleConfiguration> getAutoRolesAndAddBackRoles(@Nonnull final Member member){
		return Stream.concat(this.getAutoRoles().stream(), this.getAddBackRoles().stream().filter(c -> Objects.equals(c.getUser().getUserId(), member.getIdLong())).map(UserRoleConfiguration::getRole)).collect(Collectors.toList());
	}
	
	@Nonnull
	public OverwatchLeagueConfiguration getOverwatchLeagueConfiguration(){
		return this.overwatchLeagueConfiguration;
	}
	
	public boolean getIrcForward(){
		return this.ircForward;
	}
	
	@Nonnull
	public AniListConfiguration getAniListConfiguration(){
		return this.aniListConfiguration;
	}
	
	@Nonnull
	public Optional<RoleConfiguration> getDjRole(){
		return Optional.ofNullable(this.djRole);
	}
	
	public void setDjRole(@Nullable final RoleConfiguration djRole){
		this.djRole = djRole;
	}
	
	@Nonnull
	public Set<RoleConfiguration> getAutoRoles(){
		return this.autoRoles;
	}
	
	@Nonnull
	private Set<UserRoleConfiguration> getAddBackRoles(){
		return this.addBackRoles;
	}
	
	@Nonnull
	public WarnsConfiguration getWarnsConfiguration(){
		return this.warnsConfiguration;
	}
	
	public void setAutoRoles(@Nonnull final Set<RoleConfiguration> autoRoles){
		this.autoRoles = autoRoles;
	}
	
	public void setIdeaChannels(@Nonnull final Set<ChannelConfiguration> ideaChannels){
		this.ideaChannels = ideaChannels;
	}
	
	@Nonnull
	public Set<ChannelConfiguration> getIdeaChannels(){
		return this.ideaChannels;
	}
	
	public void setIrcForward(@Nonnull final Boolean value){
		this.ircForward = value;
	}
	
	@Nonnull
	public Optional<RoleConfiguration> getLeaverRole(){
		return Optional.ofNullable(this.leaverRole);
	}
	
	public void setLeaverRole(@Nullable final RoleConfiguration role){
		this.leaverRole = role;
	}
	
	@Nonnull
	public NicknameConfiguration getNicknameConfiguration(){
		return this.nicknameConfiguration;
	}
	
	@Nonnull
	public Set<RoleConfiguration> getModeratorRoles(){
		return this.moderatorRoles;
	}
	
	public void setModeratorRoles(@Nonnull final Set<RoleConfiguration> moderatorRoles){
		this.moderatorRoles = moderatorRoles;
	}
	
	@Nonnull
	public ParticipationConfig getParticipationConfiguration(){
		return this.participationConfig;
	}
	
	@Nonnull
	public Optional<String> getPrefix(){
		return Optional.ofNullable(this.prefix);
	}
	
	public void setNoXpChannels(@Nonnull final Set<ChannelConfiguration> noXpChannels){
		this.noXpChannels = noXpChannels;
	}
	
	@Nonnull
	public QuestionsConfiguration getQuestionsConfiguration(){
		return this.questionsConfiguration;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getQuizChannel(){
		return Optional.ofNullable(this.quizChannel);
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getAnnounceStartChannel(){
		return Optional.ofNullable(this.announceStartChannel);
	}
	
	public void setPoopRole(@Nullable final RoleConfiguration value){
		this.poopRole = value;
	}
	
	@Nonnull
	public Set<ChannelConfiguration> getNoXpChannels(){
		return this.noXpChannels;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getReportChannel(){
		return Optional.ofNullable(this.reportChannel);
	}
	
	public void setPrefix(@Nullable final String prefix){
		this.prefix = prefix;
	}
	
	@Nonnull
	public TrombinoscopeConfiguration getTrombinoscopeConfiguration(){
		return this.trombinoscopeConfiguration;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getTwitchChannel(){
		return Optional.ofNullable(this.twitchChannel);
	}
	
	public void setQuizChannel(@Nullable final ChannelConfiguration channel){
		this.quizChannel = channel;
	}
	
	public void setAnnounceStartChannel(@Nullable final ChannelConfiguration channel){
		this.announceStartChannel = channel;
	}
	
	public void setReportChannel(@Nullable final ChannelConfiguration channel){
		this.reportChannel = channel;
	}
	
	public void setTwitchChannel(@Nullable final ChannelConfiguration channel){
		this.twitchChannel = channel;
	}
	
	@Nonnull
	public Set<RemoveRoleConfiguration> getRemoveRoles(){
		return this.removeRoles;
	}
}
