package fr.mrcraftcod.gunterdiscord.commands.warn;

import fr.mrcraftcod.gunterdiscord.commands.generic.BotCommand;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.guild.warns.WarnConfiguration;
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
public class NormalWarnCommand extends WarnCommand{
	@Nonnull
	@Override
	protected Optional<Role> getRole(@Nonnull final Guild guild, @Nonnull final Message message, @Nonnull final LinkedList<String> args){
		return NewSettings.getConfiguration(guild).getWarnsConfiguration().getSimpleWarn().flatMap(WarnConfiguration::getRole);
	}
	
	@Override
	protected long getTime(@Nonnull final Guild guild, @Nonnull final Message message, @Nonnull final LinkedList<String> args){
		return NewSettings.getConfiguration(guild).getWarnsConfiguration().getSimpleWarn().map(WarnConfiguration::getDelay).orElse(24L * 3600);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Warn";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("warn");
	}
}
