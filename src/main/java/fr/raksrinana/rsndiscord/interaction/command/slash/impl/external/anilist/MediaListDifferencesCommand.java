package fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.anilist;

import fr.raksrinana.rsndiscord.api.anilist.data.media.MediaType;
import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED_NO_MESSAGE;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

public class MediaListDifferencesCommand extends SubSlashCommand{
	private static final String TYPE_OPTION_ID = "type";
	private static final String USER_OPTION_ID = "user";
	private static final String SECOND_USER_OPTION_ID = "user2";
	
	@Override
	@NotNull
	public String getId(){
		return "differences";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Show differences between lists of two users";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		var types = Arrays.stream(MediaType.values())
				.map(type -> new Command.Choice(type.getValue(), type.name()))
				.collect(Collectors.toSet());
		
		return List.of(
				new OptionData(STRING, TYPE_OPTION_ID, "Media type").setRequired(true).addChoices(types),
				new OptionData(USER, USER_OPTION_ID, "First user").setRequired(true),
				new OptionData(USER, SECOND_USER_OPTION_ID, "Second user").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var typeStr = event.getOption(TYPE_OPTION_ID).getAsString();
		var member1 = event.getOption(USER_OPTION_ID).getAsMember();
		var member2 = event.getOption(SECOND_USER_OPTION_ID).getAsMember();
		
		var type = MediaType.valueOf(typeStr);
		
		new MediaListDifferencesRunner(event.getJDA(), type, event.getTextChannel(), member1, member2).runQueryOnDefaultUsersChannels(event.getJDA());
		return HANDLED_NO_MESSAGE;
	}
}
