package fr.raksrinana.rsndiscord.command.impl.anilist;

import fr.raksrinana.rsndiscord.api.anilist.AniListApi;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.*;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

class RegisterCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	RegisterCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Code", translate(guild, "command.anilist.register.help.code", AniListApi.getCODE_LINK()), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var guild = event.getGuild();
		
		try{
			AniListApi.requestToken(event.getMember(), args.pop());
			JDAWrappers.message(event, translate(guild, "anilist.api-code.saved")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(10)));
		}
		catch(IllegalArgumentException e){
			JDAWrappers.message(event, translate(guild, "anilist.api-code.invalid")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(10)));
			return NOT_HANDLED;
		}
		catch(InvalidResponseException e){
			Log.getLogger(guild).error("Error getting AniList access token", e);
			Utilities.reportException("Error getting AniList Token", e);
			
			JDAWrappers.message(event, translate(guild, "anilist.api-code.save-error")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(10)));
			return FAILED;
		}
		return SUCCESS;
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <code>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.anilist.register", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.anilist.register.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.anilist.register.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("register", "r");
	}
}
