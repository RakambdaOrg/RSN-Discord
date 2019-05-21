package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONObject;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand.ChangeConfigType.*;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class MapMapConfiguration<K, V, W> extends Configuration{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected MapMapConfiguration(final Guild guild){
		super(guild);
	}
	
	/**
	 * Get the list of values from the given key.
	 *
	 * @param key The key to get.
	 *
	 * @return The values or null if not found.
	 */
	public Map<V, W> getValue(final K key){
		return getAsMap().get(key);
	}
	
	/**
	 * Add a value to the map list.
	 *
	 * @param key         The key to add into.
	 * @param value       The second key key.
	 * @param insideValue The value inside the second map.
	 */
	public void addValue(final K key, final V value, final W insideValue){
		if(Objects.isNull(value) || Objects.isNull(insideValue)){
			addValue(key);
		}
		else{
			Settings.mapMapValue(this.guild, this, key, value, insideValue);
		}
	}
	
	/**
	 * Add an empty value to the map list.
	 *
	 * @param key The key to add into.
	 */
	public void addValue(final K key){
		Settings.mapMapValue(this.guild, this, key);
	}
	
	/**
	 * Get the parser to parse back string keys to K.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, K> getFirstKeyParser();
	
	/**
	 * Get the parser to parse back key string values to V.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, V> getSecondKeyParser();
	
	/**
	 * Get the parser to parse back string values to W.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, W> getValueParser();
	
	/**
	 * Delete a value inside a key.
	 *
	 * @param key   The key.
	 * @param value The value.
	 */
	public void deleteKeyValue(final K key, final V value){
		if(Objects.isNull(value)){
			deleteKey(key);
		}
		else{
			Settings.deleteKey(this.guild, this, key, value);
		}
	}
	
	/**
	 * Delete the key.
	 *
	 * @param key The key.
	 */
	public void deleteKey(final K key){
		Settings.deleteKey(this.guild, this, key);
	}
	
	/**
	 * Get the map of this configuration.
	 *
	 * @return The map.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	public Map<K, Map<V, W>> getAsMap() throws IllegalArgumentException{
		final Map<K, Map<V, W>> elements = new HashMap<>();
		final var map = getObjectMap();
		if(Objects.isNull(map)){
			Settings.resetMap(this.guild, this);
		}
		else{
			for(final var key : map.keySet()){
				final var kKey = getFirstKeyParser().apply(key);
				final var value = map.optJSONObject(key);
				if(Objects.nonNull(value)){
					elements.put(kKey, value.keySet().stream().collect(Collectors.toMap(key2 -> getSecondKeyParser().apply(key2), key2 -> getValueParser().apply(value.get(key2).toString()))));
				}
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
		if(!Objects.equals(getType(), ConfigType.MAP)){
			throw new IllegalArgumentException("Not a map config");
		}
		return Settings.getJSONObject(this.guild, getName());
	}
	
	@Override
	public Collection<ConfigurationCommand.ChangeConfigType> getAllowedActions(){
		return Set.of(ADD, REMOVE, SHOW);
	}
	
	@Override
	public ConfigType getType(){
		return ConfigType.MAP;
	}
	
	/**
	 * Get the matcher to declare objects equals. This is used when deleting a key value.
	 *
	 * @return The matcher.
	 */
	protected BiFunction<Object, V, Boolean> getMatcher(){
		return Objects::equals;
	}
}
