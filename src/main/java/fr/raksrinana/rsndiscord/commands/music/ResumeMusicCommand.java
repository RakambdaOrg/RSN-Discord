package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.music.RSNAudioManager;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

public class ResumeMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	ResumeMusicCommand(@NonNull final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		switch(RSNAudioManager.resume(event.getGuild())){
			case NO_MUSIC -> Actions.reply(event, MessageFormat.format("{0}, no music is currently playing", event.getAuthor().getAsMention()), null);
			case OK -> Actions.reply(event, MessageFormat.format("{0} resumed the music", event.getAuthor().getAsMention()), null);
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Resume";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("resume", "r");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Resumes la music";
	}
}
