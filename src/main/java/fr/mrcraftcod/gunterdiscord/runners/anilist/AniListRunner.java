package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.settings.configs.AniListAccessTokenConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListLastAccessConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListDatedObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListPagedQuery;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public interface AniListRunner<T extends AniListDatedObject, U extends AniListPagedQuery<T>> extends Runnable{
	
	@Override
	default void run(){
		getLogger(null).info("Starting AniList {} runner", getRunnerName());
		try{
			final var channels = new ArrayList<TextChannel>();
			final var userElements = new HashMap<User, List<T>>();
			for(final var guild : getJDA().getGuilds()){
				final var channel = new AniListChannelConfig(guild).getObject(null);
				if(Objects.nonNull(channel)){
					channels.add(channel);
					final var tokens = new AniListAccessTokenConfig(guild);
					for(final var userID : tokens.getAsMap().keySet()){
						final var member = guild.getMemberById(userID);
						if(!userElements.containsKey(member.getUser())){
							try{
								userElements.put(member.getUser(), getElements(member));
							}
							catch(final Exception e){
								getLogger(guild).error("Error fetching user {} on AniList", member, e);
							}
						}
					}
				}
			}
			getLogger(null).info("AniList API done");
			sendMessages(channels, userElements);
			
			getLogger(null).info("AniList {} runner done", getRunnerName());
		}
		catch(final Exception e){
			getLogger(null).error("Error in AniList {} runner", getRunnerName(), e);
		}
	}
	
	default void sendMessages(final List<TextChannel> channels, final Map<User, List<T>> userElements){
		for(final var user : userElements.keySet()){
			final var element = userElements.get(user);
			element.stream().sorted(Comparator.comparing(AniListDatedObject::getDate)).map(change -> buildMessage(user, change)).<Consumer<? super TextChannel>> map(message -> c -> Actions.sendMessage(c, message)).forEach(channels::forEach);
		}
	}
	
	String getRunnerName();
	
	JDA getJDA();
	
	default List<T> getElements(final Member member) throws Exception{
		getLogger(member.getGuild()).info("Fetching user {}", member);
		final var userInfoConf = new AniListLastAccessConfig(member.getGuild());
		final var userInfo = userInfoConf.getValue(member.getUser().getIdLong());
		var elementList = initQuery(userInfo).getResult(member);
		if(keepOnlyNew()){
			final var baseDate = new Date(Optional.ofNullable(userInfo.getOrDefault("lastFetch" + getFetcherID(), null)).map(Integer::parseInt).orElse(0) * 1000L);
			elementList = elementList.stream().filter(e -> e.getDate().after(baseDate)).collect(Collectors.toList());
		}
		elementList.stream().map(AniListDatedObject::getDate).mapToLong(Date::getTime).max().ifPresent(val -> {
			getLogger(member.getGuild()).info("New last fetched date for {}: {}", member, new Date(val));
			userInfoConf.addValue(member.getUser().getIdLong(), "lastFetch" + getFetcherID(), "" + (val / 1000L));
		});
		return elementList;
	}
	
	default MessageEmbed buildMessage(final User user, final T change){
		final var builder = new EmbedBuilder();
		if(Objects.isNull(user)){
			builder.setAuthor(getJDA().getSelfUser().getName(), change.getUrl(), getJDA().getSelfUser().getAvatarUrl());
		}
		else{
			builder.setAuthor(user.getName(), change.getUrl(), user.getAvatarUrl());
		}
		change.fillEmbed(builder);
		return builder.build();
	}
	
	U initQuery(Map<String, String> userInfo);
	
	boolean keepOnlyNew();
	
	String getFetcherID();
}
