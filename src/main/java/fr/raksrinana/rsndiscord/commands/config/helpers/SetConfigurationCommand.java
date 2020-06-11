package fr.raksrinana.rsndiscord.commands.config.helpers;

import fr.raksrinana.rsndiscord.commands.config.BaseConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SetConfigurationCommand<T> extends BaseConfigurationCommand{
	protected SetConfigurationCommand(final Command parent){
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
			final var value = this.extractValue(event, args);
			this.getConfig(event.getGuild()).ifPresentOrElse(config -> config.add(value), () -> this.createConfig(event.getGuild(), value));
			final var builder = this.getConfigEmbed(event, ConfigurationOperation.SET.name(), Color.GREEN);
			builder.addField("Value added", value.toString(), false);
			Actions.reply(event, "", builder.build());
		}
		catch(final IllegalArgumentException e){
			Actions.reply(event, e.getMessage(), null);
		}
	}
	
	@Override
	protected void onRemove(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		try{
			final var value = this.extractValue(event, args);
			this.removeConfig(event.getGuild(), value);
			final var builder = this.getConfigEmbed(event, ConfigurationOperation.REMOVE.name(), Color.GREEN);
			builder.addField("Value removed", value.toString(), false);
			Actions.reply(event, "", builder.build());
		}
		catch(final IllegalArgumentException e){
			Actions.reply(event, e.getMessage(), null);
		}
	}
	
	@Override
	protected void onShow(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		final var values = this.getConfig(event.getGuild()).stream()
				.flatMap(Set::stream)
				.map(Objects::toString)
				.collect(Collectors.joining(", "));
		final var builder = this.getConfigEmbed(event, ConfigurationOperation.SHOW.name(), Color.GREEN);
		builder.addField("Values", values, false);
		Actions.reply(event, "", builder.build());
	}
	
	protected abstract void removeConfig(@NonNull Guild guild, @NonNull T value);
	
	@NonNull
	protected abstract T extractValue(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args) throws IllegalArgumentException;
	
	@NonNull
	protected abstract Optional<Set<T>> getConfig(@NonNull Guild guild);
	
	protected abstract void createConfig(@NonNull Guild guild, @NonNull T value);
}
