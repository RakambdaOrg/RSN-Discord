package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.durationToString;
import static java.awt.Color.GREEN;
import static java.time.Duration.between;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

@BotCommand
public class InfosCommand extends BasicCommand{
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		var guild = event.getGuild();
		var author = event.getAuthor();
		var now = ZonedDateTime.now();
		
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(GREEN)
				.addField(translate(guild, "infos.version"), Main.getRSNBotVersion(), false)
				.addField(translate(guild, "infos.time"), now.format(ISO_ZONED_DATE_TIME), false)
				.addField(translate(guild, "infos.last-boot"), Main.bootTime.format(ISO_ZONED_DATE_TIME), false)
				.addField(translate(guild, "infos.elapsed"), durationToString(between(Main.bootTime, now)), false)
				.build();
		
		JDAWrappers.message(event, embed).submit();
		return SUCCESS;
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.infos", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.infos.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.infos.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("info");
	}
}
