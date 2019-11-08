package fr.raksrinana.rsndiscord.commands.config.helpers;

import fr.raksrinana.rsndiscord.commands.config.BaseConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.IllegalOperationException;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.utils.Actions;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SetConfigurationCommand<T> extends BaseConfigurationCommand{
	protected SetConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Set<ConfigurationOperation> getAllowedOperations(){
		return Set.of(ConfigurationOperation.ADD, ConfigurationOperation.REMOVE, ConfigurationOperation.SHOW);
	}
	
	@SuppressWarnings("DuplicatedCode")
	@Override
	protected void onRemove(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		try{
			final var value = this.extractValue(event, args);
			this.removeConfig(event.getGuild(), value);
			final var builder = this.getConfigEmbed(event, ConfigurationOperation.REMOVE.name(), Color.GREEN);
			builder.addField("Value removed", value.toString(), false);
			Actions.reply(event, builder.build());
		}
		catch(final IllegalArgumentException e){
			Actions.reply(event, e.getMessage());
		}
	}
	
	@SuppressWarnings("DuplicatedCode")
	@Override
	protected void onShow(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		final var values = this.getConfig(event.getGuild()).stream().flatMap(Set::stream).map(Objects::toString).collect(Collectors.joining(", "));
		final var builder = this.getConfigEmbed(event, ConfigurationOperation.SHOW.name(), Color.GREEN);
		builder.addField("Values", values, false);
		Actions.reply(event, builder.build());
	}
	
	@Nonnull
	protected abstract Optional<Set<T>> getConfig(@Nonnull Guild guild);
	
	@Override
	protected void onSet(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws IllegalOperationException{
		throw new IllegalOperationException(ConfigurationOperation.SET);
	}
	
	@SuppressWarnings("DuplicatedCode")
	@Override
	protected void onAdd(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		try{
			final var value = this.extractValue(event, args);
			this.getConfig(event.getGuild()).ifPresentOrElse(config -> config.add(value), () -> this.createConfig(event.getGuild(), value));
			final var builder = this.getConfigEmbed(event, ConfigurationOperation.SET.name(), Color.GREEN);
			builder.addField("Value added", value.toString(), false);
			Actions.reply(event, builder.build());
		}
		catch(final IllegalArgumentException e){
			Actions.reply(event, e.getMessage());
		}
	}
	
	protected abstract void createConfig(@Nonnull Guild guild, @Nonnull T value);
	
	@Nonnull
	protected abstract T extractValue(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalArgumentException;
	
	protected abstract void removeConfig(@Nonnull Guild guild, @Nonnull T value);
}
