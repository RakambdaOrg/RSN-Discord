package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

@BotCommand
public class RemoveAllRoleCommand extends BasicCommand{
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(!event.getMessage().getMentionedRoles().isEmpty()){
			event.getMessage().getMentionedRoles().stream().findFirst().ifPresent(r -> {
				Actions.reply(event, "Getting members with role", null);
				event.getGuild().findMembers(member -> member.getRoles().contains(r)).onSuccess(members -> {
					Actions.reply(event, MessageFormat.format("Will remove the role of {0} people, this may take a while", members.size()), null);
					members.forEach(m -> Actions.removeRole(m, r));
				}).onError(e -> {
					Log.getLogger(event.getGuild()).error("Failed to load members", e);
					Actions.reply(event, "Failed to get members", null);
				});
			});
		}
		else{
			return CommandResult.BAD_ARGUMENTS;
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Remove role from users";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("removerole");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Remove all users from a role";
	}
}
