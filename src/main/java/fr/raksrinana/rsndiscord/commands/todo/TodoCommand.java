package fr.raksrinana.rsndiscord.commands.todo;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@BotCommand
public class TodoCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Message", "The message of the todo", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		else{
			final var data = new HashMap<String, String>();
			data.put(ReactionUtils.DELETE_KEY, Boolean.toString(true));
			if(Objects.equals(args.peek(), "false")){
				args.pop();
				data.put(ReactionUtils.DELETE_KEY, Boolean.toString(false));
			}
			Actions.reply(event, String.join(" ", args), null).thenAccept(message -> {
				Actions.addReaction(message, BasicEmotes.CHECK_OK.getValue());
				Settings.get(event.getGuild()).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(message, ReactionTag.TODO, data));
				Actions.pin(message);
			});
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <message...>";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "TODO";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("todo");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Put a todo in the chat";
	}
}
