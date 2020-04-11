package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.schedule.RemoveRoleScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

@BotCommand
public class PoopCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Role", "The role to poop on", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(!event.getMessage().getMentionedRoles().isEmpty()){
			final var removeDate = ZonedDateTime.now().plusMinutes(10);
			final var poopRoleOptional = Settings.get(event.getGuild()).getPoopRole().flatMap(RoleConfiguration::getRole);
			event.getMessage().getMentionedRoles().stream().findFirst().ifPresent(r -> event.getGuild().getMembersWithRoles(r).forEach(member -> {
				Actions.reply(event, MessageFormat.format("{0} you poop", member.getAsMention()), null);
				poopRoleOptional.ifPresent(role -> {
					Actions.giveRole(member, role);
					ScheduleUtils.addSchedule(new RemoveRoleScheduleConfiguration(event.getAuthor(), event.getChannel(), removeDate, role), event.getGuild());
				});
			}));
		}
		else{
			Actions.reply(event, "Please mention a role", null);
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <role>";
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Poop";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("poop");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Poops on people";
	}
}
