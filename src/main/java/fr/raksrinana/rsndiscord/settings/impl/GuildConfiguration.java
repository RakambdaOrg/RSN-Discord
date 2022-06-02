package fr.raksrinana.rsndiscord.settings.impl;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.schedule.api.IScheduleHandler;
import fr.raksrinana.rsndiscord.settings.api.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.HermitcraftConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.JoinLeaveConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.RandomKickConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.TraktConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.TwitterConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.anilist.AniListConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.autoroles.LeavingRolesConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.birthday.BirthdaysConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.permission.PermissionsConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.reaction.ReactionsConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.rss.RSSConfiguration;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class GuildConfiguration implements ICompositeConfiguration{
	@JsonProperty("locale")
	@Setter
	private Locale locale;
	@JsonProperty("aniList")
	@Getter
	private final AniListConfiguration aniListConfiguration = new AniListConfiguration();
	@JsonProperty("trakt")
	@Getter
	private final TraktConfiguration traktConfiguration = new TraktConfiguration();
	@JsonProperty("hermitcraft")
	@Getter
	private final HermitcraftConfiguration hermitcraftConfiguration = new HermitcraftConfiguration();
	@JsonProperty("moderatorRoles")
	@Getter
	@Setter
	private Set<RoleConfiguration> moderatorRoles = new HashSet<>();
	@JsonProperty("autoThumbChannels")
	@JsonAlias("ideaChannels")
	@Getter
	@Setter
	private Set<ChannelConfiguration> autoThumbsChannels = new HashSet<>();
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
	private Set<WaitingReactionMessageConfiguration> messagesAwaitingReaction = new ConcurrentSkipListSet<>();
	@JsonProperty("reactions")
	@Getter
	@Setter
	private ReactionsConfiguration reactionsConfiguration = new ReactionsConfiguration();
	@JsonProperty("leavingRoles")
	@Getter
	private final LeavingRolesConfiguration leavingRolesConfiguration = new LeavingRolesConfiguration();
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
	@JsonProperty("twitter")
	@Getter
	@Setter
	private TwitterConfiguration twitterConfiguration = new TwitterConfiguration();
	@JsonProperty("joinLeave")
	@Getter
	@Setter
	private JoinLeaveConfiguration joinLeaveConfiguration = new JoinLeaveConfiguration();
	@JsonProperty("mediaReactionMessages")
	@Getter
	@Setter
	private List<MessageConfiguration> mediaReactionMessages = new LinkedList<>();
	@JsonProperty("scheduleHandlers")
	@JsonAlias("scheduleActionHandlers")
	@Getter
	private Map<String, IScheduleHandler> scheduleHandlers = new ConcurrentHashMap<>();
	@JsonProperty("rss")
	@Getter
	@Setter
	private RSSConfiguration rss = new RSSConfiguration();
	
	public GuildConfiguration(long guildId){
		this.guildId = guildId;
	}
	
	public void add(@NotNull IScheduleHandler scheduleHandler){
		scheduleHandlers.put(scheduleHandler.getSchedulerId(), scheduleHandler);
	}
	
	public void removeScheduleHandler(@NotNull String id){
		scheduleHandlers.remove(id);
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getAnnounceStartChannel(){
		return ofNullable(announceStartChannel);
	}
	
	@NotNull
	public Optional<CategoryConfiguration> getArchiveCategory(){
		return ofNullable(archiveCategory);
	}
	
	@NotNull
	public Optional<Locale> getLocale(){
		return ofNullable(locale);
	}
	
	@NotNull
	public Iterator<WaitingReactionMessageConfiguration> getMessagesAwaitingReaction(){
		return messagesAwaitingReaction.iterator();
	}
}
