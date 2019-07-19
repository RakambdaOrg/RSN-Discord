package fr.mrcraftcod.gunterdiscord.utils;

import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
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
		return isAdmin(member) || NewSettings.getConfiguration(member.getGuild()).getModeratorRoles().stream().map(RoleConfiguration::getRole).filter(Optional::isPresent).map(Optional::get).anyMatch(r -> Utilities.hasRole(member, r));
	}
	
	/**
	 * Tell if the member is an admin.
	 *
	 * @param member The member to test.
	 *
	 * @return True if admin, false otherwise.
	 */
	public static boolean isAdmin(@Nullable final Member member){
		return Objects.nonNull(member) && member.getRoles().stream().anyMatch(role -> role.hasPermission(Permission.ADMINISTRATOR)) || isCreator(member);
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
	public static boolean isCreator(@Nullable final Member member){
		return Objects.nonNull(member) && (Objects.equals(member.getUser().getIdLong(), 170119951498084352L) || Objects.equals(member.getUser().getIdLong(), 432628353024131085L));
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
		if(Objects.nonNull(messageEmbed.getAuthor())){
			builder.setAuthor(messageEmbed.getAuthor().getName(), messageEmbed.getAuthor().getUrl(), messageEmbed.getAuthor().getIconUrl());
		}
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
		if(Objects.nonNull(author)){
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
		builder.append("\n").append("Author: ").append(Objects.isNull(embed.getAuthor()) ? "<NONE>" : embed.getAuthor());
		builder.append("\n").append("Title: ").append(embed.getTitle());
		builder.append("\n").append("Description: ").append(embed.getDescription());
		builder.append("\n").append("Color: ").append(embed.getColor());
		embed.getFields().forEach(field -> {
			builder.append("\n").append("FIELD:");
			builder.append("\n\t").append("Name: ").append(field.getName());
			builder.append("\n\t").append("Value: ").append(field.getValue());
		});
		return builder.toString();
	}
	
	public static <T> T getJSONMaybe(final JSONObject json, final Class<? extends T> klass, final String key){
		if(json.has(key)){
			final var value = json.get(key);
			if(!Objects.equals(value, JSONObject.NULL)){
				return klass.cast(json.get(key));
			}
		}
		return null;
	}
	
	public static String capitalize(String s){
		if(Objects.isNull(s) || s.isBlank()){
			return s;
		}
		if(s.length() == 1){
			return s;
		}
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
}
