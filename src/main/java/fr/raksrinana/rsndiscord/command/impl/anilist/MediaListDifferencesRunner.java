package fr.raksrinana.rsndiscord.command.impl.anilist;

import fr.raksrinana.rsndiscord.api.anilist.AniListApi;
import fr.raksrinana.rsndiscord.api.anilist.data.list.MediaList;
import fr.raksrinana.rsndiscord.api.anilist.data.media.MediaType;
import fr.raksrinana.rsndiscord.api.anilist.query.MediaListPagedQuery;
import fr.raksrinana.rsndiscord.runner.anilist.IAniListRunner;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MINUTES;

class MediaListDifferencesRunner implements IAniListRunner<MediaList, MediaListPagedQuery>{
	@Getter
	private final JDA jda;
	private final MediaType type;
	private final TextChannel channel;
	private final Member member1;
	private final Member member2;
	
	public MediaListDifferencesRunner(@NonNull final JDA jda, @NonNull final MediaType type, @NonNull final TextChannel channel, @NonNull final Member member1, @NonNull final Member member2){
		this.jda = jda;
		this.type = type;
		this.channel = channel;
		this.member1 = member1;
		this.member2 = member2;
	}
	
	@Override
	public void execute(){
		this.runQueryOnDefaultUsersChannels();
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "AniList list differences";
	}
	
	@Override
	public long getPeriod(){
		return 0;
	}
	
	@Override
	public Set<TextChannel> getChannels(){
		return Set.of(this.channel);
	}
	
	@Override
	public Set<Member> getMembers(){
		return Set.of(this.member1, this.member2);
	}
	
	@Override
	public void sendMessages(@NonNull final Set<TextChannel> channels, @NonNull final Map<User, Set<MediaList>> userMedias){
		if(userMedias.containsKey(this.member1.getUser()) && userMedias.containsKey(this.member2.getUser())){
			final var user2Medias = userMedias.get(this.member2.getUser());
			userMedias.get(this.member1.getUser()).stream()
					.filter(user1Media -> user1Media.getMedia().getType().equals(this.type))
					.filter(user2Medias::contains)
					.sorted()
					.map(commonMedia -> buildMessage(this.channel.getGuild(), null, commonMedia))
					.forEach(embed -> this.channel.sendMessage(embed).submit());
		}
	}
	
	@Override
	public @NonNull TimeUnit getPeriodUnit(){
		return MINUTES;
	}
	
	@NonNull
	@Override
	public MediaListPagedQuery initQuery(@NonNull final Member member){
		return new MediaListPagedQuery(AniListApi.getUserId(member).orElseThrow());
	}
	
	@Override
	public boolean isKeepOnlyNew(){
		return false;
	}
	
	@NonNull
	@Override
	public String getFetcherID(){
		return "differences";
	}
}
