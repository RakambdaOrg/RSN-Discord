package fr.raksrinana.rsndiscord.command2.impl.external.anilist;

import fr.raksrinana.rsndiscord.api.anilist.AniListApi;
import fr.raksrinana.rsndiscord.api.anilist.data.list.MediaList;
import fr.raksrinana.rsndiscord.api.anilist.data.media.MediaType;
import fr.raksrinana.rsndiscord.api.anilist.query.MediaListPagedQuery;
import fr.raksrinana.rsndiscord.runner.impl.anilist.IAniListRunner;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MINUTES;

class MediaListDifferencesRunner extends IAniListRunner<MediaList, MediaListPagedQuery>{
	@Getter
	private final JDA jda;
	private final MediaType type;
	private final TextChannel channel;
	private final Member member1;
	private final Member member2;
	
	public MediaListDifferencesRunner(@NotNull JDA jda, @NotNull MediaType type, @NotNull TextChannel channel, @NotNull Member member1, @NotNull Member member2){
		this.jda = jda;
		this.type = type;
		this.channel = channel;
		this.member1 = member1;
		this.member2 = member2;
	}
	
	@Override
	public void executeGlobal(@NotNull JDA jda){
		runQueryOnDefaultUsersChannels(jda);
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@NotNull
	@Override
	public String getName(){
		return "AniList list differences";
	}
	
	@Override
	public long getPeriod(){
		return 0;
	}
	
	@Override
	@NotNull
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
	
	@Override
	@NotNull
	public Set<TextChannel> getChannels(@NotNull JDA jda){
		return Set.of(channel);
	}
	
	@Override
	@NotNull
	public Set<Member> getMembers(@NotNull JDA jda){
		return Set.of(member1, member2);
	}
	
	@NotNull
	@Override
	public String getFetcherID(){
		return "differences";
	}
	
	@Override
	public void sendMessages(@NotNull JDA jda, @NotNull Set<TextChannel> channels, @NotNull Map<User, Set<MediaList>> userMedias){
		if(userMedias.containsKey(member1.getUser()) && userMedias.containsKey(member2.getUser())){
			var user2Medias = userMedias.get(member2.getUser());
			userMedias.get(member1.getUser()).stream()
					.filter(user1Media -> user1Media.getMedia().getType().equals(type))
					.filter(user2Medias::contains)
					.sorted()
					.map(commonMedia -> buildMessage(channel.getGuild(), null, commonMedia))
					.forEach(embed -> JDAWrappers.message(channel, embed).submit());
		}
	}
	
	@Override
	public boolean isKeepOnlyNew(){
		return false;
	}
	
	@NotNull
	@Override
	public MediaListPagedQuery initQuery(@NotNull Member member){
		return new MediaListPagedQuery(AniListApi.getUserId(member).orElseThrow());
	}
}
