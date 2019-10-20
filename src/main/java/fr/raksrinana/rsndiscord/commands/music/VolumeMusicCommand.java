package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.player.RSNAudioManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class VolumeMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	VolumeMusicCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Volume", "The volume to set, between 0 and 100", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.isEmpty()){
			Actions.reply(event, "Please give the volume to set");
		}
		else{
			try{
				final var volume = Math.min(100, Math.max(0, Integer.parseInt(args.pop())));
				NewSettings.getConfiguration(event.getGuild()).setMusicVolume(volume);
				RSNAudioManager.getFor(event.getGuild()).ifPresent(g -> g.setVolume(volume));
				Actions.replyFormatted(event, "Volume set to %d%%", volume);
			}
			catch(NumberFormatException e){
				Actions.reply(event, "Please give the volume as an integer");
			}
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <volume>";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Volume";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("volume");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Sets the bot's volume";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
