package fr.raksrinana.rsndiscord.interaction.command.slash.impl.bot;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import static java.awt.Color.GREEN;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

public class TimeCommand extends SubSlashCommand{
	@Override
	@NotNull
	public String getId(){
		return "time";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Get current time of the bot";
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		return execute(event);
	}
	
	@Override
	@NotNull
	public CommandResult executeUser(@NotNull SlashCommandInteractionEvent event){
		return execute(event);
	}
	
	@NotNull
	private CommandResult execute(@NotNull SlashCommandInteractionEvent event){
		var embed = new EmbedBuilder()
				.setColor(GREEN)
				.setTitle("Server time")
				.addField("Time", ZonedDateTime.now().format(ISO_ZONED_DATE_TIME), false)
				.addField("Milliseconds", String.valueOf(System.currentTimeMillis()), false)
				.build();
		
		JDAWrappers.edit(event, embed).submit();
		return CommandResult.HANDLED;
	}
}
