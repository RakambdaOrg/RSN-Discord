package fr.mrcraftcod.gunterdiscord.utils;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.ModoRolesConfig;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import java.io.InvalidClassException;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class Utilities
{
	public static boolean isModerator(Member member)
	{
		for(Role role : member.getRoles())
		{
			try
			{
				if(new ModoRolesConfig().getAsList().contains(role.getAsMention()))
					return true;
			}
			catch(NoValueDefinedException | InvalidClassException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static boolean isAdmin(Member member)
	{
		for(Role role : member.getRoles())
			if(role.hasPermission(Permission.ADMINISTRATOR))
				return true;
		return false;
	}
	
	public static Role getRole(JDA jda, Roles role)
	{
		return getRole(jda, role.getRole());
	}
	
	public static Role getRole(JDA jda, String name)
	{
		for(Role role : jda.getRoles())
			if(role.getName().equalsIgnoreCase(name))
				return role;
		return null;
	}
	
	public static boolean isTeam(Member member)
	{
		return isModerator(member) || isAdmin(member);
	}
	
	public static String getEmoteMention(String name)
	{
		List<Emote> emotes = Main.getJDA().getEmotesByName(name, true);
		if(emotes.size() < 1)
			return "";
		return emotes.get(0).getAsMention();
	}
}
