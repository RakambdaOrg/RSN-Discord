package fr.raksrinana.rsndiscord.commands.anilist;

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
public class AniListGetUserEntryCommand extends BasicCommand{
	static class AniListMediaUserListRunner implements AniListRunner<MediaList, MediaListPagedQuery>{
		private final int ID;
		private final JDA jda;
		private final MediaType type;
		private final TextChannel channel;
		
		AniListMediaUserListRunner(final JDA jda, final MediaType type, final int ID, final TextChannel channel){
			this.jda = jda;
			this.type = type;
			this.ID = ID;
			this.channel = channel;
		}
		
		@Override
		public void sendMessages(@Nonnull final List<TextChannel> channels, @Nonnull final Map<User, List<MediaList>> userMedias){
			userMedias.values().forEach(medias -> medias.removeIf(aniListMediaUserList -> aniListMediaUserList.getMedia().getType() != this.type || !Objects.equals(aniListMediaUserList.getMedia().getId(), this.ID)));
			userMedias.entrySet().stream().flatMap(userMedia -> userMedia.getValue().stream().map(media -> ImmutablePair.of(userMedia.getKey(), media))).sorted(Comparator.comparing(Map.Entry::getValue)).map(userMedia -> this.buildMessage(userMedia.getKey(), userMedia.getValue())).<Consumer<? super TextChannel>> map(message -> channel -> Actions.sendMessage(channel, message)).forEach(channels::forEach);
		}
		
		@Override
		public List<TextChannel> getChannels(){
			return List.of(this.channel);
		}
		
		@Nonnull
		@Override
		public String getRunnerName(){
			return "get media";
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
			//noinspection SpellCheckingInspection
			return "getmedia";
		}
	}
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AniListGetUserEntryCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder embedBuilder){
		super.addHelp(guild, embedBuilder);
		embedBuilder.addField("filter", "What kind of media to get the differences", false);
		embedBuilder.addField("mediaID", "The ID of the media", false);
		embedBuilder.addField("user", "Mention of the users to get", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.size() < 3 || event.getMessage().getMentionedUsers().isEmpty()){
			final var embedBuilder = Utilities.buildEmbed(event.getAuthor(), Color.RED, "Invalid parameters");
			embedBuilder.addField("Reason", "Please mention a user to compare and a kind of media", false);
			Actions.reply(event, embedBuilder.build());
			return CommandResult.SUCCESS;
		}
		final var type = MediaType.valueOf(args.poll());
		final var mediaId = Integer.parseInt(args.pop());
		final var runner = new AniListMediaUserListRunner(event.getJDA(), type, mediaId, event.getChannel());
		runner.runQuery(event.getMessage().getMentionedMembers(), List.of(event.getChannel()));
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <filter> <mediaID> <@user...>";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "AniList fetch media user list";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("get", "g");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Fetch media user list from AniList";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
