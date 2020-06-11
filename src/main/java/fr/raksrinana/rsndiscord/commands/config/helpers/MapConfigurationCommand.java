package fr.raksrinana.rsndiscord.commands.config.helpers;

import fr.raksrinana.rsndiscord.commands.config.BaseConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

public abstract class MapConfigurationCommand<K, V> extends BaseConfigurationCommand{
	protected MapConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	protected Set<ConfigurationOperation> getAllowedOperations(){
		return Set.of(ConfigurationOperation.ADD, ConfigurationOperation.REMOVE, ConfigurationOperation.SHOW);
	}
	
	@Override
	protected void onAdd(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		try{
			final var key = this.extractKey(event, args);
			final var value = this.extractValue(event, args);
			this.getConfig(event.getGuild()).ifPresentOrElse(config -> config.put(key, value), () -> this.createConfig(event.getGuild(), key, value));
			final var builder = this.getConfigEmbed(event, ConfigurationOperation.SET.name(), Color.GREEN);
			builder.addField("Value added", key.toString() + "=" + value.toString(), false);
			Actions.reply(event, "", builder.build());
		}
		catch(final IllegalArgumentException e){
			Actions.reply(event, e.getMessage(), null);
		}
	}
	
	@Override
	protected void onRemove(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		try{
			final var key = this.extractKey(event, args);
			this.removeConfig(event.getGuild(), key);
			final var builder = this.getConfigEmbed(event, ConfigurationOperation.REMOVE.name(), Color.GREEN);
			builder.addField("Value removed", key.toString(), false);
			Actions.reply(event, "", builder.build());
		}
		catch(final IllegalArgumentException e){
			Actions.reply(event, e.getMessage(), null);
		}
	}
	
	@Override
	protected void onShow(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		final var values = this.getConfig(event.getGuild()).stream()
				.map(Map::entrySet)
				.flatMap(Set::stream)
				.map(Objects::toString)
				.collect(Collectors.joining(", "));
		final var builder = this.getConfigEmbed(event, ConfigurationOperation.SHOW.name(), Color.GREEN);
		builder.addField("Values", values, false);
		Actions.reply(event, "", builder.build());
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
