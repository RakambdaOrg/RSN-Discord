package fr.raksrinana.rsndiscord.interaction.command.slash.impl.music;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

public class VolumeCommand extends SubSlashCommand{
	private static final String VOLUME_OPTION_ID = "volume";
	
	@Override
	@NotNull
	public String getId(){
		return "volume";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Set the volume bot";
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	protected @NotNull Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(INTEGER, VOLUME_OPTION_ID, "The volume to set").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var requestedVolume = getOptionAsInt(event.getOption(VOLUME_OPTION_ID)).orElseThrow();
		
		var volume = Math.min(100, Math.max(0, requestedVolume));
		Settings.get(guild).setMusicVolume(volume);
		RSNAudioManager.getFor(guild).ifPresent(audioManager -> audioManager.setVolume(volume));
		JDAWrappers.edit(event, translate(guild, "music.volume-set", volume)).submit();
		return HANDLED;
	}
}
