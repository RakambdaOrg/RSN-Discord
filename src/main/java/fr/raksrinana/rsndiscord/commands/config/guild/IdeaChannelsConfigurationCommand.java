package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.SetConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class IdeaChannelsConfigurationCommand extends SetConfigurationCommand<ChannelConfiguration>{
	public IdeaChannelsConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<Set<ChannelConfiguration>> getConfig(@Nonnull final Guild guild){
		return Optional.of(NewSettings.getConfiguration(guild).getIdeaChannels());
	}
	
	@Override
	protected void createConfig(@Nonnull final Guild guild, @Nonnull final ChannelConfiguration value){
		final var set = new HashSet<ChannelConfiguration>();
		set.add(value);
		NewSettings.getConfiguration(guild).setIdeaChannels(set);
	}
	
	@Nonnull
	@Override
	protected ChannelConfiguration extractValue(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws IllegalArgumentException{
		if(event.getMessage().getMentionedChannels().isEmpty())
		{
			throw new IllegalArgumentException("Please mention a channel");
		}
		return new ChannelConfiguration(event.getMessage().getMentionedChannels().get(0));
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild, @Nonnull final ChannelConfiguration value){
		NewSettings.getConfiguration(guild).getIdeaChannels().remove(value);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Idea channels";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("ideaChannels");
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Channel", "The channel to add or remove", false);
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [channel]";
	}
}
