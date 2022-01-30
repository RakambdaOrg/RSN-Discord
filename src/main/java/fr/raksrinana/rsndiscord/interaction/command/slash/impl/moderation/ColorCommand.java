package fr.raksrinana.rsndiscord.interaction.command.slash.impl.moderation;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED_NO_MESSAGE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;

@Log4j2
public class ColorCommand extends SubSlashCommand{
	private static final String ROLE_OPTION_ID = "role";
	private static final String TIME_OPTION_ID = "time";
	private final Random random;
	
	public ColorCommand(){
		random = new Random();
	}
	
	@Override
	@NotNull
	public String getId(){
		return "role-color";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Makes the color of a role change periodically";
	}
	
	@Override
	protected @NotNull Collection<? extends OptionData> getOptions(){
		return List.of(
				new OptionData(ROLE, ROLE_OPTION_ID, "The role to rainbowify").setRequired(true),
				new OptionData(INTEGER, TIME_OPTION_ID, "The number of seconds to apply the effect").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var role = event.getOption(ROLE_OPTION_ID).getAsRole();
		var time = getOptionAsInt(event.getOption(TIME_OPTION_ID)).orElseThrow();
		
		colorize(time, role);
		return HANDLED_NO_MESSAGE;
	}
	
	private void colorize(int time, Role role){
		var originalColor = role.getColor();
		
		var executorService = Main.getExecutorService();
		var changeColorTask = executorService.scheduleAtFixedRate(() -> {
			var color = random.nextInt(0xffffff + 1);
			JDAWrappers.setColor(role, color).submit();
		}, 0, 15, TimeUnit.SECONDS);
		
		executorService.schedule(() -> {
			log.info("Stopping color change for {}", role);
			changeColorTask.cancel(false);
			executorService.schedule(() -> {
				JDAWrappers.setColor(role, originalColor).submit();
			}, 60, SECONDS);
		}, time, SECONDS);
	}
}
