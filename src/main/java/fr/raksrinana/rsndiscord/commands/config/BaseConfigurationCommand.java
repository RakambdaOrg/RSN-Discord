package fr.raksrinana.rsndiscord.commands.config;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

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
			return CommandResult.BAD_ARGUMENTS;
		}
		final var operationStr = args.pop();
		try{
			final var operation = ConfigurationOperation.valueOf(operationStr.toUpperCase());
			if(!getAllowedOperations().contains(operation)){
				Actions.reply(event, "The operation isn't supported", null);
				return CommandResult.NOT_HANDLED;
			}
			Log.getLogger(event.getGuild()).info("Executing configuration operation {}", operation);
			switch(operation){
				case ADD:
					this.onAdd(event, args);
					break;
				case SET:
					this.onSet(event, args);
					break;
				case REMOVE:
					this.onRemove(event, args);
					break;
				case SHOW:
					this.onShow(event, args);
					break;
			}
			return CommandResult.SUCCESS;
		}
		catch(final RuntimeException e){
			Log.getLogger(event.getGuild()).warn("Failed to update configuration", e);
			Actions.reply(event, "Failed to update configuration: " + e.getMessage(), null);
			Utilities.reportException(e);
			return CommandResult.FAILED;
		}
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
		final var builder = new EmbedBuilder();
		builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
		builder.setColor(color);
		builder.setTitle(this.getName());
		builder.setDescription(description);
		return builder;
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Handles the configuration of " + this.getName();
	}
}
