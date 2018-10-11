package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.settings.configs.AniListChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListCodeConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListLastAccessConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListUtils;
import fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list.AniListListActivity;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONObject;
import java.util.*;
import java.util.function.Consumer;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListScheduledRunner implements Runnable{
	private static final String QUERY_FEED = "query($userID: Int, $page: Int, $perPage: Int, $date: Int){\n" + "  Page (page: $page, perPage: $perPage) {\n" + "    pageInfo {\n" + "      total\n" + "      currentPage\n" + "      lastPage\n" + "      hasNextPage\n" + "      perPage\n" + "    }\n" + "  \tactivities(userId: $userID, createdAt_greater: $date){\n" + "      ... on ListActivity{\n" + "        userId\n" + "        type\n" + "        createdAt\n" + "        progress\n" + "        siteUrl\n" + "        media {\n" + "          id\n" + "          title {\n" + "            userPreferred\n" + "          }\n" + "          season\n" + "          type\n" + "          format\n" + "          status\n" + "          episodes\n" + "          chapters\n" + "          isAdult\n" + "          coverImage{\n" + "            large\n" + "          }\n" + "          siteUrl" + "        }\n" + "      }\n" + "    }\n" + "  }\n" + "}";
	private static final String QUERY_NOTIFICATIONS = "query ($page: Int, $perPage: Int) {\n" + "  Page(page: $page, perPage: $perPage) {\n" + "    pageInfo {\n" + "      total\n" + "      currentPage\n" + "      lastPage\n" + "      hasNextPage\n" + "      perPage\n" + "    }\n" + "    notifications{\n" + "      ... on AiringNotification {\n" + "        type\n" + "        episode\n" + "        createdAt\n" + "        media {\n" + "          id\n" + "        }\n" + "      }\n" + "    }\n" + "  }\n" + "}\n";
	private final JDA jda;
	
	public AniListScheduledRunner(final JDA jda){
		getLogger(null).info("Creating AniList runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		getLogger(null).info("Starting AniList runner");
		try{
			final var channels = new ArrayList<TextChannel>();
			final var userChanges = new HashMap<User, List<AniListListActivity>>();
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
								getLogger(guild).error("Error fetching user {} on AniList", member, e);
							}
						}
					}
				}
			}
			getLogger(null).info("AniList API done");
			for(final var user : userChanges.keySet()){
				final var changes = userChanges.get(user);
				changes.stream().sorted(Comparator.comparing(AniListListActivity::getCreatedAt)).map(change -> buildMessage(user, change)).<Consumer<? super TextChannel>> map(message -> c -> Actions.sendMessage(c, message)).forEach(channels::forEach);
			}
			
			getLogger(null).info("AniList runner done");
		}
		catch(final Exception e){
			getLogger(null).error("Error in AniList runner", e);
		}
	}
	
	private List<AniListListActivity> getChanges(final Member member, final String code) throws Exception{
		getLogger(member.getGuild()).info("Fetching user {}", member);
		final var userInfoConf = new AniListLastAccessConfig(member.getGuild());
		final var userInfo = userInfoConf.getValue(member.getUser().getIdLong());
		final var variables = new JSONObject();
		variables.put("userID", Integer.parseInt(userInfo.get("userId")));
		variables.put("page", 1);
		variables.put("perPage", 50);
		variables.put("date", Integer.parseInt(userInfo.get("lastFetch")));
		final var jsonResult = AniListUtils.getQuery(member, code, QUERY_FEED, variables);
		final var changes = new ArrayList<AniListListActivity>();
		for(final var change : jsonResult.getJSONObject("data").getJSONObject("Page").getJSONArray("activities")){
			changes.add(buildChange((JSONObject) change));
		}
		changes.stream().map(AniListListActivity::getCreatedAt).mapToLong(Date::getTime).max().ifPresent(val -> {
			getLogger(member.getGuild()).info("New last fetched date for {}: {}", member, new Date(val));
			userInfoConf.addValue(member.getUser().getIdLong(), "lastFetch", "" + (val / 1000L));
		});
		return changes;
	}
	
	private MessageEmbed buildMessage(final User user, final AniListListActivity change){
		final var builder = new EmbedBuilder();
		builder.setAuthor(user.getName(), change.getUrl().toString(), user.getAvatarUrl());
		change.fillEmbed(builder);
		return builder.build();
	}
	
	private AniListListActivity buildChange(final JSONObject change) throws Exception{
		return AniListListActivity.buildFromJSON(change);
	}
}
