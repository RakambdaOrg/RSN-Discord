package fr.raksrinana.rsndiscord.command.impl.permission.role;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.SlashCommandService;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.command.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Set;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.command.permission.SimplePermission.FALSE_BY_DEFAULT;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class AllowCommand extends SubCommand{
	public static final String ROLE_OPTION_ID = "role";
	public static final String NAME_OPTION_ID = "name";
	
	@Override
	@NotNull
	public String getId(){
		return "allow";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Give permission to a role";
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
				new OptionData(ROLE, ROLE_OPTION_ID, "Role").setRequired(true),
				new OptionData(STRING, NAME_OPTION_ID, "Permission name").setRequired(true)
		);
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandEvent event, @NotNull Guild guild, @NotNull Member member){
		var role = event.getOption(ROLE_OPTION_ID).getAsRole();
		var name = event.getOption(NAME_OPTION_ID).getAsString();
		
		if(name.startsWith("$")){
			Settings.get(guild).getPermissionsConfiguration()
					.grant(role, name.substring(1));
			JDAWrappers.edit(event, "Custom permission allowed").submit();
		}
		else{
			var privilege = CommandPrivilege.enable(role);
			
			SlashCommandService.getRegistrableCommand(name).ifPresentOrElse(
					command -> command.updateCommandPrivileges(guild, privileges -> {
						privileges.remove(privilege);
						privileges.add(privilege);
						return privileges;
					}).thenAccept(empty -> JDAWrappers.edit(event, "Command permission allowed").submit()),
					() -> JDAWrappers.edit(event, "Command not found").submit());
		}
		
		return HANDLED;
	}
}
