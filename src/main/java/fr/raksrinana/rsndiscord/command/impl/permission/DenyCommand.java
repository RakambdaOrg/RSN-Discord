package fr.raksrinana.rsndiscord.command.impl.permission;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
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

public class DenyCommand extends BasicCommand{
	public DenyCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("permission", translate(guild, "command.permissions.deny.help.permission"), false)
				.addField("entity", translate(guild, "command.permissions.deny.help.entity"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		var guild = event.getGuild();
		var message = event.getMessage();
		
		if(args.isEmpty() || args.size() == message.getMentions().size()){
			return BAD_ARGUMENTS;
		}
		var permissionsConfiguration = Settings.get(guild).getPermissionsConfiguration();
		String permissionId = args.poll();
		message.getMentionedUsers().forEach(user -> permissionsConfiguration.deny(user, permissionId));
		message.getMentionedRoles().forEach(role -> permissionsConfiguration.deny(role, permissionId));
		event.getChannel().sendMessage(translate(guild, "permissions.denied", permissionId)).submit()
				.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
		return SUCCESS;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <permission> <@entity...>";
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.permissions.deny", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.permissions.deny.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("deny", "d");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.permissions.deny.description");
	}
}
