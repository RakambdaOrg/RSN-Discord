package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class RemoveAllRoleCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("role", translate(guild, "command.remove-role.help.role"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		var targetRole = getFirstRoleMentioned(event);
		
		if(targetRole.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		JDAWrappers.message(event, translate(guild, "remove-role.retrieving-with-role")).submit()
				.thenAccept(message -> deleteMessage(message, date -> date.plusMinutes(5)));
		
		guild.findMembers(member -> member.getRoles().contains(targetRole.get()))
				.onSuccess(members -> {
					JDAWrappers.message(event, translate(guild, "remove-role.removing", members.size())).submit()
							.thenAccept(message -> deleteMessage(message, date -> date.plusMinutes(5)));
					members.forEach(member -> JDAWrappers.removeRole(member, targetRole.get()).submit());
				})
				.onError(e -> {
					Log.getLogger(guild).error("Failed to load members", e);
					JDAWrappers.message(event, translate(guild, "remove-role.error-members")).submit()
							.thenAccept(message -> deleteMessage(message, date -> date.plusMinutes(5)));
				});
		
		return SUCCESS;
	}
	
	@Override
	public @NotNull String getCommandUsage(){
		return super.getCommandUsage() + "<@role>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.remove-role", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.remove-role.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.remove-role.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("removerole");
	}
}
