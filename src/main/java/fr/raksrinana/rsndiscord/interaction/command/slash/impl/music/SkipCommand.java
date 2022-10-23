package fr.raksrinana.rsndiscord.interaction.command.slash.impl.music;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@Log4j2
public class SkipCommand extends SubSlashCommand{
	@Override
	@NotNull
	public String getId(){
		return "skip";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Skip the current music";
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	public boolean isSpecificAllowed(@NotNull Member member){
		return RSNAudioManager.isRequester(member.getGuild(), member.getUser());
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var author = event.getUser();
		var track = RSNAudioManager.currentTrack(guild);
		
		if(track.isEmpty()){
			JDAWrappers.edit(event, translate(guild, "music.nothing-playing")).submit();
			return HANDLED;
		}
		
		if(track.get().getDuration() - track.get().getPosition() < 30000){
			JDAWrappers.edit(event, translate(guild, "music.skip.soon-finish")).submit();
			return HANDLED;
		}
		
		var message = skip(guild);
		JDAWrappers.edit(event, translate(guild, message, event.getUser().getAsMention())).submitAndDelete(5);
		return HANDLED;
	}
	
	private String skip(@NotNull Guild guild){
		return switch(RSNAudioManager.skip(guild)){
			case NO_MUSIC -> "music.nothing-playing";
			case OK -> "music.skipped";
			case IMPOSSIBLE -> "unknown";
		};
	}
}
