package fr.raksrinana.rsndiscord.commands.trombinoscope;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.trombinoscope.Picture;
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
		builder.addField("user", "The user to get the stats for", true);
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
		var embed = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Trombinoscope stats", null);
		embed.addField("User", target.getAsMention(), true);
		embed.addField("Picture count", Integer.toString(pictures.size()), true);
		embed.addField("Last added picture", pictures.stream().max(Comparator.comparing(Picture::getDate))
				.map(Picture::getDate)
				.map(DF::format)
				.orElse("N/A"), true);
		Actions.reply(event, "", embed.build());
		return CommandResult.SUCCESS;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <user>";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "User stats";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("stats", "s");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Get statistics about a user";
	}
}
