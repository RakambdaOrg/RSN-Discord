package fr.raksrinana.rsndiscord.commands.anilist;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

class RegisterCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	RegisterCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Code", "API code obtained from: " + AniListUtils.getCODE_LINK(), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		try{
			AniListUtils.getAndSaveToken(event.getMember(), args.pop());
			Actions.reply(event, "API code saved", null);
		}
		catch(final IllegalArgumentException e){
			Actions.reply(event, "Provided API code isn't valid", null);
			return CommandResult.NOT_HANDLED;
		}
		catch(final URISyntaxException | MalformedURLException | InvalidResponseException e){
			Log.getLogger(event.getGuild()).error("Error getting AniList access token", e);
			Actions.reply(event, "Error while saving API code", null);
			Utilities.reportException(e);
			return CommandResult.FAILED;
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <code>";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "AniList registering";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("register", "r");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Register your AniList account";
	}
}
