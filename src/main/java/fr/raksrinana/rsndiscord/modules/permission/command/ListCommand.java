package fr.raksrinana.rsndiscord.modules.permission.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.permission.config.EntityPermissions;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
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
		if(event.getMessage().getMentions().isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		event.getMessage().getMentionedMembers().forEach(member -> {
			displayUserPermissions(event, member.getUser());
			member.getRoles().forEach(role -> displayRolePermissions(event, role));
		});
		event.getMessage().getMentionedRoles().forEach(role -> displayRolePermissions(event, role));
		return CommandResult.SUCCESS;
	}
	
	private void displayUserPermissions(GuildMessageReceivedEvent event, User user){
		var permissionsConfiguration = Settings.get(event.getGuild()).getPermissionsConfiguration();
		permissionsConfiguration.getUserPermissions(user)
				.ifPresent(perms -> {
					var permsString = buildPermString(perms);
					Actions.sendMessage(event.getChannel(), "Direct user permissions for " + user.getAsMention() + ":\n" + permsString, null);
				});
	}
	
	private void displayRolePermissions(GuildMessageReceivedEvent event, Role role){
		var permissionsConfiguration = Settings.get(event.getGuild()).getPermissionsConfiguration();
		permissionsConfiguration.getRolePermissions(role)
				.ifPresent(perms -> {
					var permsString = buildPermString(perms);
					Actions.sendMessage(event.getChannel(), "Role permissions for " + role.getAsMention() + ":\n" + permsString, null);
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
