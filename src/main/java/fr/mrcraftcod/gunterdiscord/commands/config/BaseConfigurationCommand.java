package fr.mrcraftcod.gunterdiscord.commands.config;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.ConfigurationOperation;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseConfigurationCommand extends BasicCommand{
	protected BaseConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Type", this.getAllowedOperations().stream().map(Enum::name).collect(Collectors.joining(", ")), false);
	}
	
	@Nonnull
	protected abstract Set<ConfigurationOperation> getAllowedOperations();
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.isEmpty()){
			Actions.reply(event, "Please provide arguments");
		}
		else{
			final var operationStr = args.pop();
			try{
				final var operation = ConfigurationOperation.valueOf(operationStr.toUpperCase());
				Log.getLogger(event.getGuild()).info("Executing operation {}", operation);
				try{
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
				catch(final IllegalOperationException e){
					Log.getLogger(event.getGuild()).warn("Executing bad operation", e);
					Actions.reply(event, "The operation isn't supported");
				}
				catch(final Exception e){
					Log.getLogger(event.getGuild()).error("Error modifying configuration", e);
				}
			}
			catch(final IllegalArgumentException e){
				Log.getLogger(event.getGuild()).warn("Attempting unknown operation", e);
				Actions.reply(event, "This operation doesn't exist");
			}
		}
		return CommandResult.NOT_HANDLED;
	}
	
	protected abstract void onRemove(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args);
	
	protected abstract void onShow(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args);
	
	protected abstract void onSet(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException;
	
	protected abstract void onAdd(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException;
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <type>";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
	
	@Nonnull
	protected EmbedBuilder getConfigEmbed(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final String description, @Nonnull final Color color){
		final var builder = new EmbedBuilder();
		builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
		builder.setColor(color);
		builder.setTitle("Value of " + this.getName());
		builder.setDescription(description);
		return builder;
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Handles the configuration of '" + this.getName() + "'";
	}
}
