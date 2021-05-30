package fr.raksrinana.rsndiscord.command2.impl.permission.user;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.SlashCommandService;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.command2.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Set;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.command2.permission.SimplePermission.FALSE_BY_DEFAULT;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class DenyCommand extends SubCommand{
	public static final String USER_OPTION_ID = "user";
	public static final String NAME_OPTION_ID = "name";
	
	@Override
	@NotNull
	public String getId(){
		return "deny";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Deny permission to a user";
	}
	
	@Override
	@NotNull
	public IPermission getPermission(){
		return FALSE_BY_DEFAULT;
	}
	
	@Override
	public boolean isSpecificAllowed(@NotNull Member member){
		return member.isOwner() || Utilities.isCreator(member);
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return Set.of(
				new OptionData(USER, USER_OPTION_ID, "User").setRequired(true),
				new OptionData(STRING, NAME_OPTION_ID, "Permission name").setRequired(true)
		);
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var user = event.getOption(USER_OPTION_ID).getAsUser();
		var name = event.getOption(NAME_OPTION_ID).getAsString();
		
		if(name.startsWith("$")){
			Settings.get(event.getGuild()).getPermissionsConfiguration()
					.deny(user, name.substring(1));
			JDAWrappers.replyCommand(event, "Custom permission denied").submit();
		}
		else{
			var privilege = CommandPrivilege.disable(user);
			
			SlashCommandService.getRegistrableCommand(name).ifPresentOrElse(
					command -> command.updateCommandPrivileges(event.getGuild(), privileges -> {
						privileges.remove(privilege);
						privileges.add(privilege);
						return privileges;
					}).thenAccept(empty -> JDAWrappers.replyCommand(event, "Command permission denied").submit()),
					() -> JDAWrappers.replyCommand(event, "Command not found").submit());
		}
		
		return SUCCESS;
	}
}
