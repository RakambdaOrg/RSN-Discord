package fr.raksrinana.rsndiscord.command2.impl.permission.role;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.SlashCommandService;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
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
import static fr.raksrinana.rsndiscord.command.CommandResult.NOT_ALLOWED;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class RemoveCommand extends SubCommand{
	public static final String ROLE_OPTION_ID = "role";
	public static final String NAME_OPTION_ID = "name";
	
	@Override
	@NotNull
	public String getId(){
		return "remove";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Reset a permission of role";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return Set.of(
				new OptionData(ROLE, ROLE_OPTION_ID, "Role").setRequired(true),
				new OptionData(STRING, NAME_OPTION_ID, "Permission name").setRequired(true)
		);
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		if(!isAllowed(event.getMember())){
			return NOT_ALLOWED;
		}
		
		var role = event.getOption(ROLE_OPTION_ID).getAsRole();
		var name = event.getOption(NAME_OPTION_ID).getAsString();
		
		if(name.startsWith("$")){
			Settings.get(event.getGuild()).getPermissionsConfiguration()
					.grant(role, name.substring(1));
			JDAWrappers.replyCommand(event, "Custom permission reset").submit();
		}
		else{
			var privilege = CommandPrivilege.disable(role);
			
			SlashCommandService.getRegistrableCommand(name).ifPresentOrElse(
					command -> command.updateCommandPrivileges(event.getGuild(), privileges -> {
						privileges.remove(privilege);
						return privileges;
					}).thenAccept(empty -> JDAWrappers.replyCommand(event, "Command permission reset").submit()),
					() -> JDAWrappers.replyCommand(event, "Command not found").submit());
		}
		
		return SUCCESS;
	}
	
	private boolean isAllowed(Member member){
		return member.isOwner() || Utilities.isCreator(member);
	}
}
