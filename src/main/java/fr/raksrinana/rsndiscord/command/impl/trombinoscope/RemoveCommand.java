package fr.raksrinana.rsndiscord.command.impl.trombinoscope;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
	RemoveCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("id", translate(guild, "command.trombinoscope.remove.help.id"), true);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.trombinoscope.remove", true);
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
		var id = args.pop();
		
		try{
			var uuid = UUID.fromString(id);
			var trombinoscope = Settings.get(guild).getTrombinoscope();
			trombinoscope.getUserIdOfPicture(uuid).ifPresentOrElse(userId -> {
				if(Objects.equals(userId, event.getAuthor().getIdLong()) || isModerator(event.getMember())){
					trombinoscope.removePicture(userId, uuid);
					channel.sendMessage(translate(guild, "trombinoscope.picture-removed")).submit()
							.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
					
					if(!trombinoscope.isUserPresent(userId)){
						guild.retrieveMemberById(userId).submit().thenAccept(target -> trombinoscope.getPosterRole()
								.flatMap(RoleConfiguration::getRole)
								.ifPresent(role -> guild.removeRoleFromMember(target, role).submit()));
					}
				}
				else{
					channel.sendMessage(translate(guild, "trombinoscope.error.remove-other")).submit()
							.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
				}
			}, () -> channel.sendMessage(translate(guild, "trombinoscope.error.unknown")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		}
		catch(IllegalArgumentException e){
			channel.sendMessage(translate(guild, "trombinoscope.error.invalid-id")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
		}
		return SUCCESS;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <id>";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.trombinoscope.remove.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("remove", "r");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.trombinoscope.remove.description");
	}
}
