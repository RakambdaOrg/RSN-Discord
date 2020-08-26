package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class RemoveAllRoleCommand extends BasicCommand{
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.remove-role", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(!event.getMessage().getMentionedRoles().isEmpty()){
			event.getMessage().getMentionedRoles().stream().findFirst().ifPresent(r -> {
				Actions.reply(event, translate(event.getGuild(), "remove-role.retrieving-with-role"), null);
				event.getGuild().findMembers(member -> member.getRoles().contains(r)).onSuccess(members -> {
					Actions.reply(event, translate(event.getGuild(), "remove-role.removing", members.size()), null);
					members.forEach(m -> Actions.removeRole(m, r));
				}).onError(e -> {
					Log.getLogger(event.getGuild()).error("Failed to load members", e);
					Actions.reply(event, translate(event.getGuild(), "remove-role.error-members"), null);
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
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.remove-role.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("removerole");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.remove-role.description");
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("role", translate(guild, "command.remove-role.help.role"), false);
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + "<@role>";
	}
}
