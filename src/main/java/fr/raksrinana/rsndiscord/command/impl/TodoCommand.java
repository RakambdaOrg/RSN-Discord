package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.reaction.ReactionUtils.DELETE_KEY;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class TodoCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("message", translate(guild, "command.todo.help.message"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var data = new HashMap<String, String>();
		data.put(DELETE_KEY, Boolean.toString(true));
		if(Objects.equals(args.peek(), "false")){
			args.pop();
			data.put(DELETE_KEY, Boolean.toString(false));
		}
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		JDAWrappers.message(event, String.join(" ", args)).submit()
				.thenAccept(message -> {
					Settings.get(event.getGuild()).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(message, ReactionTag.TODO, data));
					message.addReaction(CHECK_OK.getValue()).submit();
					message.pin().submit();
				});
		return SUCCESS;
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <message...>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.todo", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.todo.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.todo.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("todo");
	}
}
