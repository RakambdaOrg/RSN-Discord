package fr.mrcraftcod.gunterdiscord.commands.config.guild.participation;

import fr.mrcraftcod.gunterdiscord.commands.config.helpers.SetConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.UserConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class UsersPinnedConfigurationCommand extends SetConfigurationCommand<UserConfiguration>{
	public UsersPinnedConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<Set<UserConfiguration>> getConfig(@Nonnull Guild guild){
		return Optional.of(NewSettings.getConfiguration(guild).getParticipationConfiguration().getUsersPinned());
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild, @Nonnull UserConfiguration value){
		NewSettings.getConfiguration(guild).getParticipationConfiguration().getUsersPinned().remove(value);
	}
	
	@Override
	protected void createConfig(@Nonnull Guild guild, @Nonnull UserConfiguration value){
		final var set = new HashSet<UserConfiguration>();
		set.add(value);
		NewSettings.getConfiguration(guild).getParticipationConfiguration().setUsersPinned(set);
	}
	
	@Nonnull
	@Override
	protected UserConfiguration extractValue(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalArgumentException{
		if(event.getMessage().getMentionedUsers().isEmpty())
		{
			throw new IllegalArgumentException("Please mention a user");
		}
		return new UserConfiguration(event.getMessage().getMentionedUsers().get(0));
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Pinned users";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("usersPinned");
	}
	
	@Override
	public void addHelp(@Nonnull Guild guild, @Nonnull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user to add or remove", false);
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [user]";
	}
}
