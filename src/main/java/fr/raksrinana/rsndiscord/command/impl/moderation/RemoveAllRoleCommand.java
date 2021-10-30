package fr.raksrinana.rsndiscord.command.impl.moderation;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;

@Log4j2
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
	public CommandResult executeGuild(@NotNull SlashCommandEvent event, @NotNull Guild guild, @NotNull Member member){
		var targetRole = event.getOption(ROLE_OPTION_ID).getAsRole();
		
		JDAWrappers.edit(event, translate(guild, "remove-role.retrieving-with-role")).submit();
		
		guild.findMembers(m -> m.getRoles().contains(targetRole))
				.onSuccess(members -> {
					JDAWrappers.edit(event, translate(guild, "remove-role.removing", members.size())).submit();
					members.forEach(l -> JDAWrappers.removeRole(l, targetRole).submit());
				})
				.onError(e -> {
					log.error("Failed to load members", e);
					JDAWrappers.edit(event, translate(guild, "remove-role.error-members")).submit();
				});
		
		return HANDLED;
	}
}
