package fr.raksrinana.rsndiscord.modules.trombinoscope.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.trombinoscope.config.Picture;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

class StatsCommand extends BasicCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	StatsCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.trombinoscope.stats.help.user"), true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(event.getMessage().getMentionedUsers().isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		var trombinoscope = Settings.get(event.getGuild()).getTrombinoscope();
		var target = event.getMessage().getMentionedUsers().get(0);
		var pictures = trombinoscope.getPictures(target);
		var embed = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, translate(event.getGuild(), "trombinoscope.stats.title"), null);
		embed.addField(translate(event.getGuild(), "trombinoscope.stats.user"), target.getAsMention(), true);
		embed.addField(translate(event.getGuild(), "trombinoscope.stats.count"), Integer.toString(pictures.size()), true);
		embed.addField(translate(event.getGuild(), "trombinoscope.stats.date"), pictures.stream().max(Comparator.comparing(Picture::getDate))
				.map(Picture::getDate)
				.map(DF::format)
				.orElseGet(() -> translate(event.getGuild(), "trombinoscope.stats.date-unknown")), true);
		Actions.sendEmbed(event.getChannel(), embed.build());
		return CommandResult.SUCCESS;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <user>";
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.trombinoscope.stats", true);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.trombinoscope.stats.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("stats", "s");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.trombinoscope.stats.description");
	}
}
