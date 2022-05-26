package fr.raksrinana.rsndiscord.interaction.command.slash.impl;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.SimpleSlashCommand;
import fr.raksrinana.rsndiscord.interaction.modal.impl.TimeReactionCreateModal;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;

@BotSlashCommand
@Log4j2
public class TimeReactionCommand extends SimpleSlashCommand{
	private static final String COMMENT_STR = "--";
	private static final String CONTENT_OPTION_ID = "content";
	private static final String EPISODE_OPTION_ID = "episode";
	private static final String LINK_OPTION_ID = "link";
	private static final String ARCHIVE_OPTION_ID = "archive";
	
	@Override
	@NotNull
	public String getId(){
		return "time-reaction";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Time reaction";
	}
	
	@Override
	public boolean deferReply(){
		return false;
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		JDAWrappers.reply(event, new TimeReactionCreateModal().asComponent()).submit();
		return HANDLED;
	}
}
