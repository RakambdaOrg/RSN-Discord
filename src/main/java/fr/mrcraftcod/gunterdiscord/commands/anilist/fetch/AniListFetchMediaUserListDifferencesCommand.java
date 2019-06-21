package fr.mrcraftcod.gunterdiscord.commands.anilist.fetch;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.runners.anilist.AniListRunner;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.anilist.list.AniListMediaUserList;
import fr.mrcraftcod.gunterdiscord.utils.anilist.media.AniListMediaType;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListMediaUserListPagedQuery;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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
	static class AniListMediaUserListDifferencesRunner implements AniListRunner<AniListMediaUserList, AniListMediaUserListPagedQuery>{
		private final JDA jda;
		private final AniListMediaType type;
		
		AniListMediaUserListDifferencesRunner( final JDA jda,  final AniListMediaType type){
			this.jda = jda;
			this.type = type;
		}
		
		@Override
		public void sendMessages( @Nonnull final List<TextChannel> channels,  @Nonnull final Map<User, List<AniListMediaUserList>> userMedias){
			for(final var user : userMedias.keySet()){
				userMedias.keySet().parallelStream().filter(user2 -> !Objects.equals(user, user2)).forEach(userCompare -> {
					final var user1Iterator = userMedias.get(user).iterator();
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
			userMedias.entrySet().stream().flatMap(es -> es.getValue().stream().map(val -> ImmutablePair.of(es.getKey(), val))).filter(pair -> pair.getValue().getMedia().getType().equals(this.type)).sorted(Comparator.comparing(Map.Entry::getValue)).map(change -> buildMessage(change.getKey(), change.getValue())).<Consumer<? super TextChannel>> map(message -> channel -> Actions.sendMessage(channel, message)).forEach(channels::forEach);
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
		public AniListMediaUserListPagedQuery initQuery( @Nonnull final Map<String, String> userInfo){
			return new AniListMediaUserListPagedQuery(Integer.parseInt(userInfo.get("userId")));
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
	public void addHelp( @Nonnull final Guild guild,  @Nonnull final EmbedBuilder embedBuilder){
		super.addHelp(guild, embedBuilder);
		embedBuilder.addField("filter", "What kind of media to get the differences", false);
		embedBuilder.addField("user1", "Mention of the first user to compare", false);
		embedBuilder.addField("user2", "Mention of the second user to compare", false);
	}
	
	
	@Nonnull
	@Override
	public CommandResult execute( @Nonnull final GuildMessageReceivedEvent event,  @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.size() < 3 || event.getMessage().getMentionedUsers().size() < 2){
			final var embedBuilder = Utilities.buildEmbed(event.getAuthor(), Color.RED, "Invalid parameters");
			embedBuilder.addField("Reason", "Please mention 2 users to compare", false);
			Actions.reply(event, embedBuilder.build());
			return CommandResult.SUCCESS;
		}
		final var type = AniListMediaType.valueOf(args.poll());
		final var runner = new AniListMediaUserListDifferencesRunner(event.getJDA(), type);
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
