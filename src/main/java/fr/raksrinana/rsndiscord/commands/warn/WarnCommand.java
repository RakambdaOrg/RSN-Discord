package fr.raksrinana.rsndiscord.commands.warn;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.schedule.RemoveRoleScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.schedule.RemoveRoleScheduleHandler;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

public abstract class WarnCommand extends BasicCommand{
	static final long DEFAULT_TIME = 86400L;
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "L'The user to warn", false);
		builder.addField("Reason", "Reason of the warn", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(!event.getMessage().getMentionedUsers().isEmpty()){
			final var user = event.getMessage().getMentionedUsers().get(0);
			args.poll();
			final var roleOptional = this.getRole(event.getGuild(), event.getMessage(), args);
			final var duration = this.getTime(event.getGuild(), event.getMessage(), args);
			final var reason = String.join(" ", args);
			final var builder = new EmbedBuilder();
			builder.setAuthor(user.getName(), null, user.getAvatarUrl());
			roleOptional.ifPresentOrElse(role -> {
				Actions.giveRole(event.getMember(), role);
				final var date = LocalDateTime.now();
				Settings.get(event.getGuild()).getSchedules().stream().filter(configuration -> Objects.equals(configuration.getTag(), ScheduleTag.REMOVE_ROLE) && Objects.equals(user.getIdLong(), configuration.getUser().getUserId())).filter(configuration -> Objects.equals(configuration.getData().get(RemoveRoleScheduleHandler.ROLE_ID_KEY), role.getId())).findFirst().ifPresentOrElse(c -> {
					if(date.isAfter(c.getScheduleDate())){
						c.setScheduleDate(date);
					}
				}, () -> ScheduleUtils.addSchedule(new RemoveRoleScheduleConfiguration(user, event.getChannel(), date, role), event.getGuild()));
				builder.setColor(Color.GREEN);
				builder.addField("Congratulations", user.getAsMention() + " joined the role " + role.getAsMention() + " for " + duration + " seconds(s)", false);
				builder.addField("", "To know how your warn is doing, user the magic command: g?warninfo " + user.getAsMention(), false);
				Log.getLogger(event.getGuild()).info("{} warned {} for {} days with role {}", event.getAuthor(), user, duration, role);
				if(!reason.isEmpty()){
					Actions.replyPrivate(event.getGuild(), user, MessageFormat.format("Warn reason: {0}", reason), null);
				}
			}, () -> {
				builder.setColor(Color.RED);
				builder.addField("Error", "Please configure a role to give", true);
			});
			Actions.reply(event, "", builder.build());
		}
		else{
			return CommandResult.BAD_ARGUMENTS;
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
	@NonNull
	protected abstract Optional<Role> getRole(@NonNull Guild guild, @NonNull Message message, @NonNull LinkedList<String> args);
	
	/**
	 * Get the configuration of the length for the role to be applied.
	 *
	 * @param guild   The guild of the event.
	 * @param message The message to get the time from.
	 * @param args    The args that were passed.
	 *
	 * @return The config.
	 */
	protected abstract long getTime(@NonNull Guild guild, @NonNull Message message, @NonNull LinkedList<String> args);
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user> [reason]";
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Warn a user (by giving a role) for a defined period of time";
	}
}
