package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.settings.configs.AniListAccessTokenConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListLastAccessConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list.AniListListActivity;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListListActivityPagedQuery;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import java.util.*;
import java.util.function.Consumer;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListActivityScheduledRunner implements Runnable{
	private final JDA jda;
	
	public AniListActivityScheduledRunner(final JDA jda){
		getLogger(null).info("Creating AniList list change runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		getLogger(null).info("Starting AniList list change runner");
		try{
			final var channels = new ArrayList<TextChannel>();
			final var userChanges = new HashMap<User, List<AniListListActivity>>();
			for(final var guild : jda.getGuilds()){
				final var channel = new AniListChannelConfig(guild).getObject(null);
				if(Objects.nonNull(channel)){
					channels.add(channel);
					final var tokens = new AniListAccessTokenConfig(guild);
					for(final var userID : tokens.getAsMap().keySet()){
						final var member = guild.getMemberById(userID);
						if(!userChanges.containsKey(member.getUser())){
							try{
								userChanges.put(member.getUser(), getChanges(member));
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
				changes.stream().sorted(Comparator.comparing(AniListListActivity::getDate)).map(change -> buildMessage(user, change)).<Consumer<? super TextChannel>> map(message -> c -> Actions.sendMessage(c, message)).forEach(channels::forEach);
			}
			
			getLogger(null).info("AniList list change runner done");
		}
		catch(final Exception e){
			getLogger(null).error("Error in AniList list change runner", e);
		}
	}
	
	private List<AniListListActivity> getChanges(final Member member) throws Exception{
		getLogger(member.getGuild()).info("Fetching user {}", member);
		final var userInfoConf = new AniListLastAccessConfig(member.getGuild());
		final var userInfo = userInfoConf.getValue(member.getUser().getIdLong());
		final var listActivities = new AniListListActivityPagedQuery(Integer.parseInt(userInfo.get("userId")), Integer.parseInt(userInfo.get("lastFetch"))).getResult(member);
		listActivities.stream().map(AniListListActivity::getDate).mapToLong(Date::getTime).max().ifPresent(val -> {
			getLogger(member.getGuild()).info("New last fetched date for {}: {}", member, new Date(val));
			userInfoConf.addValue(member.getUser().getIdLong(), "lastFetch", "" + (val / 1000L));
		});
		return listActivities;
	}
	
	private MessageEmbed buildMessage(final User user, final AniListListActivity change){
		final var builder = new EmbedBuilder();
		builder.setAuthor(user.getName(), change.getUrl(), user.getAvatarUrl());
		change.fillEmbed(builder);
		return builder.build();
	}
}
