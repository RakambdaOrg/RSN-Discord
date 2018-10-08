package fr.mrcraftcod.gunterdiscord.runners;

import com.mashape.unirest.http.exceptions.UnirestException;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListTokenConfig;
import fr.mrcraftcod.utils.http.requestssenders.post.JSONPostRequestSender;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListScheduledRunner implements Runnable{
	public static final Logger LOGGER = LoggerFactory.getLogger(AniListScheduledRunner.class);
	private final JDA jda;
	
	public AniListScheduledRunner(final JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void run(){
		LOGGER.info("Starting AniList scheduler");
		try{
			final var userChanges = new HashMap<User, List<String>>();
			for(final var guild : jda.getGuilds()){
				final var channel = new AniListChannelConfig(guild).getObject(null);
				if(Objects.nonNull(channel)){
					final var tokens = new AniListTokenConfig(guild).getAsMap();
					for(final var userID : tokens.keySet()){
						final var user = jda.getUserById(userID);
						if(!userChanges.containsKey(user)){
							userChanges.put(user, getChanges(user, tokens.get(userID)));
						}
					}
				}
			}
			LOGGER.info("AniList scheduler done");
		}
		catch(final Exception e){
			LOGGER.error("Error in AniList scheduler", e);
		}
	}
	
	private List<String> getChanges(final User user, final String token) throws MalformedURLException, URISyntaxException, UnirestException{
		LOGGER.info("Fetching user {}", user);
		final var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + token);
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		final var handler = new JSONPostRequestSender(new URL("https://graphql.anilist.co"), headers).getRequestHandler();
		if(handler.getStatus() == 200){
			final var jsonResult = handler.getResult();
			LOGGER.warn("AUTH RESULT: {}", jsonResult.toString());
		}
		else{
			LOGGER.error("Error getting API access, HTTP code {}", handler.getStatus());
		}
		return new ArrayList<>();
	}
}
