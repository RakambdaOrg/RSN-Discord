package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.types.TodoConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@BotCommand
public class TodoCommand extends BasicCommand{
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Message", "The message of the todo", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.isEmpty()){
			Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Please give a todo");
		}
		else{
			Actions.reply(event, message -> {
				message.addReaction(BasicEmotes.CHECK_OK.getValue()).queue();
				NewSettings.getConfiguration(event.getGuild()).addTodoMessage(new TodoConfiguration(message));
			}, String.join(" ", args));
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <message...>";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "TODO";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("todo");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Put a todo in the chat";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
