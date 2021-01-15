package fr.raksrinana.rsndiscord.command.impl.settings.guild.twitter;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.SetConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.*;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class UsersConfigurationCommand extends SetConfigurationCommand<Long>{
	public UsersConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild, @NonNull final Long value){
		Settings.get(guild).getTwitterConfiguration().getUserIds().remove(value);
	}
	
	@NonNull
	@Override
	protected Long extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args) throws IllegalArgumentException{
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please mention a user");
		}
		return Long.parseLong(args.pop());
	}
	
	@NonNull
	@Override
	protected Optional<Set<Long>> getConfig(@NonNull final Guild guild){
		return Optional.of(Settings.get(guild).getTwitterConfiguration().getUserIds());
	}
	
	@Override
	protected void createConfig(@NonNull final Guild guild, @NonNull final Long value){
		final var set = new HashSet<Long>();
		set.add(value);
		Settings.get(guild).getTwitterConfiguration().setUserIds(set);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user to add or remove", false);
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [user]";
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Users to fetch";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("users");
	}
}
