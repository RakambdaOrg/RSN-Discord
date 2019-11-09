package fr.raksrinana.rsndiscord.commands.config.guild.twitch;

import fr.raksrinana.rsndiscord.commands.config.helpers.SetConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class TwitchAutoConnectUsersConfigurationCommand extends SetConfigurationCommand<String>{
	public TwitchAutoConnectUsersConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<Set<String>> getConfig(@Nonnull final Guild guild){
		return Optional.of(Settings.getConfiguration(guild).getTwitchAutoConnectUsers());
	}
	
	@Override
	protected void createConfig(@Nonnull final Guild guild, @Nonnull final String value){
		final var set = new HashSet<String>();
		set.add(value);
		Settings.getConfiguration(guild).setTwitchAutoConnectUsers(set);
	}
	
	@Nonnull
	@Override
	protected String extractValue(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws IllegalArgumentException{
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please mention a user");
		}
		return args.pop();
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild, @Nonnull final String value){
		Settings.getConfiguration(guild).getTwitchAutoConnectUsers().remove(value);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user to add or remove", false);
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [user]";
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Users auto reconnect";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("autoReconnect");
	}
}
