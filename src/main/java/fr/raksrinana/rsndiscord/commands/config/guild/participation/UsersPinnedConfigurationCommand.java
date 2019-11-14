package fr.raksrinana.rsndiscord.commands.config.guild.participation;

import fr.raksrinana.rsndiscord.commands.config.helpers.SetConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.*;

public class UsersPinnedConfigurationCommand extends SetConfigurationCommand<UserConfiguration>{
	public UsersPinnedConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	protected Optional<Set<UserConfiguration>> getConfig(@NonNull final Guild guild){
		return Optional.of(Settings.get(guild).getParticipationConfiguration().getUsersPinned());
	}
	
	@Override
	protected void createConfig(@NonNull final Guild guild, @NonNull final UserConfiguration value){
		final var set = new HashSet<UserConfiguration>();
		set.add(value);
		Settings.get(guild).getParticipationConfiguration().setUsersPinned(set);
	}
	
	@NonNull
	@Override
	protected UserConfiguration extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args) throws IllegalArgumentException{
		if(event.getMessage().getMentionedUsers().isEmpty()){
			throw new IllegalArgumentException("Please mention a user");
		}
		return new UserConfiguration(event.getMessage().getMentionedUsers().get(0));
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild, @NonNull final UserConfiguration value){
		Settings.get(guild).getParticipationConfiguration().getUsersPinned().remove(value);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user to add or remove", false);
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [user]";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Pinned users";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("usersPinned");
	}
}
