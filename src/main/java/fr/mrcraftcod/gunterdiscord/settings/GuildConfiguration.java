package fr.mrcraftcod.gunterdiscord.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.settings.guild.*;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.RemoveRoleConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.RoleConfiguration;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
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
	private List<RoleConfiguration> autoRoles = new ArrayList<>();
	@JsonProperty("moderatorRoles")
	private List<RoleConfiguration> moderatorRoles = new ArrayList<>();
	@JsonProperty("djRole")
	private RoleConfiguration djRole;
	@JsonProperty("warns")
	private WarnsConfiguration warnsConfiguration = new WarnsConfiguration();
	@JsonProperty("ideaChannels")
	private List<ChannelConfiguration> ideaChannels = new ArrayList<>();
	@JsonProperty("npXpChannels")
	private List<ChannelConfiguration> noXpChannels = new ArrayList<>();
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
	private List<RemoveRoleConfiguration> removeRoles = new ArrayList<>();
	@JsonProperty("trombinoscope")
	private TrombinoscopeConfiguration trombinoscopeConfiguration = new TrombinoscopeConfiguration();
	
	public GuildConfiguration(){
	}
	
	GuildConfiguration(final long guildId){
		this.guildId = guildId;
	}
	
	public void removeRole(@Nonnull RemoveRoleConfiguration value){
		this.removeRoles.add(value);
	}
	
	@Nonnull
	public AniListConfiguration getAniListConfiguration(){
		return aniListConfiguration;
	}
	
	@Nonnull
	public List<RoleConfiguration> getAutoRoles(){
		return this.autoRoles;
	}
	
	public void setAutoRoles(@Nonnull List<RoleConfiguration> autoRoles){
		this.autoRoles = autoRoles;
	}
	
	@Nonnull
	public Optional<RoleConfiguration> getDjRole(){
		return Optional.ofNullable(djRole);
	}
	
	public void setDjRole(@Nullable RoleConfiguration djRole){
		this.djRole = djRole;
	}
	
	@Nonnull
	public List<ChannelConfiguration> getIdeaChannels(){
		return this.ideaChannels;
	}
	
	public void setIdeaChannels(@Nonnull List<ChannelConfiguration> ideaChannels){
		this.ideaChannels = ideaChannels;
	}
	
	@Nonnull
	public List<RoleConfiguration> getModeratorRoles(){
		return this.moderatorRoles;
	}
	
	public void setModeratorRoles(@Nonnull List<RoleConfiguration> moderatorRoles){
		this.moderatorRoles = moderatorRoles;
	}
	
	@Nonnull
	public NicknameConfiguration getNicknameConfiguration(){
		return this.nicknameConfiguration;
	}
	
	@Nonnull
	public List<ChannelConfiguration> getNoXpChannels(){
		return this.noXpChannels;
	}
	
	public void setNoXpChannels(@Nonnull List<ChannelConfiguration> noXpChannels){
		this.noXpChannels = noXpChannels;
	}
	
	@Nonnull
	public ParticipationConfig getParticipationConfiguration(){
		return this.participationConfig;
	}
	
	@Nonnull
	public Optional<String> getPrefix(){
		return Optional.ofNullable(this.prefix);
	}
	
	public void setPrefix(@Nullable String prefix){
		this.prefix = prefix;
	}
	
	@Nonnull
	public QuestionsConfiguration getQuestionsConfiguration(){
		return this.questionsConfiguration;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getQuizChannel(){
		return Optional.ofNullable(this.quizChannel);
	}
	
	public void setQuizChannel(@Nullable ChannelConfiguration channel){
		this.quizChannel = channel;
	}
	
	@Nonnull
	public List<RemoveRoleConfiguration> getRemoveRoles(){
		return this.removeRoles;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getReportChannel(){
		return Optional.ofNullable(this.reportChannel);
	}
	
	public void setReportChannel(@Nullable ChannelConfiguration channel){
		this.reportChannel = channel;
	}
	
	@Nonnull
	public TrombinoscopeConfiguration getTrombinoscopeConfiguration(){
		return this.trombinoscopeConfiguration;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getTwitchChannel(){
		return Optional.ofNullable(this.twitchChannel);
	}
	
	public void setTwitchChannel(@Nullable ChannelConfiguration channel){
		this.twitchChannel = channel;
	}
	
	@Nonnull
	public WarnsConfiguration getWarnsConfiguration(){
		return warnsConfiguration;
	}
}
