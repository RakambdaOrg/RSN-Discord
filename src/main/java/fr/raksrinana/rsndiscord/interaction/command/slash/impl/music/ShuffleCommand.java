package fr.raksrinana.rsndiscord.interaction.command.slash.impl.music;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.permission.IPermission;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.interaction.command.permission.SimplePermission.FALSE_BY_DEFAULT;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class ShuffleCommand extends SubSlashCommand{
	@Override
	@NotNull
	public String getId(){
		return "shuffle";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Shuffle the queue";
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
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		RSNAudioManager.shuffle(guild);
		JDAWrappers.edit(event, translate(guild, "music.queue.shuffled", event.getUser().getAsMention())).submit();
		return CommandResult.HANDLED;
	}
}
