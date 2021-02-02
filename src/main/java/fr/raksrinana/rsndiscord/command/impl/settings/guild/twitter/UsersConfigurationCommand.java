package fr.raksrinana.rsndiscord.command.impl.settings.guild.twitter;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.SetConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class UsersConfigurationCommand extends SetConfigurationCommand<Long>{
	public UsersConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild, @NotNull Long value){
		Settings.get(guild).getTwitterConfiguration().getUserIds().remove(value);
	}
	
	@NotNull
	@Override
	protected Long extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws IllegalArgumentException{
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please mention a user");
		}
		return Long.parseLong(args.pop());
	}
	
	@NotNull
	@Override
	protected Optional<Set<Long>> getConfig(@NotNull Guild guild){
		return Optional.of(Settings.get(guild).getTwitterConfiguration().getUserIds());
	}
	
	@Override
	protected void createConfig(@NotNull Guild guild, @NotNull Long value){
		var set = new HashSet<Long>();
		set.add(value);
		Settings.get(guild).getTwitterConfiguration().setUserIds(set);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user to add or remove", false);
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [user]";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Users to fetch";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("users");
	}
}
