package fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.anilist;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.modal.impl.AnilistTokenModal;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;

@Log4j2
public class RegisterCommand extends SubSlashCommand{
	@Override
	@NotNull
	public String getId(){
		return "register";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Register your Anilist account";
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		JDAWrappers.reply(event, new AnilistTokenModal().asComponent()).submit();
		return HANDLED;
	}
	
	@Override
	public boolean deferReply(){
		return false;
	}
}
