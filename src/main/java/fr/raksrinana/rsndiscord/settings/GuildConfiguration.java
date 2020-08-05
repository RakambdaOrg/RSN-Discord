package fr.raksrinana.rsndiscord.settings;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.guild.*;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class GuildConfiguration implements CompositeConfiguration{
	@JsonProperty("locale")
	@Setter
	@Getter
	private Locale locale = Locale.ENGLISH;
	@JsonProperty("schedules")
	@JsonAlias({"reminders"})
	private List<ScheduleConfiguration> schedules = new ArrayList<>();
	@JsonProperty("aniList")
	@Getter
	private AniListConfiguration aniListConfiguration = new AniListConfiguration();
	@JsonProperty("trakt")
	@Getter
	private TraktConfiguration traktConfiguration = new TraktConfiguration();
	@JsonProperty("nickname")
	@Getter
	private NicknameConfiguration nicknameConfiguration = new NicknameConfiguration();
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
	private ParticipationConfiguration participationConfiguration = new ParticipationConfiguration();
	@JsonProperty("trombinoscope")
	@Getter
	private TrombinoscopeConfiguration trombinoscope = new TrombinoscopeConfiguration();
	@JsonProperty("externalTodos")
	@Getter
	private ExternalTodosConfiguration externalTodos = new ExternalTodosConfiguration();
	@JsonProperty("leavingRoles")
	@Getter
	private LeavingRolesConfiguration leavingRolesConfiguration = new LeavingRolesConfiguration();
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
	private RandomKickConfiguration randomKick = new RandomKickConfiguration();
	@JsonProperty("birthdays")
	@Getter
	@Setter
	private BirthdaysConfiguration birthdays = new BirthdaysConfiguration();
	
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
	
	public void addSchedule(@NonNull ScheduleConfiguration schedule){
		this.schedules.add(schedule);
	}
	
	public Optional<ChannelConfiguration> getLogChannel(){
		return Optional.ofNullable(this.logChannel);
	}
	
	public Optional<ChannelConfiguration> getGeneralChannel(){
		return Optional.of(generalChannel);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getAnnounceStartChannel(){
		return Optional.ofNullable(this.announceStartChannel);
	}
	
	public Optional<CategoryConfiguration> getArchiveCategory(){
		return Optional.ofNullable(archiveCategory);
	}
	
	public Iterator<WaitingReactionMessageConfiguration> getMessagesAwaitingReaction(){
		return this.messagesAwaitingReaction.iterator();
	}
	
	@NonNull
	public Optional<String> getPrefix(){
		return Optional.ofNullable(this.prefix);
	}
	
	public List<ScheduleConfiguration> getSchedules(){
		return new LinkedList<>(this.schedules);
	}
}
