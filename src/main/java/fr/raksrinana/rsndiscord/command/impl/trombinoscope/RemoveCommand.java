package fr.raksrinana.rsndiscord.command.impl.trombinoscope;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.isModerator;

class RemoveCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	RemoveCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("id", translate(guild, "command.trombinoscope.remove.help.id"), true);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var guild = event.getGuild();
		var id = args.pop();
		
		try{
			var uuid = UUID.fromString(id);
			var trombinoscope = Settings.get(guild).getTrombinoscope();
			trombinoscope.getUserIdOfPicture(uuid).ifPresentOrElse(userId -> {
				if(Objects.equals(userId, event.getAuthor().getIdLong()) || isModerator(event.getMember())){
					trombinoscope.removePicture(userId, uuid);
					JDAWrappers.message(event, translate(guild, "trombinoscope.picture-removed")).submit()
							.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
					
					if(!trombinoscope.isUserPresent(userId)){
						guild.retrieveMemberById(userId).submit().thenAccept(target -> trombinoscope.getPosterRole()
								.flatMap(RoleConfiguration::getRole)
								.ifPresent(role -> JDAWrappers.removeRole(target, role).submit()));
					}
				}
				else{
					JDAWrappers.message(event, translate(guild, "trombinoscope.error.remove-other")).submit()
							.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
				}
			}, () -> JDAWrappers.message(event, translate(guild, "trombinoscope.error.unknown")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		}
		catch(IllegalArgumentException e){
			JDAWrappers.message(event, translate(guild, "trombinoscope.error.invalid-id")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
		}
		return SUCCESS;
	}
	
	@Override
	public @NotNull String getCommandUsage(){
		return super.getCommandUsage() + " <id>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.trombinoscope.remove", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.trombinoscope.remove.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.trombinoscope.remove.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("remove", "r");
	}
}
