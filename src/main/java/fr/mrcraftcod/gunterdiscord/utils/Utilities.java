package fr.mrcraftcod.gunterdiscord.utils;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.configs.ModoRolesConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import java.awt.*;
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
	/**
	 * Check if a member have a role.
	 *
	 * @param member The member to test.
	 * @param roles  The roles to search for.
	 *
	 * @return True if the member have the role, false otherwise.
	 */
	public static boolean hasRole(Member member, List<Role> roles)
	{
		return roles.stream().anyMatch(r -> hasRole(member, r));
	}
	
	/**
	 * Check if a member have a role.
	 *
	 * @param member The member to test.
	 * @param role   The role to search for.
	 *
	 * @return True if the member have the role, false otherwise.
	 */
	public static boolean hasRole(Member member, Role role)
	{
		return member.getRoles().contains(role);
	}
	
	/**
	 * Get a role.
	 *
	 * @param guild The guild the role is in.
	 * @param name  The role to search for.
	 *
	 * @return The role or null if not found.
	 */
	public static List<Role> getRole(Guild guild, String name)
	{
		return guild.getJDA().getRoles().stream().filter(r -> r.getName().equalsIgnoreCase(name)).filter(r -> r.getGuild().equals(guild)).collect(Collectors.toList());
	}
	
	/**
	 * Tell if a member is part of the team (admin or moderator).
	 *
	 * @param member The member to test.
	 *
	 * @return True if part of the team, false otherwise.
	 */
	public static boolean isTeam(Member member)
	{
		return isModerator(member) || isAdmin(member);
	}
	
	/**
	 * Tell if the member is a moderator.
	 *
	 * @param member The member to test.
	 *
	 * @return True if moderator, false otherwise.
	 */
	public static boolean isModerator(Member member)
	{
		return Utilities.hasRoleIDs(member, new ModoRolesConfig().getAsList(member.getGuild()));
	}
	
	/**
	 * Tell if the member is an admin.
	 *
	 * @param member The member to test.
	 *
	 * @return True if admin, false otherwise.
	 */
	public static boolean isAdmin(Member member)
	{
		for(Role role: member.getRoles())
			if(role.hasPermission(Permission.ADMINISTRATOR))
				return true;
		return false;
	}
	
	/**
	 * Check if a member have a role.
	 *
	 * @param member The member to test.
	 * @param roles  The roles to search for.
	 *
	 * @return True if the member have the role, false otherwise.
	 */
	public static boolean hasRoleIDs(Member member, List<Long> roles)
	{
		return roles.stream().map(r -> member.getGuild().getRoleById(r)).anyMatch(r -> hasRole(member, r));
	}
	
	/**
	 * Get a server emote as a mention.
	 *
	 * @param name The name of the emote.
	 *
	 * @return The mention, or empty string if not found.
	 */
	public static String getEmoteMention(String name)
	{
		List<Emote> emotes = Main.getJDA().getEmotesByName(name, true);
		if(emotes.size() < 1)
			return "";
		return emotes.get(0).getAsMention();
	}
	
	/**
	 * Get all the members that have a role.
	 *
	 * @param role The role to search for.
	 *
	 * @return The members that have this role.
	 */
	public static List<Member> getMembersRole(Role role)
	{
		return role.getGuild().getMembersWithRoles(role);
	}
	
	/**
	 * Get all the members that have a role.
	 *
	 * @param roles The roles to search for.
	 *
	 * @return The members that have this role.
	 */
	public static List<Member> getMembersRole(List<Role> roles)
	{
		return roles.stream().map(Utilities::getMembersRole).flatMap(Collection::stream).collect(Collectors.toList());
	}
	
	/**
	 * Tell if a user is this bot created.
	 *
	 * @param member The member to test.
	 *
	 * @return True if the creator, false otherwise.
	 */
	public static boolean isCreator(Member member)
	{
		return member.getUser().getIdLong() == 170119951498084352L;
	}
	
	/**
	 * Build an embed.
	 *
	 * @param author The author.
	 * @param color  The color.
	 * @param title  The title.
	 *
	 * @return The builder.
	 */
	public static EmbedBuilder buildEmbed(User author, Color color, String title)
	{
		EmbedBuilder builder = new EmbedBuilder();
		if(author != null)
			builder.setAuthor(author.getName(), null, author.getAvatarUrl());
		builder.setColor(color);
		builder.setTitle(title);
		return builder;
	}
}
