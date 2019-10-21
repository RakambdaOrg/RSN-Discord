package fr.raksrinana.rsndiscord.commands.warn;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.guild.warns.WarnConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@BotCommand
public class DoubleWarnCommand extends WarnCommand{
	@Nonnull
	@Override
	protected Optional<Role> getRole(@Nonnull final Guild guild, @Nonnull final Message message, @Nonnull final LinkedList<String> args){
		return NewSettings.getConfiguration(guild).getWarnsConfiguration().getDoubleWarn().flatMap(WarnConfiguration::getRole);
	}
	
	@Override
	protected long getTime(@Nonnull final Guild guild, @Nonnull final Message message, @Nonnull final LinkedList<String> args){
		return NewSettings.getConfiguration(guild).getWarnsConfiguration().getDoubleWarn().map(WarnConfiguration::getDelay).orElse(DEFAULT_TIME);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Double warn";
	}
	
	@Nonnull
	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public List<String> getCommandStrings(){
		return List.of("doublewarn", "dwarn");
	}
}