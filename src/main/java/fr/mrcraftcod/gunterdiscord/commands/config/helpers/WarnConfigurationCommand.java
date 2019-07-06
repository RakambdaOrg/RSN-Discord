package fr.mrcraftcod.gunterdiscord.commands.config.helpers;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.config.BaseConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.config.IllegalOperationException;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class WarnConfigurationCommand extends BaseConfigurationCommand{
	public WarnConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected List<ConfigurationOperation> getAllowedOperations(){
		return List.of(ConfigurationOperation.SET, ConfigurationOperation.REMOVE, ConfigurationOperation.SHOW);
	}
	
	@Nonnull
	protected abstract Optional<WarnConfiguration> getConfig(Guild guild);
	
	@Override
	protected void onShow(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		final var builder = getConfigEmbed(event, ConfigurationOperation.SHOW.name(), Color.GREEN);
		builder.addField("Role", getConfig(event.getGuild()).map(WarnConfiguration::getRole).map(Objects::toString).orElse("<<EMPTY>>"), false);
		builder.addField("Delay", getConfig(event.getGuild()).map(WarnConfiguration::getDelay).map(Objects::toString).orElse("<<EMPTY>>"), false);
		Actions.reply(event, builder.build());
	}
	
	@Override
	protected void onSet(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
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
			getConfig(event.getGuild()).ifPresentOrElse(config -> {
				config.setRole(role);
				config.setDelay(delay);
			}, () -> createConfig(event.getGuild(), role, delay));
			final var builder = getConfigEmbed(event, ConfigurationOperation.SET.name(), Color.GREEN);
			builder.addField("Role", role.getAsMention(), false);
			builder.addField("Delay", "" + delay, false);
			Actions.reply(event, builder.build());
		}
		catch(NumberFormatException e){
			Actions.reply(event, "Please mention the delay");
		}
	}
	
	@Override
	protected void onRemove(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		removeConfig(event.getGuild());
		final var builder = getConfigEmbed(event, ConfigurationOperation.REMOVE.name(), Color.GREEN);
		builder.addField("Role", "<<EMPTY>>", false);
		builder.addField("Delay", "<<EMPTY>>", false);
		Actions.reply(event, builder.build());
	}
	
	protected abstract void removeConfig(@Nonnull Guild guild);
	
	protected abstract void createConfig(@Nonnull Guild guild, @Nonnull Role role, long delay);
	
	@Override
	protected void onAdd(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		throw new IllegalOperationException(ConfigurationOperation.ADD);
	}
	@Override
	public void addHelp(@Nonnull Guild guild, @Nonnull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Delay", "The delay before the person is unbanned", false);
		builder.addField("Role", "The role to set", false);
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return super.getDescription() + " [delay] [role]";
	}
}
