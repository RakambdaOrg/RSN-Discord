package fr.raksrinana.rsndiscord.commands.trombinoscope;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
		builder.addField("id", "The id of the picture to remove", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		var id = args.pop();
		try{
			var uuid = UUID.fromString(id);
			var trombinoscope = Settings.get(event.getGuild()).getTrombinoscope();
			trombinoscope.getUserIdOfPicture(uuid).ifPresentOrElse(userId -> {
				if(Objects.equals(userId, event.getAuthor().getIdLong()) || isModerator(event.getMember())){
					trombinoscope.removePicture(userId, uuid);
					Actions.reply(event, "Picture removed", null);
					if(!trombinoscope.isUserPresent(userId)){
						event.getGuild().retrieveMemberById(userId).submit()
								.thenAccept(target -> trombinoscope.getPosterRole()
										.flatMap(RoleConfiguration::getRole)
										.ifPresent(role -> Actions.removeRole(target, role)));
					}
				}
				else{
					Actions.reply(event, "You can't delete picture of others", null);
				}
			}, () -> Actions.reply(event, "Picture not found", null));
		}
		catch(IllegalArgumentException e){
			Actions.reply(event, "Invalid id provided", null);
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <id>";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Remove";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("remove", "r");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Removes a picture";
	}
}
