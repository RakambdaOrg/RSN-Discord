package fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.twitter;

import fr.raksrinana.rsndiscord.api.twitter.TwitterApi;
import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.permission.IPermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.interaction.command.permission.CreatorPermission.CREATOR_PERMISSION;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class BlockCommand extends SubSlashCommand{
	private static final String USER_OPTION_ID = "user";
	
	@Override
	@NotNull
	public String getId(){
		return "block";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Block a user on twitter";
	}
	
	@Override
	@NotNull
	public IPermission getPermission(){
		return CREATOR_PERMISSION;
	}
	
	@Override
	protected @NotNull Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(STRING, USER_OPTION_ID, "Username").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var username = event.getOption(USER_OPTION_ID).getAsString();
		var result = TwitterApi.blockUser(username);
		JDAWrappers.edit(event, "%s: %s".formatted(username, Boolean.toString(result.getData().isBlocking()))).submitAndDelete(1);
		return HANDLED;
	}
}
