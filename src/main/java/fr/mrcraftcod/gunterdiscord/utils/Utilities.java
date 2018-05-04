package fr.mrcraftcod.gunterdiscord.utils;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.configs.ModoRolesConfig;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import java.io.InvalidClassException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
				if(new ModoRolesConfig().getAsList().contains(role.getName()))
					return true;
			}
			catch(InvalidClassException e)
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
	
	public static boolean hasRole(Member member, Roles role)
	{
		return getRole(member.getGuild(), role).stream().anyMatch(r -> member.getRoles().contains(r));
	}
	
	public static boolean hasRole(Member member, Role role)
	{
		return member.getRoles().contains(role);
	}
	
	public static boolean hasRole(Member member, List<Role> roles)
	{
		return roles.stream().anyMatch(r -> hasRole(member, r));
	}
	
	public static List<Role> getRole(Guild guild, Roles role)
	{
		return getRole(guild, role.getRole());
	}
	
	public static List<Role> getRole(Guild guild, String name)
	{
		return guild.getJDA().getRoles().stream().filter(r -> r.getName().equalsIgnoreCase(name)).filter(r -> r.getGuild().equals(guild)).collect(Collectors.toList());
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
	
	public static List<Member> getMembersRole(Guild guild, Roles role)
	{
		return getMembersRole(getRole(guild, role));
	}
	
	public static List<Member> getMembersRole(Guild guild, Role role)
	{
		return guild.getMembersWithRoles(role);
	}
	
	public static List<Member> getMembersRole(List<Role> roles)
	{
		return roles.stream().map(r -> getMembersRole(r.getGuild(), r)).flatMap(Collection::stream).collect(Collectors.toList());
	}
}
