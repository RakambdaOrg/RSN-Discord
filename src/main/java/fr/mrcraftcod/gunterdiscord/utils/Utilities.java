package fr.mrcraftcod.gunterdiscord.utils;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class Utilities
{
	private final Settings settings;
	
	public Utilities(Settings settings)
	{
		this.settings = settings;
	}
	
	public boolean isModerator(Member member)
	{
		for(Role role : member.getRoles())
			if(settings.getModeratorsRoles().contains(role.getAsMention()))
				return true;
		return false;
	}
	
	public boolean isAdmin(Member member)
	{
		for(Role role : member.getRoles())
			if(role.hasPermission(Permission.ADMINISTRATOR))
				return true;
		return false;
	}
	
	public boolean isTeam(Member member)
	{
		return Main.utilities.isModerator(member) || Main.utilities.isAdmin(member);
	}
}
