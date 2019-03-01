package fr.mrcraftcod.gunterdiscord.commands.config;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-27
 */
public class ConfigurationCommand extends BasicCommand{
	private final ChangeConfigType type;
	private final List<String> commands;
	
	/**
	 * Actions available.
	 */
	public enum ChangeConfigType{ADD, REMOVE, SET, SHOW, ERROR}
	
	/**
	 * The result of a setting action.
	 */
	public enum ActionResult{OK, NONE, ERROR}
	
	/**
	 * Constructor.
	 *
	 * @param parent   The parent command.
	 * @param type     The operation that will be done on the configuration.
	 * @param commands The commands to call this command.
	 */
	ConfigurationCommand(final Command parent, final ChangeConfigType type, final List<String> commands){
		super(parent);
		this.type = type;
		this.commands = commands;
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Configuration", "Configuration's name", false);
		builder.addField("Value", "Value of the sub command", false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.size() > 0){
			final var configuration = Settings.getSettings(args.pop());
			if(configuration != null){
				final List<String> beforeArgs = new LinkedList<>(args);
				final var result = processWithValue(event, configuration.getClass(), args);
				if(result == ActionResult.ERROR){
					final var builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.RED);
					builder.setTitle("Error performing the operation");
					builder.setDescription("Command: " + getName());
					builder.addField("Reason", "It's complicated", false);
					builder.addField("Configuration", configuration.getName(), false);
					Actions.reply(event, builder.build());
					getLogger(event.getGuild()).error("Error handling configuration change");
				}
				else if(result != ActionResult.NONE){
					final var builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.GREEN);
					builder.setTitle("Value changed");
					builder.setDescription("Command: " + getName());
					builder.addField("Configuration:", configuration.getName(), false);
					builder.addField("Value:", beforeArgs.toString(), false);
					Actions.reply(event, builder.build());
					getLogger(event.getGuild()).info("Config value {} changed", configuration.getName());
				}
			}
			else{
				final var builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.setColor(Color.ORANGE);
				builder.setTitle("Configuration not found");
				builder.addField("Available configurations", Arrays.stream(Settings.SETTINGS).map(Configuration::getName).collect(Collectors.joining(", ")), false);
				Actions.reply(event, builder.build());
			}
		}
		else{
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.ORANGE);
			builder.setTitle("Please give the name of the configuration to modify");
			Actions.reply(event, builder.build());
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <configuration> [value]";
	}
	
	/**
	 * Process the configuration.
	 *
	 * @param event         The event that triggered this change.
	 * @param configuration The configuration to change.
	 * @param args          The args to pass.
	 *
	 * @return The result that happened.
	 */
	private ActionResult processWithValue(final MessageReceivedEvent event, final Class<? extends Configuration> configuration, final LinkedList<String> args){
		try{
			final var configInstance = configuration.getConstructor(Guild.class).newInstance(event.getGuild());
			if(configInstance.getAllowedActions().contains(getType())){
				try{
					getLogger(event.getGuild()).info("Handling change {} on config {} with parameters {}", getType().name(), configuration, args);
					return configInstance.handleChange(event, getType(), args);
				}
				catch(final Exception e){
					final var builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.RED);
					builder.setTitle("Error performing the operation");
					builder.setDescription("Command: " + getName());
					builder.addField("Reason", (Objects.isNull(e.getMessage()) || e.getMessage().isBlank()) ? e.getClass().getName() : e.getMessage(), false);
					builder.addField("Configuration", configuration.getName(), false);
					Actions.reply(event, builder.build());
					getLogger(event.getGuild()).error("Error handling configuration change", e);
				}
			}
			else{
				Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, "Invalid operation").addField("Reason", "Operation " + getType().name() + " invalid for this configuration", false).addField("Configuration", configuration.getName(), false).build());
			}
		}
		catch(final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
			getLogger(event.getGuild()).error("Error instantiating config", e);
		}
		return ActionResult.ERROR;
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Override
	public String getName(){
		return "Operation " + getType().name();
	}
	
	@Override
	public List<String> getCommand(){
		return commands;
	}
	
	@Override
	public String getDescription(){
		return "Applies an operation " + getType().name() + " on this configuration";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
	
	/**
	 * Get the type of operation done.
	 *
	 * @return The type.
	 */
	private ChangeConfigType getType(){
		return type;
	}
}
