package fr.raksrinana.rsndiscord.commands.anilist;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.anilist.queries.MediaPagedQuery;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

@Slf4j
class InfoCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	InfoCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder embedBuilder){
		super.addHelp(guild, embedBuilder);
		embedBuilder.addField("id", "The id of the media on AniList", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		final int mediaId;
		try{
			mediaId = Integer.parseInt(args.pop());
		}
		catch(NumberFormatException e){
			return CommandResult.BAD_ARGUMENTS;
		}
		try{
			final var medias = new MediaPagedQuery(mediaId).getResult(event.getMember());
			if(medias.isEmpty()){
				Actions.reply(event, "No information on the next airing for media", null);
			}
			else{
				medias.forEach(media -> {
					final var builder = new EmbedBuilder();
					media.fillEmbed(builder);
					Actions.sendMessage(event.getChannel(), "", builder.build());
				});
			}
		}
		catch(Exception e){
			log.error("Failed to get media with id {}", mediaId, e);
			return CommandResult.FAILED;
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + "<id>";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Info";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("info");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Gets info about a media";
	}
}
