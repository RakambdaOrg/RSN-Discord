package fr.raksrinana.rsndiscord.commands.config.helpers;

import fr.raksrinana.rsndiscord.commands.config.BaseConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class ValueConfigurationCommand<T> extends BaseConfigurationCommand{
	protected ValueConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField(this.getValueName(), "The value to set", false);
	}
	
	@NonNull
	@Override
	protected Set<ConfigurationOperation> getAllowedOperations(){
		return Set.of(ConfigurationOperation.SET, ConfigurationOperation.REMOVE, ConfigurationOperation.SHOW);
	}
	
	@Override
	protected void onSet(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		try{
			final var value = this.extractValue(event, args);
			this.setConfig(event.getGuild(), value);
			final var builder = this.getConfigEmbed(event, ConfigurationOperation.SET.name(), Color.GREEN);
			builder.addField(this.getValueName(), value.toString(), false);
			Actions.reply(event, "", builder.build());
		}
		catch(final IllegalArgumentException e){
			Actions.reply(event, e.getMessage(), null);
		}
	}
	
	protected abstract T extractValue(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args);
	
	protected abstract void setConfig(@NonNull Guild guild, @NonNull T value);
	
	@Override
	protected void onRemove(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		this.removeConfig(event.getGuild());
		final var builder = this.getConfigEmbed(event, ConfigurationOperation.REMOVE.name(), Color.GREEN);
		builder.addField(this.getValueName(), "<<EMPTY>>", false);
		Actions.reply(event, "", builder.build());
	}
	
	protected abstract void removeConfig(@NonNull Guild guild);
	
	@Override
	protected void onShow(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		final var builder = this.getConfigEmbed(event, ConfigurationOperation.SHOW.name(), Color.GREEN);
		builder.addField(this.getValueName(), this.getConfig(event.getGuild()).map(Objects::toString).orElse("<<EMPTY>>"), false);
		Actions.reply(event, "", builder.build());
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return super.getDescription() + " [" + this.getValueName().toLowerCase() + "]";
	}
	
	protected abstract String getValueName();
	
	@NonNull
	protected abstract Optional<T> getConfig(Guild guild);
}
