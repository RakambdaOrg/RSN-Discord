package fr.mrcraftcod.gunterdiscord.commands.config.guild;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.config.helpers.ListConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class AutoRolesConfigurationCommand extends ListConfigurationCommand<RoleConfiguration>{
	public AutoRolesConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<List<RoleConfiguration>> getConfig(@Nonnull Guild guild){
		return Optional.of(NewSettings.getConfiguration(guild).getAutoRoles());
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild, @Nonnull RoleConfiguration value){
		NewSettings.getConfiguration(guild).getAutoRoles().remove(value);
	}
	
	@Override
	protected void createConfig(@Nonnull Guild guild, @Nonnull RoleConfiguration value){
		final var list = new ArrayList<RoleConfiguration>();
		list.add(value);
		NewSettings.getConfiguration(guild).setAutoRoles(list);
	}
	
	@Nonnull
	@Override
	protected RoleConfiguration extractValue(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalArgumentException{
		if(event.getMessage().getMentionedRoles().isEmpty())
		{
			throw new IllegalArgumentException("Please mention a role");
		}
		return new RoleConfiguration(event.getMessage().getMentionedRoles().get(0));
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
	
	@Override
	public void addHelp(@Nonnull Guild guild, @Nonnull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Role", "The role to add or remove", false);
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [role]";
	}
}
