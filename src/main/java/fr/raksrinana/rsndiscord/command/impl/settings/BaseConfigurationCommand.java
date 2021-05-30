package fr.raksrinana.rsndiscord.command.impl.settings;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.command.CommandResult.*;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public abstract class BaseConfigurationCommand extends BasicCommand{
	protected BaseConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Type", getAllowedOperations().stream().map(Enum::name).collect(Collectors.joining(", ")), false);
	}
	
	/**
	 * Get the operations that can be executed with this command.
	 *
	 * @return A set of operations possible.
	 */
	@NotNull
	protected abstract Set<ConfigurationOperation> getAllowedOperations();
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var guild = event.getGuild();
		var operationStr = args.pop();
		
		try{
			var operation = ConfigurationOperation.valueOf(operationStr.toUpperCase());
			if(!getAllowedOperations().contains(operation)){
				JDAWrappers.message(event, translate(guild, "configuration.operation.not-supported")).submit()
						.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
				return SUCCESS;
			}
			Log.getLogger(guild).info("Executing configuration operation {}", operation);
			switch(operation){
				case ADD -> onAdd(event, args);
				case SET -> onSet(event, args);
				case REMOVE -> onRemove(event, args);
				case SHOW -> onShow(event, args);
			}
		}
		catch(IllegalArgumentException e){
			JDAWrappers.message(event, translate(guild, "configuration.operation.unknown")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
		}
		catch(RuntimeException e){
			Log.getLogger(guild).warn("Failed to update configuration", e);
			Utilities.reportException("Error updating configuration", e);
			JDAWrappers.message(event, translate(guild, "configuration.update-failed", e.getMessage())).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
			return FAILED;
		}
		return SUCCESS;
	}
	
	protected void onAdd(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){}
	
	protected void onSet(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){}
	
	protected void onRemove(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){}
	
	protected void onShow(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <type>";
	}
	
	@NotNull
	protected EmbedBuilder getConfigEmbed(@NotNull GuildMessageReceivedEvent event, @NotNull String description, @NotNull Color color){
		return new EmbedBuilder()
				.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
				.setColor(color)
				.setTitle(getName(event.getGuild()))
				.setDescription(description);
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.config.generic-description", getName(guild));
	}
}
