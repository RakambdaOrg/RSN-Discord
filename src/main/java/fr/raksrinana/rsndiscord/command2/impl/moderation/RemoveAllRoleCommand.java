package fr.raksrinana.rsndiscord.command2.impl.moderation;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;

public class RemoveAllRoleCommand extends SubCommand{
	private static final String ROLE_OPTION_ID = "role";
	
	@Override
	@NotNull
	public String getId(){
		return "remove-role";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Remove a role from all members";
	}
	
	@Override
	protected @NotNull Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(ROLE, ROLE_OPTION_ID, "The role to remove from members").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var targetRole = event.getOption(ROLE_OPTION_ID).getAsRole();
		
		JDAWrappers.edit(event, translate(guild, "remove-role.retrieving-with-role")).submit();
		
		guild.findMembers(member -> member.getRoles().contains(targetRole))
				.onSuccess(members -> {
					JDAWrappers.edit(event, translate(guild, "remove-role.removing", members.size())).submit();
					members.forEach(member -> JDAWrappers.removeRole(member, targetRole).submit());
				})
				.onError(e -> {
					Log.getLogger(guild).error("Failed to load members", e);
					JDAWrappers.edit(event, translate(guild, "remove-role.error-members")).submit();
				});
		
		return SUCCESS;
	}
}
