package fr.raksrinana.rsndiscord.command.impl.settings.guild.reactions;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.MapConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class SavedForwardingChannelsConfigurationCommand extends MapConfigurationCommand<ChannelConfiguration, ChannelConfiguration>{
	public SavedForwardingChannelsConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild, @NotNull ChannelConfiguration key){
		Settings.get(guild).getReactionsConfiguration().getSavedForwarding().remove(key);
	}
	
	@NotNull
	@Override
	protected ChannelConfiguration extractKey(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws IllegalArgumentException{
		return new ChannelConfiguration(event.getChannel());
	}
	
	@NotNull
	@Override
	protected ChannelConfiguration extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws IllegalArgumentException{
		if(event.getMessage().getMentionedChannels().isEmpty()){
			throw new IllegalArgumentException("Please mention a channel");
		}
		return new ChannelConfiguration(event.getMessage().getMentionedChannels().get(0));
	}
	
	@NotNull
	@Override
	protected Optional<Map<ChannelConfiguration, ChannelConfiguration>> getConfig(@NotNull Guild guild){
		return Optional.of(Settings.get(guild).getReactionsConfiguration().getSavedForwarding());
	}
	
	@Override
	protected void createConfig(@NotNull Guild guild, @NotNull ChannelConfiguration key, @NotNull ChannelConfiguration value){
		var map = new HashMap<ChannelConfiguration, ChannelConfiguration>();
		map.put(key, value);
		Settings.get(guild).getReactionsConfiguration().setSavedForwarding(map);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Channel", "The channel to add or remove", false);
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [channel]";
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Saved forwarding channels";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("savedForwardingChannels");
	}
}
