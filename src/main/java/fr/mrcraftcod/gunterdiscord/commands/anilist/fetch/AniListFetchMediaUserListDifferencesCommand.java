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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
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
	class AniListMediaUserListDifferencesRunner implements AniListRunner<AniListMediaUserList, AniListMediaUserListPagedQuery>{
		private final JDA jda;
		private final AniListMediaType type;
		
		AniListMediaUserListDifferencesRunner(final JDA jda, final AniListMediaType type){
			this.jda = jda;
			this.type = type;
		}
		
		public void sendMessages(final List<TextChannel> channels, final Map<User, List<AniListMediaUserList>> userElements){
			for(final var user : userElements.keySet()){
				userElements.keySet().parallelStream().filter(user2 -> !Objects.equals(user, user2)).forEach(userCompare -> {
					final var iterator1 = userElements.get(user).iterator();
					while(iterator1.hasNext()){
						final var media = iterator1.next().getMedia();
						final var iterator2 = userElements.get(userCompare).iterator();
						while(iterator2.hasNext()){
							if(Objects.equals(media, iterator2.next().getMedia())){
								iterator1.remove();
								iterator2.remove();
								break;
							}
						}
					}
				});
			}
			userElements.entrySet().stream().flatMap(es -> es.getValue().stream().map(val -> Map.entry(es.getKey(), val))).filter(m -> Objects.nonNull(type) && m.getValue().getMedia().getType().equals(type)).sorted(Comparator.comparing(Map.Entry::getValue)).map(change -> buildMessage(change.getKey(), change.getValue())).<Consumer<? super TextChannel>> map(message -> c -> Actions.sendMessage(c, message)).forEach(channels::forEach);
		}
		
		@Override
		public String getRunnerName(){
			return "differences";
		}
		
		@Override
		public JDA getJDA(){
			return this.jda;
		}
		
		@Override
		public AniListMediaUserListPagedQuery initQuery(final Map<String, String> userInfo){
			return new AniListMediaUserListPagedQuery(Integer.parseInt(userInfo.get("userId")));
		}
		
		@Override
		public boolean keepOnlyNew(){
			return false;
		}
		
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
	AniListFetchMediaUserListDifferencesCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("filter", "What kind of media to get the differences", false);
		builder.addField("user1", "Mention of the first user to compare", false);
		builder.addField("user2", "Mention of the second user to compare", false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.size() < 3 || event.getMessage().getMentionedUsers().size() < 2){
			final var embed = Utilities.buildEmbed(event.getAuthor(), Color.RED, "Invalid parameters");
			embed.addField("Reason", "Please mention 2 users to compare", false);
			Actions.reply(event, embed.build());
			return CommandResult.SUCCESS;
		}
		final var type = AniListMediaType.valueOf(args.poll());
		final var runner = new AniListMediaUserListDifferencesRunner(event.getJDA(), type);
		runner.runQuery(event.getMessage().getMentionedMembers(), List.of(event.getTextChannel()));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user1> <@user2>";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "AniList fetch media user list differences";
	}
	
	@Override
	public List<String> getCommand(){
		//noinspection SpellCheckingInspection
		return List.of("mediadiff", "d");
	}
	
	@Override
	public String getDescription(){
		return "Fetch media user list differences from AniList";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
