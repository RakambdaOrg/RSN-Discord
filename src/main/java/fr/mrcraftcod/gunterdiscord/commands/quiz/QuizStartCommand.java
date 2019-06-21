package fr.mrcraftcod.gunterdiscord.commands.quiz;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.quiz.QuizListener;
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
 * @since 2018-05-27
 */
public class QuizStartCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	QuizStartCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Quantity", "The maximum number of questions (default: all questions)", false);
		builder.addField("Delay", "The delay before starting the quiz (default: 60 seconds)", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		var amount = Integer.MAX_VALUE;
		var delay = 60;
		if(!args.isEmpty()){
			try{
				amount = Integer.parseInt(args.poll());
			}
			catch(final Exception ignored){
			}
		}
		if(!args.isEmpty()){
			try{
				delay = Integer.parseInt(args.poll());
			}
			catch(final Exception ignored){
			}
		}
		QuizListener.getQuiz(event.getGuild(), amount, delay);
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [quantity] [delay]";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Start quiz";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("start");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Start a new quiz";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
