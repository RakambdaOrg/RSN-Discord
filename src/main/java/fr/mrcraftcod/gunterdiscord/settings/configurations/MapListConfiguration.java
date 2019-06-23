package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	protected MapListConfiguration(@Nullable final Guild guild){
		super(guild);
	}
	
	/**
	 * Get the list of values from the given key.
	 *
	 * @param key The key to get.
	 *
	 * @return The values or null if not found.
	 */
	@Nonnull
	public Optional<List<V>> getValue(@Nonnull final K key){
		return getAsMap().map(map -> map.get(key));
	}
	
	/**
	 * Get the map of this configuration.
	 *
	 * @return The map.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	@Nonnull
	public Optional<Map<K, ArrayList<V>>> getAsMap() throws IllegalArgumentException{
		return getObjectMap().map(map -> {
			final Map<K, ArrayList<V>> elements = new HashMap<>();
			for(final var key : map.keySet()){
				final var kKey = getKeyParser().apply(key);
				if(Objects.nonNull(kKey)){
					if(!elements.containsKey(kKey)){
						elements.put(kKey, new ArrayList<>());
					}
					final var value = map.optJSONArray(key);
					if(Objects.nonNull(value)){
						value.toList().stream().map(val -> getValueParser().apply(val.toString())).forEach(o -> elements.get(kKey).add(o));
					}
				}
			}
			return elements;
		});
	}
	
	/**
	 * Get the JSON Object.
	 *
	 * @return The JSON object.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	@Nonnull
	private Optional<JSONObject> getObjectMap() throws IllegalArgumentException{
		if(!Objects.equals(getType(), ConfigType.MAP)){
			throw new IllegalArgumentException("Not a map config");
		}
		return Settings.getJSONObject(this.guild, getName());
	}
	
	/**
	 * Get the parser to parse back string keys to K.
	 *
	 * @return The parser.
	 */
	@Nonnull
	protected abstract Function<String, K> getKeyParser();
	
	/**
	 * Get the parser to parse back values to V.
	 *
	 * @return The parser.
	 */
	@Nonnull
	protected abstract Function<String, V> getValueParser();
	
	/**
	 * Delete a value inside a key.
	 *
	 * @param key   The key.
	 * @param value The value.
	 */
	public void deleteKeyValue(@Nonnull final K key, @Nullable final V value){
		if(Objects.isNull(value)){
			deleteKey(key);
		}
		else{
			Settings.deleteKey(this.guild, this, key, value, getMatcher());
		}
	}
	
	/**
	 * Delete the key.
	 *
	 * @param key The key.
	 */
	public void deleteKey(@Nonnull final K key){
		Settings.deleteKey(this.guild, this, key);
	}
	
	/**
	 * Get the matcher to declare objects equals. This is used when deleting a key value.
	 *
	 * @return The matcher.
	 */
	@Nonnull
	protected BiFunction<Object, V, Boolean> getMatcher(){
		return Objects::equals;
	}
	
	/**
	 * Add a value to the map list.
	 *
	 * @param key   The key to add into.
	 * @param value The value to add at the key.
	 */
	public void addValue(@Nonnull final K key, @Nullable final V value){
		Settings.mapListValue(this.guild, this, key, value);
	}
	
	@Nonnull
	@Override
	public Collection<ConfigurationCommand.ChangeConfigType> getAllowedActions(){
		return Set.of(ADD, REMOVE, SHOW);
	}
	
	@Nonnull
	@Override
	public ConfigType getType(){
		return ConfigType.MAP;
	}
}
