package fr.raksrinana.rsndiscord.modules.event;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
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
		if(noMemberIsMentioned(event)){
			return BAD_ARGUMENTS;
		}
		
		var guild = event.getGuild();
		var members = event.getMessage().getMentionedMembers();
		
		Settings.get(guild).getEventWinnerRole()
				.flatMap(RoleConfiguration::getRole)
				.ifPresent(winnerRole -> {
					guild.findMembers(member -> member.getRoles().contains(winnerRole) && !members.contains(member))
							.onSuccess(previousWinners -> previousWinners.forEach(member -> guild.removeRoleFromMember(member, winnerRole).submit()));
					members.forEach(member -> guild.addRoleToMember(member, winnerRole).submit());
				});
		return SUCCESS;
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
