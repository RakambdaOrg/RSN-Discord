package fr.raksrinana.rsndiscord.command.impl.settings.helpers;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.BaseConfigurationCommand;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import static fr.raksrinana.rsndiscord.settings.ConfigurationOperation.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;
import static java.util.stream.Collectors.joining;

public abstract class MapConfigurationCommand<K, V> extends BaseConfigurationCommand{
	protected MapConfigurationCommand(Command parent){
		super(parent);
	}
	
	@NotNull
	@Override
	protected Set<ConfigurationOperation> getAllowedOperations(){
		return Set.of(ADD, REMOVE, SHOW);
	}
	
	@Override
	protected void onAdd(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		var guild = event.getGuild();
		
		try{
			var key = extractKey(event, args);
			var value = extractValue(event, args);
			
			getConfig(guild)
					.ifPresentOrElse(config -> config.put(key, value),
							() -> createConfig(guild, key, value));
			
			var embed = getConfigEmbed(event, SET.name(), GREEN)
					.addField(translate(guild, "command.config.helpers.value-added"), key + "=" + value, false)
					.build();
			JDAWrappers.message(event, embed).submit();
		}
		catch(IllegalArgumentException e){
			JDAWrappers.message(event, e.getMessage()).submit();
		}
	}
	
	@Override
	protected void onRemove(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		try{
			var guild = event.getGuild();
			var key = extractKey(event, args);
			removeConfig(guild, key);
			var embed = getConfigEmbed(event, REMOVE.name(), GREEN)
					.addField(translate(guild, "command.config.helpers.value-removed"), key.toString(), false)
					.build();
			JDAWrappers.message(event, embed).submit();
		}
		catch(IllegalArgumentException e){
			JDAWrappers.message(event, e.getMessage()).submit();
		}
	}
	
	@Override
	protected void onShow(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		var values = getConfig(event.getGuild()).stream()
				.map(Map::entrySet)
				.flatMap(Set::stream)
				.map(Objects::toString)
				.collect(joining(", "));
		var embed = getConfigEmbed(event, SHOW.name(), GREEN)
				.addField(translate(event.getGuild(), "command.config.helpers.values"), values, false)
				.build();
		JDAWrappers.message(event, embed).submit();
	}
	
	protected abstract void removeConfig(@NotNull Guild guild, @NotNull K key);
	
	@NotNull
	protected abstract K extractKey(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws IllegalArgumentException;
	
	@NotNull
	protected abstract V extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws IllegalArgumentException;
	
	@NotNull
	protected abstract Optional<Map<K, V>> getConfig(@NotNull Guild guild);
	
	protected abstract void createConfig(@NotNull Guild guild, @NotNull K key, @NotNull V value);
}
