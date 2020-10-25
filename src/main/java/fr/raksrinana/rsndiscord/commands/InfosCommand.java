package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class InfosCommand extends BasicCommand{
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.infos", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var now = ZonedDateTime.now();
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, translate(event.getGuild(), "infos.title"), null);
		builder.addField(translate(event.getGuild(), "infos.version"), Main.getRSNBotVersion(), false);
		builder.addField(translate(event.getGuild(), "infos.time"), now.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), false);
		builder.addField(translate(event.getGuild(), "infos.last-boot"), Main.bootTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), false);
		builder.addField(translate(event.getGuild(), "infos.elapsed"), Duration.between(Main.bootTime, now).toString(), false);
		Actions.sendEmbed(event.getChannel(), builder.build());
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.infos.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("info");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.infos.description");
	}
}
