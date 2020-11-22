package fr.raksrinana.rsndiscord.modules.trombinoscope.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.trombinoscope.config.Picture;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

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
		event.getChannel().sendMessage(embed).submit();
		return SUCCESS;
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
