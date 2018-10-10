package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.settings.configs.AniListChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListCodeConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListLastAccessConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListChange;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListMediaType;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListScheduledRunner implements Runnable{
	private static final Logger LOGGER = LoggerFactory.getLogger(AniListScheduledRunner.class);
	private static final String QUERY = "query($userID: Int, $page: Int, $perPage: Int, $date: Int){\n" + "  Page (page: $page, perPage: $perPage) {\n" + "    pageInfo {\n" + "      total\n" + "      currentPage\n" + "      lastPage\n" + "      hasNextPage\n" + "      perPage\n" + "    }\n" + "  \tactivities(userId: $userID, createdAt_greater: $date){\n" + "      ... on ListActivity{\n" + "        userId\n" + "        type\n" + "        createdAt\n" + "        progress\n" + "        siteUrl\n" + "        media {\n" + "          id\n" + "          title {\n" + "            userPreferred\n" + "          }\n" + "          season\n" + "          type\n" + "          format\n" + "          status\n" + "          episodes\n" + "          chapters\n" + "          isAdult\n" + "          coverImage{\n" + "            large\n" + "          }\n" + "          siteUrl" + "        }\n" + "      }\n" + "    }\n" + "  }\n" + "}";
	private final JDA jda;
	
	public AniListScheduledRunner(final JDA jda){
		getLogger(null).info("Creating AniList runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		LOGGER.info("Starting AniList runner");
		try{
			final var channels = new ArrayList<TextChannel>();
			final var userChanges = new HashMap<User, List<AniListChange>>();
			for(final var guild : jda.getGuilds()){
				final var channel = new AniListChannelConfig(guild).getObject(null);
				if(Objects.nonNull(channel)){
					channels.add(channel);
					final var tokens = new AniListCodeConfig(guild);
					final var tokensMap = tokens.getAsMap();
					for(final var userID : tokensMap.keySet()){
						final var member = guild.getMemberById(userID);
						if(!userChanges.containsKey(member.getUser())){
							try{
								userChanges.put(member.getUser(), getChanges(member, tokensMap.get(userID)));
							}
							catch(final Exception e){
								LOGGER.error("Error fetching user {} on AniList", member, e);
							}
						}
					}
				}
			}
			LOGGER.info("AniList API done");
			for(final var user : userChanges.keySet()){
				final var changes = userChanges.get(user);
				for(final var change : changes){
					final var message = buildMessage(user, change);
					channels.forEach(c -> Actions.sendMessage(c, message));
				}
			}
			
			LOGGER.info("AniList runner done");
		}
		catch(final Exception e){
			LOGGER.error("Error in AniList runner", e);
		}
	}
	
	private List<AniListChange> getChanges(final Member member, final String code) throws Exception{
		LOGGER.info("Fetching user {}", member);
		final var userInfoConf = new AniListLastAccessConfig(member.getGuild());
		final var userInfo = userInfoConf.getValue(member.getUser().getIdLong());
		final var variables = new JSONObject();
		variables.put("userID", Integer.parseInt(userInfo.get("userId")));
		variables.put("page", 1);
		variables.put("perPage", 50);
		variables.put("date", Integer.parseInt(userInfo.get("lastFetch")));
		final var jsonResult = AniListUtils.getQuery(member, code, QUERY, variables);
		LOGGER.warn("ANILIST RESULT: {}", jsonResult.toString());
		final var changes = new ArrayList<AniListChange>();
		for(final var change : jsonResult.getJSONObject("data").getJSONObject("Page").getJSONArray("activities")){
			changes.add(buildChange((JSONObject) change));
		}
		changes.stream().map(AniListChange::getCreatedAt).mapToLong(Date::getTime).max().ifPresent(val -> userInfoConf.addValue(member.getUser().getIdLong(), "lastFetch", "" + (val / 1000L)));
		return changes;
	}
	
	private MessageEmbed buildMessage(final User user, final AniListChange change){
		final var builder = new EmbedBuilder();
		
		builder.setAuthor(user.getName(), change.getUrl().toString(), user.getAvatarUrl());
		builder.setColor(change.getType().getColor());
		builder.setTimestamp(change.getCreatedAt().toInstant());
		builder.setTitle(change.getMedia().getTitle());
		
		if(Objects.isNull(change.getProgress())){
			builder.setDescription("Added to list");
		}
		else{
			builder.setDescription(StringUtils.capitalize(change.getMedia().getProgressType(change.getProgress().contains("-"))) + " " + change.getProgress());
		}
		builder.addField("Format", Optional.ofNullable(change.getMedia().getFormat()).map(Enum::name).orElse("UNKNOWN"), true);
		builder.addField("Status", Optional.ofNullable(change.getMedia().getStatus()).map(Enum::name).orElse("UNKNOWN"), true);
		if(change.getMedia().getType() == AniListMediaType.ANIME){
			Optional.ofNullable(change.getMedia().getSeason()).map(Enum::name).ifPresent(val -> builder.addField("Season", val, true));
			Optional.ofNullable(change.getMedia().getEpisodes()).map(Object::toString).ifPresent(val -> builder.addField("Episodes", val, true));
		}
		else if(change.getMedia().getType() == AniListMediaType.MANGA){
			Optional.ofNullable(change.getMedia().getChapters()).map(Object::toString).ifPresent(val -> builder.addField("Chapters", val, true));
		}
		if(change.getMedia().isAdult()){
			builder.addField("Adult content", "true", true);
		}
		builder.addField("Link", change.getMedia().getUrl().toString(), false);
		builder.setThumbnail(change.getMedia().getCoverUrl().toString());
		
		return builder.build();
	}
	
	private AniListChange buildChange(final JSONObject change) throws Exception{
		return AniListChange.fromJSON(change);
	}
}
