package fr.raksrinana.rsndiscord.commands.anilist.fetch;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.runners.anilist.AniListRunner;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.list.MediaList;
import fr.raksrinana.rsndiscord.utils.anilist.media.MediaType;
import fr.raksrinana.rsndiscord.utils.anilist.queries.MediaListPagedQuery;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListFetchMediaUserListDifferencesCommand extends BasicCommand{
	static class AniListMediaUserListDifferencesRunner implements AniListRunner<MediaList, MediaListPagedQuery>{
		private final JDA jda;
		private final MediaType type;
		private final TextChannel channel;
		
		AniListMediaUserListDifferencesRunner(final JDA jda, final MediaType type, final TextChannel channel){
			this.jda = jda;
			this.type = type;
			this.channel = channel;
		}
		
		@Override
		public void sendMessages(@Nonnull final List<TextChannel> channels, @Nonnull final Map<User, List<MediaList>> userMedias){
			for(final var entry : userMedias.entrySet()){
				userMedias.keySet().parallelStream().filter(user2 -> !Objects.equals(entry.getKey(), user2)).forEach(userCompare -> {
					final var user1Iterator = entry.getValue().iterator();
					while(user1Iterator.hasNext()){
						final var media = user1Iterator.next().getMedia();
						final var user2Iterator = userMedias.get(userCompare).iterator();
						while(user2Iterator.hasNext()){
							if(Objects.equals(media, user2Iterator.next().getMedia())){
								user1Iterator.remove();
								user2Iterator.remove();
								break;
							}
						}
					}
				});
			}
			userMedias.entrySet().stream().flatMap(es -> es.getValue().stream().map(val -> ImmutablePair.of(es.getKey(), val))).filter(pair -> pair.getValue().getMedia().getType() == this.type).sorted(Comparator.comparing(Map.Entry::getValue)).map(change -> this.buildMessage(change.getKey(), change.getValue())).<Consumer<? super TextChannel>> map(message -> channel -> Actions.sendMessage(channel, message)).forEach(channels::forEach);
		}
		
		@Override
		public List<TextChannel> getChannels(){
			return List.of(this.channel);
		}
		
		@Nonnull
		@Override
		public String getRunnerName(){
			return "differences";
		}
		
		@Nonnull
		@Override
		public JDA getJDA(){
			return this.jda;
		}
		
		@Nonnull
		@Override
		public MediaListPagedQuery initQuery(@Nonnull final Member member){
			return new MediaListPagedQuery(AniListUtils.getUserId(member).orElseThrow());
		}
		
		@Override
		public boolean keepOnlyNew(){
			return false;
		}
		
		@Nonnull
		@Override
		public String getFetcherID(){
			return "differences";
		}
	}
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AniListFetchMediaUserListDifferencesCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder embedBuilder){
		super.addHelp(guild, embedBuilder);
		embedBuilder.addField("filter", "What kind of media to get the differences", false);
		embedBuilder.addField("user1", "Mention of the first user to compare", false);
		embedBuilder.addField("user2", "Mention of the second user to compare", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.size() < 3 || event.getMessage().getMentionedUsers().size() < 2){
			final var embedBuilder = Utilities.buildEmbed(event.getAuthor(), Color.RED, "Invalid parameters");
			embedBuilder.addField("Reason", "Please mention 2 users to compare", false);
			Actions.reply(event, embedBuilder.build());
			return CommandResult.SUCCESS;
		}
		final var type = MediaType.valueOf(args.poll());
		final var runner = new AniListMediaUserListDifferencesRunner(event.getJDA(), type, event.getChannel());
		runner.runQuery(event.getMessage().getMentionedMembers(), List.of(event.getChannel()));
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user1> <@user2>";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "AniList fetch media user list differences";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		//noinspection SpellCheckingInspection
		return List.of("mediadiff", "d");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Fetch media user list differences from AniList";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
