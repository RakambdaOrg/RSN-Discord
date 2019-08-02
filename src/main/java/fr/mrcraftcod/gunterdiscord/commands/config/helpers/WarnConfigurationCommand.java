package fr.mrcraftcod.gunterdiscord.commands.config.helpers;

import fr.mrcraftcod.gunterdiscord.commands.config.BaseConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.config.IllegalOperationException;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.settings.ConfigurationOperation;
import fr.mrcraftcod.gunterdiscord.settings.guild.warns.WarnConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class WarnConfigurationCommand extends BaseConfigurationCommand{
	protected WarnConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Set<ConfigurationOperation> getAllowedOperations(){
		return Set.of(ConfigurationOperation.SET, ConfigurationOperation.REMOVE, ConfigurationOperation.SHOW);
	}
	
	@Nonnull
	protected abstract Optional<WarnConfiguration> getConfig(Guild guild);
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Delay", "The delay before the person is unbanned", false);
		builder.addField("Role", "The role to set", false);
	}
	
	@Override
	protected void onShow(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		final var builder = this.getConfigEmbed(event, ConfigurationOperation.SHOW.name(), Color.GREEN);
		builder.addField("Role", this.getConfig(event.getGuild()).map(WarnConfiguration::getRole).map(Objects::toString).orElse("<<EMPTY>>"), false);
		builder.addField("Delay", this.getConfig(event.getGuild()).map(WarnConfiguration::getDelay).map(Objects::toString).orElse("<<EMPTY>>"), false);
		Actions.reply(event, builder.build());
	}
	
	@Override
	protected void onRemove(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		this.removeConfig(event.getGuild());
		final var builder = this.getConfigEmbed(event, ConfigurationOperation.REMOVE.name(), Color.GREEN);
		builder.addField("Role", "<<EMPTY>>", false);
		builder.addField("Delay", "<<EMPTY>>", false);
		Actions.reply(event, builder.build());
	}
	
	protected abstract void removeConfig(@Nonnull Guild guild);
	
	protected abstract void createConfig(@Nonnull Guild guild, @Nonnull Role role, long delay);
	
	@Override
	protected void onSet(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		if(args.isEmpty()){
			Actions.reply(event, "Please mention the delay");
			return;
		}
		try{
			final var delay = Long.parseLong(args.pop());
			if(event.getMessage().getMentionedRoles().isEmpty()){
				Actions.reply(event, "Please mention the role");
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
			Actions.reply(event, builder.build());
		}
		catch(final NumberFormatException e){
			Actions.reply(event, "Please mention the delay");
		}
	}

	@Override
	protected void onAdd(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws IllegalOperationException{
		throw new IllegalOperationException(ConfigurationOperation.ADD);
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return super.getDescription() + " [delay] [role]";
	}
}
