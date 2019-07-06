package fr.mrcraftcod.gunterdiscord.commands.newconfig.guild;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.helpers.ListConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.newSettings.NewSettings;
import fr.mrcraftcod.gunterdiscord.newSettings.types.ChannelConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class IdeaChannelsConfigurationCommand extends ListConfigurationCommand<ChannelConfiguration>{
	public IdeaChannelsConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<List<ChannelConfiguration>> getConfig(@Nonnull Guild guild){
		return Optional.of(NewSettings.getConfiguration(guild).getIdeaChannels());
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild, @Nonnull ChannelConfiguration value){
		NewSettings.getConfiguration(guild).getIdeaChannels().remove(value);
	}
	
	@Override
	protected void createConfig(@Nonnull Guild guild, @Nonnull ChannelConfiguration value){
		final var list = new ArrayList<ChannelConfiguration>();
		list.add(value);
		NewSettings.getConfiguration(guild).setIdeaChannels(list);
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
		return "Idea channels";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("ideaChannels");
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
