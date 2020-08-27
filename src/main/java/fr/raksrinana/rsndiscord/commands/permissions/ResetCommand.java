package fr.raksrinana.rsndiscord.commands.permissions;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class ResetCommand extends BasicCommand{
	public ResetCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("permission", translate(guild, "command.permissions.reset.help.permission"), false);
		builder.addField("entity", translate(guild, "command.permissions.reset.help.entity"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty() || args.size() == event.getMessage().getMentions().size()){
			return CommandResult.BAD_ARGUMENTS;
		}
		var permissionsConfiguration = Settings.get(event.getGuild()).getPermissionsConfiguration();
		String permissionId = args.poll();
		event.getMessage().getMentionedUsers().forEach(user -> permissionsConfiguration.reset(user, permissionId));
		event.getMessage().getMentionedRoles().forEach(role -> permissionsConfiguration.reset(role, permissionId));
		Actions.reply(event, translate(event.getGuild(), "permissions.reset", permissionId), null);
		return CommandResult.SUCCESS;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <permission> <@entity...>";
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.permissions.reset", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.permissions.reset.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("reset", "r");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.permissions.reset.description");
	}
}
