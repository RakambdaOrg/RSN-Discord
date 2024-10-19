package fr.rakambda.rsndiscord.spring.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "bot")
public class ApplicationSettings{
	private boolean development;
	private List<Long> developmentGuilds = new LinkedList<>();
	private String botToken;
	private String youtubeRefreshToken;
	private String amqpPrefix;
	@NestedConfigurationProperty
	private AnilistSettings anilist;
	@NestedConfigurationProperty
	private TwitterSettings twitter;
	@NestedConfigurationProperty
	private TheMovieDbSettings theMovieDb;
	@NestedConfigurationProperty
	private TraktSettings trakt;
	@NestedConfigurationProperty
	private SimklSettings simkl;
}
