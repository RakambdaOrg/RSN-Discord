package fr.mrcraftcod.gunterdiscord.utils;

import fr.mrcraftcod.gunterdiscord.settings.configs.ModoRolesConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.json.JSONObject;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
@SuppressWarnings("WeakerAccess")
public class Utilities{
	/**
	 * Check if a member have a role.
	 *
	 * @param member The member to test.
	 * @param roles  The roles to search for.
	 *
	 * @return True if the member have the role, false otherwise.
	 */
	public static boolean hasRole(final Member member, final List<Role> roles){
		return roles.stream().anyMatch(r -> hasRole(member, r));
	}
	
	/**
	 * Get a role.
	 *
	 * @param guild The guild the role is in.
	 * @param name  The role to search for.
	 *
	 * @return The role or null if not found.
	 */
	public static List<Role> getRole(final Guild guild, final String name){
		return guild.getJDA().getRoles().stream().filter(r -> r.getName().equalsIgnoreCase(name)).filter(r -> r.getGuild().equals(guild)).collect(Collectors.toList());
	}
	
	/**
	 * Tell if a member is part of the team (admin or moderator).
	 *
	 * @param member The member to test.
	 *
	 * @return True if part of the team, false otherwise.
	 */
	public static boolean isTeam(final Member member){
		return isModerator(member) || isAdmin(member);
	}
	
	/**
	 * Tell if the member is a moderator.
	 *
	 * @param member The member to test.
	 *
	 * @return True if moderator, false otherwise.
	 */
	public static boolean isModerator(final Member member){
		return Utilities.hasRole(member, new ModoRolesConfig(member.getGuild()).getAsList()) || isAdmin(member);
	}
	
	/**
	 * Tell if the member is an admin.
	 *
	 * @param member The member to test.
	 *
	 * @return True if admin, false otherwise.
	 */
	public static boolean isAdmin(final Member member){
		return member.getRoles().stream().anyMatch(role -> role.hasPermission(Permission.ADMINISTRATOR)) || isCreator(member);
	}
	
	/**
	 * Check if a member have a role.
	 *
	 * @param member The member to test.
	 * @param roles  The roles to search for.
	 *
	 * @return True if the member have the role, false otherwise.
	 */
	public static boolean hasRoleIDs(final Member member, final List<Long> roles){
		return roles.stream().map(r -> member.getGuild().getRoleById(r)).anyMatch(r -> hasRole(member, r));
	}
	
	/**
	 * Check if a member have a role.
	 *
	 * @param member The member to test.
	 * @param role   The role to search for.
	 *
	 * @return True if the member have the role, false otherwise.
	 */
	public static boolean hasRole(final Member member, final Role role){
		return member.getRoles().contains(role);
	}
	
	/**
	 * Get a server emote as a mention.
	 *
	 * @param name The name of the emote.
	 *
	 * @return The mention, or empty string if not found.
	 */
	public static String getEmoteMention(final Guild guild, final String name){
		final var emotes = guild.getEmotesByName(name, true);
		if(emotes.size() < 1){
			return "";
		}
		return emotes.get(0).getAsMention();
	}
	
	/**
	 * Get all the members that have a role.
	 *
	 * @param roles The roles to search for.
	 *
	 * @return The members that have this role.
	 */
	public static List<Member> getMembersWithRole(final List<Role> roles){
		return roles.stream().map(Utilities::getMembersWithRole).flatMap(Collection::stream).collect(Collectors.toList());
	}
	
	/**
	 * Get all the members that have a role.
	 *
	 * @param role The role to search for.
	 *
	 * @return The members that have this role.
	 */
	public static List<Member> getMembersWithRole(final Role role){
		return role.getGuild().getMembersWithRoles(role);
	}
	
	/**
	 * Tell if a user is this bot created.
	 *
	 * @param member The member to test.
	 *
	 * @return True if the creator, false otherwise.
	 */
	public static boolean isCreator(final Member member){
		return member.getUser().getIdLong() == 170119951498084352L || member.getUser().getIdLong() == 432628353024131085L;
	}
	
	/**
	 * Builds a embed builder from a message embed.
	 *
	 * @param messageEmbed The message embed to build from.
	 *
	 * @return An embed builder.
	 */
	public static EmbedBuilder buildEmbed(final MessageEmbed messageEmbed){
		final var builder = buildEmbed(null, messageEmbed.getColor(), messageEmbed.getTitle());
		builder.setAuthor(messageEmbed.getAuthor().getName(), messageEmbed.getAuthor().getUrl(), messageEmbed.getAuthor().getIconUrl());
		builder.setDescription(messageEmbed.getDescription());
		messageEmbed.getFields().forEach(builder::addField);
		return builder;
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
	public static EmbedBuilder buildEmbed(final User author, final Color color, final String title){
		return buildEmbed(author, color, title, null);
	}
	
	/**
	 * Build an embed.
	 *
	 * @param author   The author.
	 * @param color    The color.
	 * @param title    The title.
	 * @param titleURL The url of the title.
	 *
	 * @return The builder.
	 */
	public static EmbedBuilder buildEmbed(final User author, final Color color, final String title, final String titleURL){
		final var builder = new EmbedBuilder();
		if(author != null){
			builder.setAuthor(author.getName(), null, author.getAvatarUrl());
		}
		builder.setColor(color);
		builder.setTitle(title, titleURL);
		return builder;
	}
	
	/**
	 * Transform an embed into text.
	 *
	 * @param embed The embed.
	 *
	 * @return The text.
	 */
	static String getEmbedForLog(final MessageEmbed embed){
		final var builder = new StringBuilder("Embed " + embed.hashCode());
		builder.append("\n").append("Author: ").append(embed.getAuthor() == null ? "<NONE>" : embed.getAuthor());
		builder.append("\n").append("Title: ").append(embed.getTitle());
		builder.append("\n").append("Description: ").append(embed.getDescription());
		builder.append("\n").append("Color: ").append(embed.getColor());
		embed.getFields().forEach(f -> {
			builder.append("\n").append("FIELD:");
			builder.append("\n\t").append("Name: ").append(f.getName());
			builder.append("\n\t").append("Value: ").append(f.getValue());
		});
		return builder.toString();
	}
	
	public static <T> T getJSONMaybe(final JSONObject json, final Class<? extends T> klass, final String key){
		if(json.has(key)){
			final var value = json.get(key);
			if(value != JSONObject.NULL){
				return klass.cast(json.get(key));
			}
		}
		return null;
	}
}
