package fr.raksrinana.rsndiscord.modules.event;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class WinnerCommand extends BasicCommand{
	public WinnerCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.event.winner.help.user"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(event.getMessage().getMentionedMembers().isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		Settings.get(event.getGuild()).getEventWinnerRole()
				.flatMap(RoleConfiguration::getRole)
				.ifPresent(winnerRole -> {
					event.getGuild()
							.findMembers(member -> member.getRoles().contains(winnerRole) && !event.getMessage().getMentionedMembers().contains(member))
							.onSuccess(members -> members.forEach(member -> Actions.removeRole(member, winnerRole)));
					event.getMessage()
							.getMentionedMembers()
							.forEach(member -> Actions.giveRole(member, winnerRole));
				});
		return CommandResult.SUCCESS;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <@user...>";
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.event.winner", true);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.event.winner.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("winner");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.event.winner.description");
	}
}
