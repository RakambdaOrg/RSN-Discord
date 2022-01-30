package fr.raksrinana.rsndiscord.interaction.command.slash.impl.permission.user;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.CommandService;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Set;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.interaction.command.permission.SimplePermission.FALSE_BY_DEFAULT;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

public class DenyCommand extends SubSlashCommand{
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
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var user = event.getOption(USER_OPTION_ID).getAsUser();
		var name = event.getOption(NAME_OPTION_ID).getAsString();
		
		if(name.startsWith("$")){
			Settings.get(guild).getPermissionsConfiguration()
					.deny(user, name.substring(1));
			JDAWrappers.edit(event, "Custom permission denied").submit();
		}
		else{
			var privilege = CommandPrivilege.disable(user);
			
			CommandService.getRegistrableCommand(name).ifPresentOrElse(
					command -> command.updateCommandPrivileges(guild, privileges -> {
						privileges.remove(privilege);
						privileges.add(privilege);
						return privileges;
					}).thenAccept(empty -> JDAWrappers.edit(event, "Command permission denied").submit()),
					() -> JDAWrappers.edit(event, "Command not found").submit());
		}
		
		return HANDLED;
	}
}
