package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.schedule.RemoveRoleScheduleHandler;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleTag;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@BotCommand
public class WarnInfoCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", "The user to get the infos for (default: @me)", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var target = event.getMessage().getMentionedUsers().stream().findFirst().orElse(event.getAuthor());
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, "Warns info", null);
		builder.addField("User", target.getAsMention(), false);
		final var bans = Settings.get(event.getGuild()).getSchedules().stream().filter(scheduleConfiguration -> Objects.equals(scheduleConfiguration.getTag(), ScheduleTag.REMOVE_ROLE) && Objects.equals(target.getIdLong(), scheduleConfiguration.getUser().getUserId())).collect(Collectors.toSet());
		if(bans.isEmpty()){
			builder.setColor(Color.GREEN);
			builder.setDescription("The user have no warns");
		}
		else{
			final var formatter = new SimpleDateFormat("dd MMM at HH:mm:ssZ");
			builder.setDescription("Warns will be removed with a maximum delay of 15 minutes");
			bans.forEach(ban -> builder.addField("Role " + RemoveRoleScheduleHandler.getRole(ban).map(Role::getAsMention).orElse("<<UNKNOWN>>"), "Ends the " + formatter.format(ban.getScheduleDate()), false));
		}
		Actions.reply(event, "", builder.build());
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user]";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Warn info";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("warninfo", "wi");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Gets information about the warns in progress";
	}
}
