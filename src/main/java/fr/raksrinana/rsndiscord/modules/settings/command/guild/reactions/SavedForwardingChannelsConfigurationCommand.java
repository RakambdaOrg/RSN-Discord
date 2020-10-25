package fr.raksrinana.rsndiscord.modules.settings.command.guild.reactions;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.command.helpers.MapConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.*;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class SavedForwardingChannelsConfigurationCommand extends MapConfigurationCommand<ChannelConfiguration, ChannelConfiguration>{
	public SavedForwardingChannelsConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild, @NonNull final ChannelConfiguration key){
		Settings.get(guild).getReactionsConfiguration().getSavedForwarding().remove(key);
	}
	
	@NonNull
	@Override
	protected ChannelConfiguration extractKey(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args) throws IllegalArgumentException{
		return new ChannelConfiguration(event.getChannel());
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
	protected Optional<Map<ChannelConfiguration, ChannelConfiguration>> getConfig(@NonNull final Guild guild){
		return Optional.of(Settings.get(guild).getReactionsConfiguration().getSavedForwarding());
	}
	
	@Override
	protected void createConfig(@NonNull Guild guild, @NonNull ChannelConfiguration key, @NonNull ChannelConfiguration value){
		final var map = new HashMap<ChannelConfiguration, ChannelConfiguration>();
		map.put(key, value);
		Settings.get(guild).getReactionsConfiguration().setSavedForwarding(map);
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
	public String getName(@NonNull Guild guild){
		return "Saved forwarding channels";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("savedForwardingChannels");
	}
}
