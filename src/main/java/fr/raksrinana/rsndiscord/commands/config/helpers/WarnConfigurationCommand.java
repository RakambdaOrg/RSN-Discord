package fr.raksrinana.rsndiscord.commands.config.helpers;

import fr.raksrinana.rsndiscord.commands.config.BaseConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.settings.guild.warns.WarnConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class WarnConfigurationCommand extends BaseConfigurationCommand{
	protected WarnConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Delay", "The delay before the person is unbanned", false);
		builder.addField("Role", "The role to set", false);
	}
	
	@NonNull
	@Override
	protected Set<ConfigurationOperation> getAllowedOperations(){
		return Set.of(ConfigurationOperation.SET, ConfigurationOperation.REMOVE, ConfigurationOperation.SHOW);
	}
	
	@Override
	protected void onSet(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		if(args.isEmpty()){
			Actions.reply(event, "Please mention the delay", null);
			return;
		}
		try{
			final var delay = Long.parseLong(args.pop());
			if(event.getMessage().getMentionedRoles().isEmpty()){
				Actions.reply(event, "Please mention the role", null);
				return;
			}
			final var role = event.getMessage().getMentionedRoles().get(0);
			this.getConfig(event.getGuild()).ifPresentOrElse(config -> {
				config.setRole(role);
				config.setDelay(delay);
			}, () -> this.createConfig(event.getGuild(), role, delay));
			final var builder = this.getConfigEmbed(event, ConfigurationOperation.SET.name(), Color.GREEN);
			builder.addField("Role", role.getAsMention(), false);
			builder.addField("Delay", String.valueOf(delay), false);
			Actions.reply(event, "", builder.build());
		}
		catch(final NumberFormatException e){
			Actions.reply(event, "Please mention the delay", null);
		}
	}
	
	@Override
	protected void onRemove(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		this.removeConfig(event.getGuild());
		final var builder = this.getConfigEmbed(event, ConfigurationOperation.REMOVE.name(), Color.GREEN);
		builder.addField("Role", "<<EMPTY>>", false);
		builder.addField("Delay", "<<EMPTY>>", false);
		Actions.reply(event, "", builder.build());
	}
	
	protected abstract void removeConfig(@NonNull Guild guild);
	
	@Override
	protected void onShow(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		final var builder = this.getConfigEmbed(event, ConfigurationOperation.SHOW.name(), Color.GREEN);
		builder.addField("Role", this.getConfig(event.getGuild()).map(WarnConfiguration::getRole).map(Objects::toString).orElse("<<EMPTY>>"), false);
		builder.addField("Delay", this.getConfig(event.getGuild()).map(WarnConfiguration::getDelay).map(Objects::toString).orElse("<<EMPTY>>"), false);
		Actions.reply(event, "", builder.build());
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return super.getDescription() + " [delay] [role]";
	}
	
	@NonNull
	protected abstract Optional<WarnConfiguration> getConfig(Guild guild);
	
	protected abstract void createConfig(@NonNull Guild guild, @NonNull Role role, long delay);
}
