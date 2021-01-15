package fr.raksrinana.rsndiscord.command.impl.permission;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.permission.EntityPermissions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class ListCommand extends BasicCommand{
	public ListCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("entity", translate(guild, "command.permissions.list.help.entity"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		var message = event.getMessage();
		
		if(message.getMentions().isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		message.getMentionedMembers().forEach(member -> {
			displayUserPermissions(event, member.getUser());
			member.getRoles().forEach(role -> displayRolePermissions(event, role));
		});
		message.getMentionedRoles().forEach(role -> displayRolePermissions(event, role));
		return SUCCESS;
	}
	
	private void displayUserPermissions(GuildMessageReceivedEvent event, User user){
		var permissionsConfiguration = Settings.get(event.getGuild()).getPermissionsConfiguration();
		permissionsConfiguration.getUserPermissions(user)
				.ifPresent(perms -> {
					var permsString = buildPermString(perms);
					event.getChannel().sendMessage("Direct user permissions for " + user.getAsMention() + ":\n" + permsString).submit()
							.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
				});
	}
	
	private void displayRolePermissions(GuildMessageReceivedEvent event, Role role){
		var permissionsConfiguration = Settings.get(event.getGuild()).getPermissionsConfiguration();
		permissionsConfiguration.getRolePermissions(role)
				.ifPresent(perms -> {
					var permsString = buildPermString(perms);
					event.getChannel().sendMessage("Role permissions for " + role.getAsMention() + ":\n" + permsString).submit()
							.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
				});
	}
	
	private String buildPermString(EntityPermissions perms){
		var sb = new StringBuilder();
		sb.append("GRANTED:\n");
		perms.getGranted().stream().sorted().forEach(grant -> sb.append("\t").append(grant).append("\n"));
		sb.append("DENIED:\n");
		perms.getDenied().stream().sorted().forEach(deny -> sb.append("\t").append(deny).append("\n"));
		return sb.toString();
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <@entity...>";
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.permissions.list", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.permissions.list.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("list", "l");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.permissions.list.description");
	}
}
