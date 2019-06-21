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
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	ConfigurationCommand(@Nullable final Command parent, @Nonnull final ChangeConfigType type, @Nonnull final List<String> commands){
		super(parent);
		this.type = type;
		this.commands = commands;
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder embedBuilder){
		super.addHelp(guild, embedBuilder);
		embedBuilder.addField("Configuration", "Configuration's name", false);
		embedBuilder.addField("Value", "Value of the sub command", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var embedBuilder = new EmbedBuilder();
		embedBuilder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
		if(!args.isEmpty()){
			final var configuration = Settings.getSettings(args.pop());
			if(Objects.nonNull(configuration)){
				final List<String> beforeArgs = new LinkedList<>(args);
				final var result = processWithValue(event, configuration.getClass(), args);
				if(Objects.equals(result, ActionResult.ERROR)){
					embedBuilder.setColor(Color.RED);
					embedBuilder.setTitle("Error performing the operation");
					embedBuilder.setDescription("Command: " + getName());
					embedBuilder.addField("Reason", "It's complicated", false);
					embedBuilder.addField("Configuration", configuration.getName(), false);
					getLogger(event.getGuild()).error("Error handling configuration change");
				}
				else if(!Objects.equals(result, ActionResult.NONE)){
					embedBuilder.setColor(Color.GREEN);
					embedBuilder.setTitle("Value changed");
					embedBuilder.setDescription("Command: " + getName());
					embedBuilder.addField("Configuration:", configuration.getName(), false);
					embedBuilder.addField("Value:", beforeArgs.toString(), false);
					getLogger(event.getGuild()).info("Config value {} changed", configuration.getName());
				}
			}
			else{
				embedBuilder.setColor(Color.ORANGE);
				embedBuilder.setTitle("Configuration not found");
				embedBuilder.addField("Available configurations", Arrays.stream(Settings.SETTINGS).map(Configuration::getName).collect(Collectors.joining(", ")), false);
			}
		}
		else{
			embedBuilder.setColor(Color.ORANGE);
			embedBuilder.setTitle("Please give the name of the configuration to modify");
		}
		Actions.reply(event, embedBuilder.build());
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
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
	@Nonnull
	private ActionResult processWithValue(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final Class<? extends Configuration> configuration, @Nonnull final LinkedList<String> args){
		try{
			final var configInstance = configuration.getConstructor(Guild.class).newInstance(event.getGuild());
			if(configInstance.getAllowedActions().contains(getType())){
				try{
					getLogger(event.getGuild()).info("Handling change {} on config {} with parameters {}", getType().name(), configuration, args);
					return configInstance.handleChange(event, getType(), args);
				}
				catch(final Exception e){
					final var embedBuilder = new EmbedBuilder();
					embedBuilder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					embedBuilder.setColor(Color.RED);
					embedBuilder.setTitle("Error performing the operation");
					embedBuilder.setDescription("Command: " + getName());
					embedBuilder.addField("Reason", (Objects.isNull(e.getMessage()) || e.getMessage().isBlank()) ? e.getClass().getName() : e.getMessage(), false);
					embedBuilder.addField("Configuration", configuration.getName(), false);
					Actions.reply(event, embedBuilder.build());
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
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Operation " + getType().name();
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return this.commands;
	}
	
	@Nonnull
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
	@Nonnull
	private ChangeConfigType getType(){
		return this.type;
	}
}
