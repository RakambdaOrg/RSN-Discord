package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;
import java.awt.Color;
import java.util.*;
import java.util.function.Function;
import static fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand.ChangeConfigType.*;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class MapConfiguration<K, V> extends Configuration{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected MapConfiguration(final Guild guild){
		super(guild);
	}
	
	/**
	 * Get the value from the given key.
	 *
	 * @param key The key to get.
	 *
	 * @return The value or null if not found.
	 */
	public V getValue(final K key){
		try{
			return getAsMap().get(key);
		}
		catch(final Exception e){
			getLogger(guild).error("Can't get value {} with key {}", getName(), key, e);
		}
		return null;
	}
	
	/**
	 * Get the map of this configuration.
	 *
	 * @return The map.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	public Map<K, V> getAsMap() throws IllegalArgumentException{
		final Map<K, V> elements = new HashMap<>();
		final var map = getObjectMap();
		if(map == null){
			Settings.resetMap(guild, this);
		}
		else{
			for(final var key : map.keySet()){
				final var value = map.get(key);
				elements.put(getKeyParser().apply(key), getValueParser().apply(value.toString()));
			}
		}
		return elements;
	}
	
	/**
	 * Get the JSON Object.
	 *
	 * @return The JSON object.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	private JSONObject getObjectMap() throws IllegalArgumentException{
		if(getType() != ConfigType.MAP){
			throw new IllegalArgumentException("Not a map config");
		}
		return Settings.getJSONObject(guild, getName());
	}
	
	/**
	 * Get the parser to parse back string keys to K.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, K> getKeyParser();
	
	/**
	 * Get the parser to parse back string values to V.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, V> getValueParser();
	
	@Override
	public ConfigurationCommand.ActionResult handleChange(final MessageReceivedEvent event, final ConfigurationCommand.ChangeConfigType action, final LinkedList<String> args){
		if(action == SHOW){
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Values of " + getName());
			final var map = getAsMap();
			map.keySet().stream().map(k -> k + " -> " + map.get(k)).forEach(o -> builder.addField("", o, false));
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		
		switch(action){
			case ADD:
				if(args.size() < 2){
					return ConfigurationCommand.ActionResult.ERROR;
				}
				addValue(getKeyParser().apply(args.poll()), getValueParser().apply(args.poll()));
				return ConfigurationCommand.ActionResult.OK;
			case REMOVE:
				if(args.size() < 1){
					return ConfigurationCommand.ActionResult.ERROR;
				}
				deleteKey(getKeyParser().apply(args.poll()));
				return ConfigurationCommand.ActionResult.OK;
		}
		return ConfigurationCommand.ActionResult.ERROR;
	}
	
	@Override
	public Collection<ConfigurationCommand.ChangeConfigType> getAllowedActions(){
		return Set.of(ADD, SHOW, REMOVE);
	}
	
	@Override
	public ConfigType getType(){
		return ConfigType.MAP;
	}
	
	/**
	 * Add a value to the map.
	 *
	 * @param key   The key to add into.
	 * @param value The value to set at the key.
	 */
	public void addValue(final K key, final V value){
		Settings.mapValue(guild, this, key, value);
	}
	
	/**
	 * Delete the key.
	 *
	 * @param key The key.
	 */
	public void deleteKey(final K key){
		Settings.deleteKey(guild, this, key);
	}
}
