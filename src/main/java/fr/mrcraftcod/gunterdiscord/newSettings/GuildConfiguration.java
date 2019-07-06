package fr.mrcraftcod.gunterdiscord.newSettings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.newSettings.guild.*;
import fr.mrcraftcod.gunterdiscord.newSettings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.newSettings.types.RemoveRoleConfiguration;
import fr.mrcraftcod.gunterdiscord.newSettings.types.RoleConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.RemoveRoleConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

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
	
	public GuildConfiguration mapOldConf(){
		final var guild = Main.getJDA().getGuildById(this.guildId);
		this.aniListConfiguration.mapOldConf(Objects.requireNonNull(guild));
		this.warnsConfiguration.mapOldConf(Objects.requireNonNull(guild));
		this.participationConfig.mapOldConf(Objects.requireNonNull(guild));
		this.nicknameConfiguration.mapOldConf(Objects.requireNonNull(guild));
		this.questionsConfiguration.mapOldConf(Objects.requireNonNull(guild));
		this.trombinoscopeConfiguration.mapOldConf(Objects.requireNonNull(guild));
		this.autoRoles.addAll(new AutoRolesConfig(guild).getAsList().orElse(List.of()).stream().map(RoleConfiguration::new).collect(Collectors.toList()));
		this.moderatorRoles.addAll(new ModoRolesConfig(guild).getAsList().orElse(List.of()).stream().map(RoleConfiguration::new).collect(Collectors.toList()));
		this.ideaChannels.addAll(new OnlyIdeasConfig(guild).getAsList().orElse(List.of()).stream().filter(Objects::nonNull).map(ChannelConfiguration::new).collect(Collectors.toList()));
		this.noXpChannels.addAll(new NoXPChannelsConfig(guild).getAsList().orElse(List.of()).stream().filter(Objects::nonNull).map(ChannelConfiguration::new).collect(Collectors.toList()));
		this.djRole = new DJRoleConfig(guild).getObject().map(RoleConfiguration::new).orElse(null);
		this.prefix = new PrefixConfig(guild).getObject().orElse(null);
		this.reportChannel = new ReportChannelConfig(guild).getObject().map(ChannelConfiguration::new).orElse(null);
		this.twitchChannel = new TwitchIRCChannelConfig(guild).getObject().map(ChannelConfiguration::new).orElse(null);
		this.quizChannel = new QuizChannelConfig(guild).getObject().map(ChannelConfiguration::new).orElse(null);
		this.removeRoles.addAll(new RemoveRoleConfig(guild).getAsMap().orElse(new HashMap<>()).entrySet().stream().flatMap(entry -> entry.getValue().entrySet().stream().map(entry2 -> new RemoveRoleConfiguration(guild.getJDA().getUserById(entry.getKey()), guild.getRoleById(entry2.getKey()), new Date(entry2.getValue())))).collect(Collectors.toList()));
		return this;
	}
	
	@Nonnull
	public TrombinoscopeConfiguration getTrombinoscopeConfiguration(){
		return this.trombinoscopeConfiguration;
	}
	
	@Nonnull
	public NicknameConfiguration getNicknameConfiguration(){
		return this.nicknameConfiguration;
	}
	
	@Nonnull
	public QuestionsConfiguration getQuestionsConfiguration(){
		return this.questionsConfiguration;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getReportChannel(){
		return Optional.ofNullable(this.reportChannel);
	}
	
	public void setReportChannel(@Nullable ChannelConfiguration channel){
		this.reportChannel = channel;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getTwitchChannel(){
		return Optional.ofNullable(this.twitchChannel);
	}
	
	public void setTwitchChannel(@Nullable ChannelConfiguration channel){
		this.twitchChannel = channel;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getQuizChannel(){
		return Optional.ofNullable(this.quizChannel);
	}
	
	public void setQuizChannel(@Nullable ChannelConfiguration channel){
		this.quizChannel = channel;
	}
	
	@Nonnull
	public ParticipationConfig getParticipationConfiguration(){
		return this.participationConfig;
	}
	
	public void setNoXpChannels(@Nonnull List<ChannelConfiguration> noXpChannels){
		this.noXpChannels = noXpChannels;
	}
	
	public void setIdeaChannels(@Nonnull List<ChannelConfiguration> ideaChannels){
		this.ideaChannels = ideaChannels;
	}
	
	public void setModeratorRoles(@Nonnull List<RoleConfiguration> moderatorRoles){
		this.moderatorRoles = moderatorRoles;
	}
	
	@Nonnull
	public Optional<RoleConfiguration> getDjRole(){
		return Optional.ofNullable(djRole);
	}
	
	public void setDjRole(@Nullable RoleConfiguration djRole){
		this.djRole = djRole;
	}
	
	public void setAutoRoles(@Nonnull List<RoleConfiguration> autoRoles){
		this.autoRoles = autoRoles;
	}
	
	@Nonnull
	public AniListConfiguration getAniListConfiguration(){
		return aniListConfiguration;
	}
	
	@Nonnull
	public WarnsConfiguration getWarnsConfiguration(){
		return warnsConfiguration;
	}
	
	@Nonnull
	public List<RoleConfiguration> getAutoRoles(){
		return this.autoRoles;
	}
	
	@Nonnull
	public List<RoleConfiguration> getModeratorRoles(){
		return this.moderatorRoles;
	}
	
	@Nonnull
	public List<ChannelConfiguration> getNoXpChannels(){
		return this.noXpChannels;
	}
	
	@Nonnull
	public List<ChannelConfiguration> getIdeaChannels(){
		return this.ideaChannels;
	}
	
	@Nonnull
	public Optional<String> getPrefix(){
		return Optional.ofNullable(this.prefix);
	}
	
	public void setPrefix(@Nullable String prefix){
		this.prefix = prefix;
	}
}
