package fr.raksrinana.rsndiscord.commands.config.helpers;

import fr.raksrinana.rsndiscord.commands.config.BaseConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.IllegalOperationException;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class ValueConfigurationCommand<T> extends BaseConfigurationCommand{
	protected ValueConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Set<ConfigurationOperation> getAllowedOperations(){
		return Set.of(ConfigurationOperation.SET, ConfigurationOperation.REMOVE, ConfigurationOperation.SHOW);
	}
	
	@Nonnull
	protected abstract Optional<T> getConfig(Guild guild);
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField(this.getValueName(), "The value to set", false);
	}
	
	protected abstract String getValueName();
	
	@Override
	protected void onShow(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		final var builder = this.getConfigEmbed(event, ConfigurationOperation.SHOW.name(), Color.GREEN);
		builder.addField(this.getValueName(), this.getConfig(event.getGuild()).map(Objects::toString).orElse("<<EMPTY>>"), false);
		Actions.reply(event, builder.build());
	}
	
	protected abstract void setConfig(@Nonnull Guild guild, @Nonnull T value);
	
	protected abstract T extractValue(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args);
	
	@Override
	protected void onRemove(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		this.removeConfig(event.getGuild());
		final var builder = this.getConfigEmbed(event, ConfigurationOperation.REMOVE.name(), Color.GREEN);
		builder.addField(this.getValueName(), "<<EMPTY>>", false);
		Actions.reply(event, builder.build());
	}
	
	protected abstract void removeConfig(@Nonnull Guild guild);
	
	@Override
	protected void onSet(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		try{
			final var value = this.extractValue(event, args);
			this.setConfig(event.getGuild(), value);
			final var builder = this.getConfigEmbed(event, ConfigurationOperation.SET.name(), Color.GREEN);
			builder.addField(this.getValueName(), value.toString(), false);
			Actions.reply(event, builder.build());
		}
		catch(final IllegalArgumentException e){
			Actions.reply(event, e.getMessage());
		}
	}
	
	@Override
	protected void onAdd(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws IllegalOperationException{
		throw new IllegalOperationException(ConfigurationOperation.ADD);
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return super.getDescription() + " [" + this.getValueName().toLowerCase() + "]";
	}
}
