package fr.raksrinana.rsndiscord.modules.settings.command.helpers;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.modules.settings.command.BaseConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import static fr.raksrinana.rsndiscord.modules.settings.ConfigurationOperation.*;
import static java.awt.Color.GREEN;

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
		return Set.of(SET, REMOVE, SHOW);
	}
	
	@Override
	protected void onSet(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		var channel = event.getChannel();
		
		try{
			var value = this.extractValue(event, args);
			this.setConfig(event.getGuild(), value);
			var embed = this.getConfigEmbed(event, SET.name(), GREEN)
					.addField(this.getValueName(), value.toString(), false)
					.build();
			channel.sendMessage(embed).submit();
		}
		catch(final IllegalArgumentException e){
			channel.sendMessage(e.getMessage()).submit();
		}
	}
	
	protected abstract T extractValue(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args);
	
	protected abstract void setConfig(@NonNull Guild guild, @NonNull T value);
	
	@Override
	protected void onRemove(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		this.removeConfig(event.getGuild());
		var embed = this.getConfigEmbed(event, REMOVE.name(), GREEN)
				.addField(this.getValueName(), "<<EMPTY>>", false)
				.build();
		event.getChannel().sendMessage(embed).submit();
	}
	
	protected abstract void removeConfig(@NonNull Guild guild);
	
	@Override
	protected void onShow(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		var embed = this.getConfigEmbed(event, SHOW.name(), GREEN)
				.addField(this.getValueName(), this.getConfig(event.getGuild()).map(Objects::toString).orElse("<<EMPTY>>"), false)
				.build();
		event.getChannel().sendMessage(embed).submit();
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return super.getDescription(guild) + " [" + this.getValueName().toLowerCase() + "]";
	}
	
	@NonNull
	protected abstract Optional<T> getConfig(Guild guild);
	
	protected abstract String getValueName();
}
