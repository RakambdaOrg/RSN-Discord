package fr.raksrinana.rsndiscord.settings.impl;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.schedule.api.IScheduleHandler;
import fr.raksrinana.rsndiscord.settings.api.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.HermitcraftConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.TraktConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.TwitterConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.anilist.AniListConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.reaction.ReactionsConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.guild.rss.RSSConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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
	@JsonProperty("guildId")
	@Getter
	private long guildId;
	@JsonProperty("musicVolume")
	@Getter
	@Setter
	private int musicVolume = 100;
	@JsonProperty("reactions")
	@Getter
	@Setter
	private ReactionsConfiguration reactionsConfiguration = new ReactionsConfiguration();
	@JsonProperty("twitter")
	@Getter
	@Setter
	private TwitterConfiguration twitterConfiguration = new TwitterConfiguration();
	@JsonProperty("mediaReactionMessages")
	@Getter
	@Setter
	private List<MessageConfiguration> mediaReactionMessages = new LinkedList<>();
	@JsonProperty("scheduleHandlers")
	@JsonAlias("scheduleActionHandlers")
	@Getter
	private ConcurrentHashMap<String, IScheduleHandler> scheduleHandlers = new ConcurrentHashMap<>();
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
	
	@NotNull
	public Optional<Locale> getLocale(){
		return ofNullable(locale);
	}
}
