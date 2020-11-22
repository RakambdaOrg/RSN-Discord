package fr.raksrinana.rsndiscord.modules.settings.command.helpers;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.modules.settings.command.BaseConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.modules.settings.ConfigurationOperation.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

public abstract class SetConfigurationCommand<T> extends BaseConfigurationCommand{
	protected SetConfigurationCommand(final Command parent){
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
			var value = this.extractValue(event, args);
			this.getConfig(guild).ifPresentOrElse(config -> config.add(value), () -> this.createConfig(guild, value));
			var embed = this.getConfigEmbed(event, SET.name(), GREEN)
					.addField(translate(guild, "command.config.helpers.value-added"), value.toString(), false)
					.build();
			channel.sendMessage(embed).submit();
		}
		catch(final IllegalArgumentException e){
			channel.sendMessage(e.getMessage()).submit();
		}
	}
	
	@Override
	protected void onRemove(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		var guild = event.getGuild();
		var channel = event.getChannel();
		
		try{
			var value = this.extractValue(event, args);
			this.removeConfig(guild, value);
			var embed = this.getConfigEmbed(event, REMOVE.name(), GREEN)
					.addField(translate(guild, "command.config.helpers.value-removed"), value.toString(), false)
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
				.flatMap(Set::stream)
				.map(Objects::toString)
				.collect(Collectors.joining(", "));
		var embed = this.getConfigEmbed(event, SHOW.name(), GREEN)
				.addField(translate(event.getGuild(), "command.config.helpers.values"), values, false)
				.build();
		event.getChannel().sendMessage(embed).submit();
	}
	
	protected abstract void removeConfig(@NonNull Guild guild, @NonNull T value);
	
	@NonNull
	protected abstract T extractValue(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args) throws IllegalArgumentException;
	
	@NonNull
	protected abstract Optional<Set<T>> getConfig(@NonNull Guild guild);
	
	protected abstract void createConfig(@NonNull Guild guild, @NonNull T value);
}
