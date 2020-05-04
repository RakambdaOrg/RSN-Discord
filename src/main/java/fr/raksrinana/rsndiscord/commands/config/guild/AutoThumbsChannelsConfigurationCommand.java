package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.SetConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.*;

public class AutoThumbsChannelsConfigurationCommand extends SetConfigurationCommand<ChannelConfiguration>{
	public AutoThumbsChannelsConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild, @NonNull final ChannelConfiguration value){
		Settings.get(guild).getAutoThumbsChannels().remove(value);
	}
	
	@NonNull
	@Override
	protected ChannelConfiguration extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args) throws IllegalArgumentException{
		if(event.getMessage().getMentionedChannels().isEmpty()){
			throw new IllegalArgumentException("Please mention a channel");
		}
		return new ChannelConfiguration(event.getMessage().getMentionedChannels().get(0));
	}
	
	@NonNull
	@Override
	protected Optional<Set<ChannelConfiguration>> getConfig(@NonNull final Guild guild){
		return Optional.of(Settings.get(guild).getAutoThumbsChannels());
	}
	
	@Override
	protected void createConfig(@NonNull final Guild guild, @NonNull final ChannelConfiguration value){
		final var set = new HashSet<ChannelConfiguration>();
		set.add(value);
		Settings.get(guild).setAutoThumbsChannels(set);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Channel", "The channel to add or remove", false);
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [channel]";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Auto thumbs channels";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("autoThumbsChannels");
	}
}
