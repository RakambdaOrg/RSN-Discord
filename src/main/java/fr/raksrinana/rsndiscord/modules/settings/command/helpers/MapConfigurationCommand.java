package fr.raksrinana.rsndiscord.modules.settings.command.helpers;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.modules.settings.command.BaseConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.*;
import static fr.raksrinana.rsndiscord.modules.settings.ConfigurationOperation.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;
import static java.util.stream.Collectors.joining;

public abstract class MapConfigurationCommand<K, V> extends BaseConfigurationCommand{
	protected MapConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	protected Set<ConfigurationOperation> getAllowedOperations(){
		return Set.of(ADD, REMOVE, SHOW);
	}
	
	@Override
	protected void onAdd(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		var guild = event.getGuild();
		var channel = event.getChannel();
		
		try{
			var key = this.extractKey(event, args);
			var value = this.extractValue(event, args);
			
			this.getConfig(guild)
					.ifPresentOrElse(config -> config.put(key, value),
							() -> this.createConfig(guild, key, value));
			
			var embed = this.getConfigEmbed(event, SET.name(), GREEN)
					.addField(translate(guild, "command.config.helpers.value-added"), key.toString() + "=" + value.toString(), false)
					.build();
			channel.sendMessage(embed).submit();
		}
		catch(final IllegalArgumentException e){
			channel.sendMessage(e.getMessage()).submit();
		}
	}
	
	@Override
	protected void onRemove(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		var channel = event.getChannel();
		
		try{
			var guild = event.getGuild();
			var key = this.extractKey(event, args);
			this.removeConfig(guild, key);
			var embed = this.getConfigEmbed(event, REMOVE.name(), GREEN)
					.addField(translate(guild, "command.config.helpers.value-removed"), key.toString(), false)
					.build();
			channel.sendMessage(embed).submit();
		}
		catch(final IllegalArgumentException e){
			channel.sendMessage(e.getMessage()).submit();
		}
	}
	
	@Override
	protected void onShow(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		var values = this.getConfig(event.getGuild()).stream()
				.map(Map::entrySet)
				.flatMap(Set::stream)
				.map(Objects::toString)
				.collect(joining(", "));
		var embed = this.getConfigEmbed(event, SHOW.name(), GREEN)
				.addField(translate(event.getGuild(), "command.config.helpers.values"), values, false)
				.build();
		event.getChannel().sendMessage(embed).submit();
	}
	
	protected abstract void removeConfig(@NonNull Guild guild, @NonNull K key);
	
	@NonNull
	protected abstract K extractKey(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args) throws IllegalArgumentException;
	
	@NonNull
	protected abstract V extractValue(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args) throws IllegalArgumentException;
	
	@NonNull
	protected abstract Optional<Map<K, V>> getConfig(@NonNull Guild guild);
	
	protected abstract void createConfig(@NonNull Guild guild, @NonNull K key, @NonNull V value);
}
