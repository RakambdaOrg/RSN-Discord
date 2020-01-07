package fr.raksrinana.rsndiscord.commands.warn;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.warns.WarnConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@BotCommand
public class MegaWarnCommand extends WarnCommand{
	@NonNull
	@Override
	protected Optional<Role> getRole(@NonNull final Guild guild, @NonNull final Message message, @NonNull final LinkedList<String> args){
		return Settings.get(guild).getWarnsConfiguration().getMegaWarn().map(WarnConfiguration::getRole).flatMap(RoleConfiguration::getRole);
	}
	
	@Override
	protected long getTime(@NonNull final Guild guild, @NonNull final Message message, @NonNull final LinkedList<String> args){
		return Settings.get(guild).getWarnsConfiguration().getMegaWarn().map(WarnConfiguration::getDelay).orElse(DEFAULT_TIME);
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Mega warn";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("megawarn", "mwarn");
	}
}
