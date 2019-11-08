package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.SetConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class AutoRolesConfigurationCommand extends SetConfigurationCommand<RoleConfiguration>{
	public AutoRolesConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<Set<RoleConfiguration>> getConfig(@Nonnull final Guild guild){
		return Optional.of(NewSettings.getConfiguration(guild).getAutoRoles());
	}
	
	@Override
	protected void createConfig(@Nonnull final Guild guild, @Nonnull final RoleConfiguration value){
		final var set = new HashSet<RoleConfiguration>();
		set.add(value);
		NewSettings.getConfiguration(guild).setAutoRoles(set);
	}
	
	@Nonnull
	@Override
	protected RoleConfiguration extractValue(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws IllegalArgumentException{
		if(event.getMessage().getMentionedRoles().isEmpty()){
			throw new IllegalArgumentException("Please mention a role");
		}
		return new RoleConfiguration(event.getMessage().getMentionedRoles().get(0));
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild, @Nonnull final RoleConfiguration value){
		NewSettings.getConfiguration(guild).getAutoRoles().remove(value);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Role", "The role to add or remove", false);
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [role]";
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Auto roles";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("autoRoles");
	}
}
