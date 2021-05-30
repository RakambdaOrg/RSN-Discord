package fr.raksrinana.rsndiscord.command2.impl.bot;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

public class TimeCommand extends SubCommand{
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
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var author = event.getUser();
		
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(GREEN)
				.setTitle(translate(guild, "time.title"))
				.addField(translate(guild, "time.time"), ZonedDateTime.now().format(ISO_ZONED_DATE_TIME), false)
				.addField(translate(guild, "time.milliseconds"), String.valueOf(System.currentTimeMillis()), false)
				.build();
		
		JDAWrappers.replyCommand(event, embed).submit();
		return CommandResult.SUCCESS;
	}
}
