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
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
	RegisterCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Code", translate(guild, "command.anilist.register.help.code", AniListApi.getCODE_LINK()), false);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.anilist.register", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var guild = event.getGuild();
		var channel = event.getChannel();
		
		try{
			AniListApi.requestToken(event.getMember(), args.pop());
			channel.sendMessage(translate(guild, "anilist.api-code.saved")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(10)));
		}
		catch(final IllegalArgumentException e){
			channel.sendMessage(translate(guild, "anilist.api-code.invalid")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(10)));
			return NOT_HANDLED;
		}
		catch(final InvalidResponseException e){
			Log.getLogger(guild).error("Error getting AniList access token", e);
			Utilities.reportException("Error getting AniList Token", e);
			
			channel.sendMessage(translate(guild, "anilist.api-code.save-error")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(10)));
			return FAILED;
		}
		return SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <code>";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.anilist.register.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("register", "r");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.anilist.register.description");
	}
}
