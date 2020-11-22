package fr.raksrinana.rsndiscord.modules.settings.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.*;
import static fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public abstract class BaseConfigurationCommand extends BasicCommand{
	protected BaseConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Type", this.getAllowedOperations().stream().map(Enum::name).collect(Collectors.joining(", ")), false);
	}
	
	/**
	 * Get the operations that can be executed with this command.
	 *
	 * @return A set of operations possible.
	 */
	@NonNull
	protected abstract Set<ConfigurationOperation> getAllowedOperations();
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var guild = event.getGuild();
		var channel = event.getChannel();
		var operationStr = args.pop();
		
		try{
			var operation = ConfigurationOperation.valueOf(operationStr.toUpperCase());
			if(!getAllowedOperations().contains(operation)){
				channel.sendMessage(translate(guild, "configuration.operation.not-supported")).submit()
						.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
				return NOT_HANDLED;
			}
			Log.getLogger(guild).info("Executing configuration operation {}", operation);
			switch(operation){
				case ADD -> this.onAdd(event, args);
				case SET -> this.onSet(event, args);
				case REMOVE -> this.onRemove(event, args);
				case SHOW -> this.onShow(event, args);
			}
		}
		catch(final IllegalArgumentException e){
			channel.sendMessage(translate(guild, "configuration.operation.unknown")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
		}
		catch(final RuntimeException e){
			Log.getLogger(guild).warn("Failed to update configuration", e);
			Utilities.reportException("Error updating configuration", e);
			channel.sendMessage(translate(guild, "configuration.update-failed", e.getMessage())).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
			return FAILED;
		}
		return SUCCESS;
	}
	
	protected void onAdd(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args){}
	
	protected void onSet(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args){}
	
	protected void onRemove(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args){}
	
	protected void onShow(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args){}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <type>";
	}
	
	@NonNull
	protected EmbedBuilder getConfigEmbed(@NonNull final GuildMessageReceivedEvent event, @NonNull final String description, @NonNull final Color color){
		return new EmbedBuilder()
				.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
				.setColor(color)
				.setTitle(this.getName(event.getGuild()))
				.setDescription(description);
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.config.generic-description", this.getName(guild));
	}
}
