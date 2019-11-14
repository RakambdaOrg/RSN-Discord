package fr.raksrinana.rsndiscord.commands.quiz;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.listeners.quiz.QuizListener;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

public class QuizStartCommand extends BasicCommand{
	private static final int DEFAULT_START_DELAY = 60;
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	QuizStartCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Quantity", "The maximum number of questions (default: all questions)", false);
		builder.addField("Delay", "The delay before starting the quiz (default: 60 seconds)", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		var amount = Integer.MAX_VALUE;
		var delay = DEFAULT_START_DELAY;
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
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [quantity] [delay]";
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Start quiz";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("start");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Start a new quiz";
	}
}
