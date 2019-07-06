package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.RemoveRoleConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.RoleConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-12
 */
public class AutoRolesListener extends ListenerAdapter{
	@Override
	public void onGuildMemberJoin(@Nonnull final GuildMemberJoinEvent event){
		super.onGuildMemberJoin(event);
		try{
			NewSettings.getConfiguration(event.getGuild()).getAutoRoles().stream().map(RoleConfiguration::getRole).filter(Optional::isPresent).map(Optional::get).forEach(role -> Actions.giveRole(event.getUser(), List.of(role)));
			NewSettings.getConfiguration(event.getGuild()).getRemoveRoles().stream().filter(b -> Objects.equals(b.getUserId(), event.getUser().getIdLong())).filter( b -> b.getEndDate().after(new Date())).map(RemoveRoleConfiguration::getRole).filter(Optional::isPresent).map(Optional::get).forEach(r -> Actions.giveRole(event.getGuild(), event.getUser(), r));
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("Error on user join", e);
		}
	}
}
