package fr.raksrinana.rsndiscord.command.impl.serverevent;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class WinnerCommand extends BasicCommand{
	public WinnerCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.event.winner.help.user"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
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
							.onSuccess(previousWinners -> previousWinners.forEach(member -> JDAWrappers.removeRole(member, winnerRole).submit()));
					members.forEach(member -> JDAWrappers.addRole(member, winnerRole).submit());
				});
		return SUCCESS;
	}
	
	@Override
	public @NotNull String getCommandUsage(){
		return super.getCommandUsage() + " <@user...>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.event.winner", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.event.winner.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.event.winner.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("winner");
	}
}
