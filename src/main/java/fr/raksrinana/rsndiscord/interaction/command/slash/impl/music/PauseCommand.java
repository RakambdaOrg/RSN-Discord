package fr.raksrinana.rsndiscord.interaction.command.slash.impl.music;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.permission.IPermission;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.interaction.command.permission.SimplePermission.FALSE_BY_DEFAULT;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class PauseCommand extends SubSlashCommand{
	@Override
	@NotNull
	public String getId(){
		return "pause";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Pause the music";
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	@NotNull
	public IPermission getPermission(){
		return FALSE_BY_DEFAULT;
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var message = switch(RSNAudioManager.pause(guild)){
			case NO_MUSIC -> "music.nothing-playing";
			case OK -> "music.paused";
			case IMPOSSIBLE -> "unknown";
		};
		JDAWrappers.edit(event, translate(guild, message, event.getUser().getAsMention())).submitAndDelete(5);
		return HANDLED;
	}
}
