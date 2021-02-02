package fr.raksrinana.rsndiscord.command.impl.settings.guild.reactions;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.SetConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class AutoTodoChannelsConfigurationCommand extends SetConfigurationCommand<ChannelConfiguration>{
	public AutoTodoChannelsConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	protected Optional<Set<ChannelConfiguration>> getConfig(@NotNull Guild guild){
		return Optional.of(Settings.get(guild).getReactionsConfiguration().getAutoTodoChannels());
	}
	
	@NotNull
	@Override
	protected ChannelConfiguration extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws IllegalArgumentException{
		if(event.getMessage().getMentionedChannels().isEmpty()){
			throw new IllegalArgumentException("Please mention a channel");
		}
		return new ChannelConfiguration(event.getMessage().getMentionedChannels().get(0));
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild, @NotNull ChannelConfiguration value){
		Settings.get(guild).getReactionsConfiguration().getAutoTodoChannels().remove(value);
	}
	
	@Override
	protected void createConfig(@NotNull Guild guild, @NotNull ChannelConfiguration value){
		var set = new HashSet<ChannelConfiguration>();
		set.add(value);
		Settings.get(guild).getReactionsConfiguration().setAutoTodoChannels(set);
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
		return "Auto todo channels";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("autoTodoChannels");
	}
}
