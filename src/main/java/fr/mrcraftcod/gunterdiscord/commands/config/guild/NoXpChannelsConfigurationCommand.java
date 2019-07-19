package fr.mrcraftcod.gunterdiscord.commands.config.guild;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.config.helpers.ListConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class NoXpChannelsConfigurationCommand extends ListConfigurationCommand<ChannelConfiguration>{
	public NoXpChannelsConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<List<ChannelConfiguration>> getConfig(@Nonnull Guild guild){
		return Optional.of(NewSettings.getConfiguration(guild).getNoXpChannels());
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild, @Nonnull ChannelConfiguration value){
		NewSettings.getConfiguration(guild).getNoXpChannels().remove(value);
	}
	
	@Override
	protected void createConfig(@Nonnull Guild guild, @Nonnull ChannelConfiguration value){
		final var list = new ArrayList<ChannelConfiguration>();
		list.add(value);
		NewSettings.getConfiguration(guild).setNoXpChannels(list);
	}
	
	@Nonnull
	@Override
	protected ChannelConfiguration extractValue(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalArgumentException{
		if(event.getMessage().getMentionedChannels().isEmpty())
		{
			throw new IllegalArgumentException("Please mention a channel");
		}
		return new ChannelConfiguration(event.getMessage().getMentionedChannels().get(0));
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "No XP channels";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("npXpChannels");
	}
	
	@Override
	public void addHelp(@Nonnull Guild guild, @Nonnull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Channel", "The channel to add or remove", false);
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [channel]";
	}
}
