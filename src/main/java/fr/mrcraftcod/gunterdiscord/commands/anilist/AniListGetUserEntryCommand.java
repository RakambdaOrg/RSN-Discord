package fr.mrcraftcod.gunterdiscord.commands.anilist;

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
public class AniListGetUserEntryCommand extends BasicCommand{
	class AniListMediaUserListRunner implements AniListRunner<AniListMediaUserList, AniListMediaUserListPagedQuery>{
		private final int ID;
		private final JDA jda;
		private final AniListMediaType type;
		
		AniListMediaUserListRunner(final JDA jda, final AniListMediaType type, final int ID){
			this.jda = jda;
			this.type = type;
			this.ID = ID;
		}
		
		public void sendMessages(final List<TextChannel> channels, final Map<User, List<AniListMediaUserList>> userElements){
			userElements.values().forEach(l -> l.removeIf(aniListMediaUserList -> !aniListMediaUserList.getMedia().getType().equals(this.type) || !Objects.equals(aniListMediaUserList.getMedia().getId(), this.ID)));
			userElements.entrySet().stream().flatMap(es -> es.getValue().stream().map(val -> Map.entry(es.getKey(), val))).sorted(Comparator.comparing(Map.Entry::getValue)).map(change -> buildMessage(change.getKey(), change.getValue())).<Consumer<? super TextChannel>> map(message -> c -> Actions.sendMessage(c, message)).forEach(channels::forEach);
		}
		
		@Override
		public String getRunnerName(){
			return "get media";
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
			//noinspection SpellCheckingInspection
			return "getmedia";
		}
	}
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AniListGetUserEntryCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("filter", "What kind of media to get the differences", false);
		builder.addField("mediaID", "The ID of the media", false);
		builder.addField("user", "Mention of the users to get", false);
	}
	
	@Override
	public CommandResult execute(final GuildMessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.size() < 3 || event.getMessage().getMentionedUsers().isEmpty()){
			final var embed = Utilities.buildEmbed(event.getAuthor(), Color.RED, "Invalid parameters");
			embed.addField("Reason", "Please mention a user to compare and a kind of media", false);
			Actions.reply(event, embed.build());
			return CommandResult.SUCCESS;
		}
		final var type = AniListMediaType.valueOf(args.poll());
		final var ID = Integer.parseInt(args.pop());
		final var runner = new AniListMediaUserListRunner(event.getJDA(), type, ID);
		runner.runQuery(event.getMessage().getMentionedMembers(), List.of(event.getChannel()));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <filter> <mediaID> <@user...>";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "AniList fetch media user list";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("get", "g");
	}
	
	@Override
	public String getDescription(){
		return "Fetch media user list from AniList";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
