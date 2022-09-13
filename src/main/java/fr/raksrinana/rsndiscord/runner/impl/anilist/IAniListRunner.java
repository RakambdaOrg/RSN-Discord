package fr.raksrinana.rsndiscord.runner.impl.anilist;

import fr.raksrinana.rsndiscord.api.anilist.data.IAniListDatedObject;
import fr.raksrinana.rsndiscord.api.anilist.data.IAniListObject;
import fr.raksrinana.rsndiscord.api.anilist.query.IPagedQuery;
import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.awt.Color.RED;
import static java.util.stream.Collectors.toSet;

@Log4j2
public abstract class IAniListRunner<T extends IAniListObject, U extends IPagedQuery<T>> implements IScheduledRunner{
	@Override
	public void executeGlobal(@NotNull JDA jda){
		runQueryOnDefaultUsersChannels(jda);
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild){
	}
	
	public void runQueryOnDefaultUsersChannels(@NotNull JDA jda){
		runQuery(jda, getMembers(jda), getChannels(jda));
	}
	
	public void runQuery(@NotNull JDA jda, @NotNull Set<Member> members, @NotNull Set<TextChannel> channels){
		var userElements = new HashMap<User, Set<T>>();
		for(var member : members){
			userElements.computeIfAbsent(member.getUser(), user -> {
				try{
					return getElements(member);
				}
				catch(Exception e){
					log.error("Error fetching user {} on AniList", member, e);
				}
				return null;
			});
		}
		log.debug("AniList API done");
		sendMessages(jda, channels, userElements);
	}
	
	@NotNull
	protected Set<T> getElements(@NotNull Member member) throws Exception{
		var user = member.getUser();
		log.debug("Fetching user {}", member);
		
		var elementList = initQuery(member).getResult(member);
		if(!isKeepOnlyNew()){
			return elementList;
		}
		
		var aniListGeneral = Settings.getGeneral().getAniList();
		var baseDate = aniListGeneral.getLastAccess(getFetcherID(), user.getIdLong())
				.map(UserDateConfiguration::getDate);
		
		elementList = elementList.stream()
				.filter(IAniListDatedObject.class::isInstance)
				.filter(e -> baseDate.map(date -> ((IAniListDatedObject) e).getDate().isAfter(date)).orElse(true))
				.collect(toSet());
		elementList.stream()
				.filter(IAniListDatedObject.class::isInstance)
				.map(IAniListDatedObject.class::cast)
				.map(IAniListDatedObject::getDate)
				.max(ZonedDateTime::compareTo)
				.ifPresent(val -> {
					log.debug("New last fetched date for {} on section {}: {} (last was {})", member, getFetcherID(), val, baseDate);
					aniListGeneral.setLastAccess(user, getFetcherID(), val);
				});
		return elementList;
	}
	
	protected void sendMessages(@NotNull JDA jda, @NotNull Set<TextChannel> channels, @NotNull Map<User, Set<T>> userElements){
		userElements.entrySet().stream()
				.flatMap(entry -> entry.getValue().stream().map(val -> Map.entry(entry.getKey(), val)))
				.sorted(Map.Entry.comparingByValue())
				.forEach(change -> channels.stream()
						.filter(channel -> shouldSendTo(channel, change.getKey()))
						.forEach(channel -> {
							var embed = buildMessage(channel.getGuild(), change.getKey(), change.getValue());
							JDAWrappers.message(channel, embed).submit();
						}));
	}
	
	@NotNull
	protected abstract U initQuery(@NotNull Member member);
	
	@NotNull
	public abstract String getFetcherID();
	
	protected boolean shouldSendTo(@NotNull TextChannel channel, @NotNull User user){
		return Settings.getGeneral().getAniList().isRegisteredOn(channel.getGuild(), user);
	}
	
	@NotNull
	protected MessageEmbed buildMessage(@NotNull Guild guild, User user, @NotNull T change){
		var builder = new EmbedBuilder();
		
		try{
			var author = Optional.ofNullable(user).orElse(guild.getJDA().getSelfUser());
			builder.setAuthor(author.getName(), change.getUrl().toString(), author.getAvatarUrl());
			change.fillEmbed(guild, builder);
		}
		catch(Exception e){
			log.error("Error building message for {}", getName(), e);
			builder.addField("Error", e.getClass().getName() + " => " + e.getMessage(), false)
					.setColor(RED);
		}
		return builder.build();
	}
	
	protected abstract boolean isKeepOnlyNew();
	
	@NotNull
	protected abstract Set<TextChannel> getChannels(@NotNull JDA jda);
	
	@NotNull
	protected Set<Member> getMembers(JDA jda){
		return jda.getGuilds().stream()
				.flatMap(guild -> Settings.getGeneral().getAniList().getRegisteredMembers(guild).stream())
				.collect(toSet());
	}
}
