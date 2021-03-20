package fr.raksrinana.rsndiscord.command.impl.settings.guild;

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

public class AutoDeleteConfigurationCommand extends MapConfigurationCommand<ChannelConfiguration, Integer>{
	public AutoDeleteConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild, @NotNull ChannelConfiguration key){
		Settings.get(guild).getAutoDeleteChannels().remove(key);
	}
	
	@NotNull
	@Override
	protected ChannelConfiguration extractKey(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws IllegalArgumentException{
		args.pop();
		return new ChannelConfiguration(event.getChannel());
	}
	
	@NotNull
	@Override
	protected Integer extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws IllegalArgumentException{
		if(event.getMessage().getMentionedChannels().isEmpty()){
			throw new IllegalArgumentException("Please mention a channel");
		}
		return Integer.parseInt(args.pop());
	}
	
	@NotNull
	@Override
	protected Optional<Map<ChannelConfiguration, Integer>> getConfig(@NotNull Guild guild){
		return Optional.of(Settings.get(guild).getAutoDeleteChannels());
	}
	
	@Override
	protected void createConfig(@NotNull Guild guild, @NotNull ChannelConfiguration key, @NotNull Integer value){
		var map = new HashMap<ChannelConfiguration, Integer>();
		map.put(key, value);
		Settings.get(guild).setAutoDeleteChannels(map);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Channel", "The channel to add or remove", false);
		builder.addField("Duration", "The duration before deleting messages (minutes)", false);
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [channel] <duration>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("autoDelete");
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Automatically delete messages in a channel";
	}
}
