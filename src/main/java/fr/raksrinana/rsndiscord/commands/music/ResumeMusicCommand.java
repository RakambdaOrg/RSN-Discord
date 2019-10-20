package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.player.RSNAudioManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class ResumeMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	ResumeMusicCommand(@Nonnull final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		switch(RSNAudioManager.resume(event.getGuild())){
			case NO_MUSIC:
				Actions.replyFormatted(event, "%s, no music are currently playing", event.getAuthor().getAsMention());
				break;
			case OK:
				Actions.replyFormatted(event, "%s resumed the music", event.getAuthor().getAsMention());
				break;
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Resume";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("resume", "r");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Resumes la music";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
