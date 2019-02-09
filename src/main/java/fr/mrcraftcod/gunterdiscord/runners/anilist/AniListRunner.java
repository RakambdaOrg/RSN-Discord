package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.settings.configs.AniListAccessTokenConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListLastAccessConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListDatedObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListPagedQuery;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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
public interface AniListRunner<T extends AniListObject, U extends AniListPagedQuery<T>>{
	default void runQueryOnEveryUserAndDefaultChannels(){
		final var channels = getJDA().getGuilds().stream().map(g -> new AniListChannelConfig(g).getObject(null)).filter(Objects::nonNull).collect(Collectors.toList());
		final var members = channels.stream().flatMap(c -> new AniListAccessTokenConfig(c.getGuild()).getAsMap().keySet().stream().map(k -> c.getGuild().getMemberById(k))).collect(Collectors.toList());
		runQuery(members, channels);
	}
	
	default void runQuery(final List<Member> members, final List<TextChannel> channels){
		getLogger(null).info("Starting AniList {} runner", getRunnerName());
		try{
			final var userElements = new HashMap<User, List<T>>();
			for(var member : members){
				if(!userElements.containsKey(member.getUser())){
					try{
						userElements.put(member.getUser(), getElements(member));
					}
					catch(final Exception e){
						getLogger(member.getGuild()).error("Error fetching user {} on AniList", member, e);
					}
				}
			}
			getLogger(null).debug("AniList API done");
			sendMessages(channels, userElements);
			
			getLogger(null).info("AniList {} runner done", getRunnerName());
		}
		catch(final Exception e){
			getLogger(null).error("Error in AniList {} runner", getRunnerName(), e);
		}
	}
	
	default void sendMessages(final List<TextChannel> channels, final Map<User, List<T>> userElements){
		if(sortedByUser()){
			for(final var user : userElements.keySet()){
				final var element = userElements.get(user);
				element.stream().sorted().map(change -> buildMessage(user, change)).<Consumer<? super TextChannel>> map(message -> c -> Actions.sendMessage(c, message)).forEach(channels::forEach);
			}
		}
		else{
			userElements.entrySet().stream().flatMap(es -> es.getValue().stream().map(val -> Map.entry(es.getKey(), val))).sorted(Comparator.comparing(Map.Entry::getValue)).map(change -> buildMessage(change.getKey(), change.getValue())).<Consumer<? super TextChannel>> map(message -> c -> Actions.sendMessage(c, message)).forEach(channels::forEach);
		}
	}
	
	default boolean sortedByUser(){
		return false;
	}
	
	String getRunnerName();
	
	JDA getJDA();
	
	default List<T> getElements(final Member member) throws Exception{
		getLogger(member.getGuild()).debug("Fetching user {}", member);
		final var userInfoConf = new AniListLastAccessConfig(member.getGuild());
		final var userInfo = userInfoConf.getValue(member.getUser().getIdLong());
		var elementList = initQuery(userInfo).getResult(member);
		if(keepOnlyNew()){
			final var baseDate = new Date(Optional.ofNullable(userInfo.getOrDefault("lastFetch" + getFetcherID(), null)).map(Integer::parseInt).orElse(0) * 1000L);
			elementList = elementList.stream().filter(e -> e instanceof AniListDatedObject).filter(e -> ((AniListDatedObject) e).getDate().after(baseDate)).collect(Collectors.toList());
		}
		elementList.stream().filter(e -> e instanceof AniListDatedObject).map(e -> (AniListDatedObject) e).map(AniListDatedObject::getDate).mapToLong(Date::getTime).max().ifPresent(val -> {
			getLogger(member.getGuild()).debug("New last fetched date for {}: {}", member, new Date(val));
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
