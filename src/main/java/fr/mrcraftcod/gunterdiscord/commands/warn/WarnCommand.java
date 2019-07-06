package fr.mrcraftcod.gunterdiscord.commands.warn;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.RemoveRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Optional;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public abstract class WarnCommand extends BasicCommand{
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "L'The user to warn", false);
		builder.addField("Reason", "Reason of the warn", false);
	}
	
	@Nonnull
	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(!event.getMessage().getMentionedUsers().isEmpty()){
			final var user = event.getMessage().getMentionedUsers().get(0);
			args.poll();
			final var roleOptional = getRole(event.getGuild(), event.getMessage(), args);
			final var duration = getTime(event.getGuild(), event.getMessage(), args);
			final var reason = String.join(" ", args);
			final var builder = new EmbedBuilder();
			builder.setAuthor(user.getName(), null, user.getAvatarUrl());
			roleOptional.ifPresentOrElse(role -> {
				Actions.giveRole(event.getGuild(), user, role);
				new RemoveRoleConfig(event.getGuild()).addValue(user.getIdLong(), role.getIdLong(), (long) (System.currentTimeMillis() + duration * 24 * 60 * 60 * 1000L));
				builder.setColor(Color.GREEN);
				builder.addField("Congratulations", user.getAsMention() + " joined the role " + role.getAsMention() + " for " + duration + " day(s)", false);
				builder.addField("", "To know how your warn is doing, user the magic command: g?warninfo " + user.getAsMention(), false);
				getLogger(event.getGuild()).info("{} warned {} for {} days with role {}", event.getAuthor(), user, duration, role);
				if(!reason.isEmpty()){
					Actions.replyPrivate(event.getGuild(), user, "Warn reason: %s", reason);
				}
			}, () -> {
				builder.setColor(Color.RED);
				builder.addField("Error", "Please configure a role to give", true);
			});
			Actions.reply(event, builder.build());
		}
		else{
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.RED);
			builder.addField("Error", "Please give a user to warn", true);
			Actions.reply(event, builder.build());
		}
		return CommandResult.SUCCESS;
	}
	
	/**
	 * Get the configuration of the role to apply.
	 *
	 * @param guild   The guild of the event.
	 * @param message The message to get the rome from.
	 * @param args    The args that were passed.
	 *
	 * @return The config.
	 */
	@Nonnull
	protected abstract Optional<Role> getRole(@Nonnull Guild guild, @Nonnull Message message, @Nonnull LinkedList<String> args) throws NoValueDefinedException;
	
	/**
	 * Get the configuration of the length for the role to be applied.
	 *
	 * @param guild   The guild of the event.
	 * @param message The message to get the time from.
	 * @param args    The args that were passed.
	 *
	 * @return The config.
	 */
	protected abstract double getTime(@Nonnull Guild guild, @Nonnull Message message, @Nonnull LinkedList<String> args);
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user> [reason]";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Warn a user (by giving a role) for a defined period of time";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
