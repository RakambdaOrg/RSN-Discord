package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import java.awt.Color;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import static fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand.ChangeConfigType.*;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class ListConfiguration<T> extends Configuration{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected ListConfiguration(final Guild guild){
		super(guild);
	}
	
	/**
	 * Add a value to the list.
	 *
	 * @param value The value to add.
	 */
	public void addValue(@NotNull final T value){
		addRawValue(getValueParser().apply(value));
	}
	
	/**
	 * Add a value to the list.
	 *
	 * @param value The value to add.
	 */
	private void addRawValue(final String value){
		Settings.addValue(guild, this, value);
	}
	
	/**
	 * Get a parser to convert an object to its config representation.
	 *
	 * @return The parser.
	 */
	protected abstract Function<T, String> getValueParser();
	
	/**
	 * Remove a value from the list.
	 *
	 * @param value The value to remove.
	 */
	public void removeValue(@NotNull final T value){
		removeRawValue(getValueParser().apply(value));
	}
	
	/**
	 * Remove a value from the list.
	 *
	 * @param value The value to remove.
	 */
	private void removeRawValue(final String value){
		Settings.removeValue(guild, this, value);
	}
	
	@Override
	public ConfigurationCommand.ActionResult handleChange(final MessageReceivedEvent event, final ConfigurationCommand.ChangeConfigType action, final LinkedList<String> args){
		if(action == ConfigurationCommand.ChangeConfigType.SHOW){
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Values of " + getName());
			getAsList().stream().filter(Objects::nonNull).map(Object::toString).forEach(o -> builder.addField("", o, false));
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		if(args.size() < 1){
			return ConfigurationCommand.ActionResult.ERROR;
		}
		switch(action){
			case ADD:
				addRawValue(getMessageParser().apply(event, args.poll()));
				return ConfigurationCommand.ActionResult.OK;
			case REMOVE:
				removeRawValue(getMessageParser().apply(event, args.poll()));
				return ConfigurationCommand.ActionResult.OK;
		}
		return ConfigurationCommand.ActionResult.ERROR;
	}
	
	/**
	 * Get a list of the values.
	 *
	 * @return The values list.
	 *
	 * @throws IllegalArgumentException If the configuration isn't a list.
	 */
	public List<T> getAsList() throws IllegalArgumentException{
		final List<T> elements = new LinkedList<>();
		final var array = getObjectList();
		if(array == null){
			Settings.resetList(guild, this);
		}
		else{
			for(var i = 0; i < array.length(); i++){
				elements.add(getConfigParser().apply(array.get(i).toString()));
			}
		}
		return elements;
	}
	
	/**
	 * Get the parser to parse values from a message to T.
	 *
	 * @return The parser.
	 */
	protected abstract BiFunction<MessageReceivedEvent, String, String> getMessageParser();
	
	/**
	 * Get the JSON array.
	 *
	 * @return The JSON array.
	 *
	 * @throws IllegalArgumentException If the configuration isn't a list.
	 */
	private JSONArray getObjectList() throws IllegalArgumentException{
		if(getType() != ConfigType.LIST){
			throw new IllegalArgumentException("Not a list config");
		}
		try{
			return Settings.getArray(guild, getName());
		}
		catch(final NullPointerException e){
			getLogger(guild).error("NullPointer", e);
		}
		return null;
	}
	
	/**
	 * Get the parser to parse back values to T.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, T> getConfigParser();
	
	@Override
	public Collection<ConfigurationCommand.ChangeConfigType> getAllowedActions(){
		return Set.of(ADD, REMOVE, SHOW);
	}
	
	@Override
	public ConfigType getType(){
		return ConfigType.LIST;
	}
	
	/**
	 * Tell if an element is inside the list.
	 *
	 * @param value The value to test.
	 *
	 * @return True if the value is inside, false otherwise.
	 */
	public boolean contains(final T value){
		return getAsList().contains(value);
	}
}
