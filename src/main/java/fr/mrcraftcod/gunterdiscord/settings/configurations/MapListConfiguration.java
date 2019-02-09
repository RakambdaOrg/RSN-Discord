package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONObject;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import static fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand.ChangeConfigType.*;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class MapListConfiguration<K, V> extends Configuration{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected MapListConfiguration(final Guild guild){
		super(guild);
	}
	
	/**
	 * Get the list of values from the given key.
	 *
	 * @param key The key to get.
	 *
	 * @return The values or null if not found.
	 */
	public List<V> getValue(final K key){
		return getAsMap().get(key);
	}
	
	/**
	 * Get the map of this configuration.
	 *
	 * @return The map.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	public Map<K, ArrayList<V>> getAsMap() throws IllegalArgumentException{
		final Map<K, ArrayList<V>> elements = new HashMap<>();
		final var map = getObjectMap();
		if(map == null){
			Settings.resetMap(guild, this);
		}
		else{
			for(final var key : map.keySet()){
				final var kKey = getKeyParser().apply(key);
				if(!elements.containsKey(kKey)){
					elements.put(kKey, new ArrayList<>());
				}
				final var value = map.optJSONArray(key);
				if(value != null){
					value.toList().stream().map(val -> getValueParser().apply(val.toString())).forEach(o -> elements.get(kKey).add(o));
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
	 * Get the parser to parse back values to V.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, V> getValueParser();
	
	/**
	 * Add a value to the map list.
	 *
	 * @param key   The key to add into.
	 * @param value The value to add at the key.
	 */
	public void addValue(final K key, final V value){
		Settings.mapListValue(guild, this, key, value);
	}
	
	/**
	 * Delete a value inside a key.
	 *
	 * @param key   The key.
	 * @param value The value.
	 */
	public void deleteKeyValue(final K key, final V value){
		if(value == null){
			deleteKey(key);
		}
		else{
			Settings.deleteKey(guild, this, key, value, getMatcher());
		}
	}
	
	/**
	 * Delete the key.
	 *
	 * @param key The key.
	 */
	public void deleteKey(final K key){
		Settings.deleteKey(guild, this, key);
	}
	
	/**
	 * Get the matcher to declare objects equals. This is used when deleting a key value.
	 *
	 * @return The matcher.
	 */
	protected BiFunction<Object, V, Boolean> getMatcher(){
		return Objects::equals;
	}
	
	@Override
	public Collection<ConfigurationCommand.ChangeConfigType> getAllowedActions(){
		return Set.of(ADD, REMOVE, SHOW);
	}
	
	@Override
	public ConfigType getType(){
		return ConfigType.MAP;
	}
}
