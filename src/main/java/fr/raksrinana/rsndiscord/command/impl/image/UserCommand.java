package fr.raksrinana.rsndiscord.command.impl.image;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Set;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static java.awt.Color.GREEN;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

public class UserCommand extends SubCommand{
	public static final String USER_PARAMETER_ID = "user";
	
	@Override
	@NotNull
	public String getId(){
		return "user";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Get the avatar of a user";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return Set.of(new OptionData(USER, USER_PARAMETER_ID, "The target user from whom to get the avatar").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandEvent event, @NotNull Guild guild, @NotNull Member member){
		return execute(event);
	}
	
	@Override
	@NotNull
	public CommandResult executeUser(@NotNull SlashCommandEvent event){
		return execute(event);
	}
	
	@NotNull
	private CommandResult execute(@NotNull SlashCommandEvent event){
		var target = event.getOption(USER_PARAMETER_ID).getAsUser();
		
		var author = event.getUser();
		
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(GREEN)
				.setTitle("Link", target.getAvatarUrl())
				.setImage(target.getAvatarUrl())
				.build();
		
		JDAWrappers.edit(event, embed).submit();
		return HANDLED;
	}
}
