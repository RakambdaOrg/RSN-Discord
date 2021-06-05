package fr.raksrinana.rsndiscord.command2.impl.music;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.command2.permission.IPermission;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.command2.permission.SimplePermission.FALSE_BY_DEFAULT;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class StopCommand extends SubCommand{
	@Override
	@NotNull
	public String getId(){
		return "stop";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Stop the music";
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
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		
		var message = switch(RSNAudioManager.leave(guild)){
			case NO_MUSIC -> "music.nothing-playing";
			case OK -> "music.stopped";
			case IMPOSSIBLE -> "unknown";
		};
		JDAWrappers.edit(event, translate(guild, message, event.getUser().getAsMention())).submitAndDelete(5);
		return HANDLED;
	}
}
