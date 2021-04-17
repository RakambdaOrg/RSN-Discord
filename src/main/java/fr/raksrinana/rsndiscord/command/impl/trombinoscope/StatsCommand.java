package fr.raksrinana.rsndiscord.command.impl.trombinoscope;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.trombinoscope.Picture;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

class StatsCommand extends BasicCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	StatsCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.trombinoscope.stats.help.user"), true);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(noUserIsMentioned(event)){
			return BAD_ARGUMENTS;
		}
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		var trombinoscope = Settings.get(guild).getTrombinoscope();
		var target = getFirstUserMentioned(event).orElseThrow();
		var pictures = trombinoscope.getPictures(target);
		
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(GREEN)
				.setTitle(translate(guild, "trombinoscope.stats.title"))
				.addField(translate(guild, "trombinoscope.stats.user"), target.getAsMention(), true)
				.addField(translate(guild, "trombinoscope.stats.count"), Integer.toString(pictures.size()), true)
				.addField(translate(guild, "trombinoscope.stats.date"), pictures.stream().max(Comparator.comparing(Picture::getDate))
						.map(Picture::getDate)
						.map(DF::format)
						.orElseGet(() -> translate(guild, "trombinoscope.stats.date-unknown")), true)
				.build();
		JDAWrappers.message(event, embed).submit();
		return SUCCESS;
	}
	
	@Override
	public @NotNull String getCommandUsage(){
		return super.getCommandUsage() + " <user>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.trombinoscope.stats", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.trombinoscope.stats.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.trombinoscope.stats.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("stats", "s");
	}
}
