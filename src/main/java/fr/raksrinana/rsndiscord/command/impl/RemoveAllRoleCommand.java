package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class RemoveAllRoleCommand extends BasicCommand{
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.remove-role", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		var channel = event.getChannel();
		var targetRole = getFirstRoleMentioned(event);
		
		if(targetRole.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		channel.sendMessage(translate(guild, "remove-role.retrieving-with-role")).submit()
				.thenAccept(message -> deleteMessage(message, date -> date.plusMinutes(5)));
		guild.findMembers(member -> member.getRoles().contains(targetRole.get()))
				.onSuccess(members -> {
					channel.sendMessage(translate(guild, "remove-role.removing", members.size())).submit()
							.thenAccept(message -> deleteMessage(message, date -> date.plusMinutes(5)));
					members.forEach(member -> guild.removeRoleFromMember(member, targetRole.get()).submit());
				})
				.onError(e -> {
					Log.getLogger(guild).error("Failed to load members", e);
					channel.sendMessage(translate(guild, "remove-role.error-members")).submit()
							.thenAccept(message -> deleteMessage(message, date -> date.plusMinutes(5)));
				});
		
		return SUCCESS;
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
