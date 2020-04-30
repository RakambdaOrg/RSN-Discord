package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.music.RSNAudioManager;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

public class VolumeMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	VolumeMusicCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Volume", "The volume to set, between 0 and 100", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		else{
			try{
				final var volume = Math.min(100, Math.max(0, Integer.parseInt(args.pop())));
				Settings.get(event.getGuild()).setMusicVolume(volume);
				RSNAudioManager.getFor(event.getGuild()).ifPresent(g -> g.setVolume(volume));
				Actions.reply(event, MessageFormat.format("Volume set to {0}%", volume), null);
			}
			catch(NumberFormatException e){
				Actions.reply(event, "Please give the volume as an integer", null);
			}
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <volume>";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Volume";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("volume");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Sets the bot's volume";
	}
}
