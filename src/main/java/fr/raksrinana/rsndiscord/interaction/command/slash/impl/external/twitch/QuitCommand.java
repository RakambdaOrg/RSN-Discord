package fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.twitch;

import fr.raksrinana.rsndiscord.api.twitch.TwitchUtils;
import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED_NO_MESSAGE;
import static fr.raksrinana.rsndiscord.interaction.command.permission.CreatorPermission.CREATOR_PERMISSION;

public class QuitCommand extends SubSlashCommand{
	@Override
	@NotNull
	public String getId(){
		return "quit";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Disconnects from all twitch channels";
	}
	
	@Override
	@NotNull
	public IPermission getPermission(){
		return CREATOR_PERMISSION;
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		TwitchUtils.disconnectAll();
		return HANDLED_NO_MESSAGE;
	}
}
