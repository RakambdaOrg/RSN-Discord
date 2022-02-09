package fr.raksrinana.rsndiscord.interaction.command.slash.impl;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.SimpleSlashCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED_NO_MESSAGE;

@BotSlashCommand
public class EventWinnerCommand extends SimpleSlashCommand{
	private static final String WINNER_OPTION_ID = "winner";
	private static final String SECOND_WINNER_OPTION_ID = "winner2";
	
	@Override
	@NotNull
	public String getId(){
		return "event-winner";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Define the new winners of an event";
	}
	
	@Override
	public boolean getDefaultPermission(){
		return false;
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(
				new OptionData(OptionType.USER, WINNER_OPTION_ID, "First winner").setRequired(true),
				new OptionData(OptionType.USER, SECOND_WINNER_OPTION_ID, "Second winner"));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var members = new LinkedList<Member>();
		members.add(event.getOption(WINNER_OPTION_ID).getAsMember());
		Optional.ofNullable(event.getOption(SECOND_WINNER_OPTION_ID)).map(OptionMapping::getAsMember).ifPresent(members::add);
		
		var looseRoleTime = ZonedDateTime.now().plus(1, ChronoUnit.WEEKS);
		var eventConfiguration = Settings.get(guild).getEventConfiguration();
		eventConfiguration.getWinnerRole()
				.flatMap(RoleConfiguration::getRole)
				.ifPresent(winnerRole -> {
					guild.findMembersWithRoles(winnerRole)
							.onSuccess(previousWinners -> previousWinners.stream()
									.filter(m -> !members.contains(m))
									.forEach(m -> JDAWrappers.removeRole(m, winnerRole).submit()));
					
					members.forEach(m -> {
						JDAWrappers.addRole(m, winnerRole).submit();
						eventConfiguration.setLooseRoleTime(m, looseRoleTime);
					});
				});
		return HANDLED_NO_MESSAGE;
	}
}