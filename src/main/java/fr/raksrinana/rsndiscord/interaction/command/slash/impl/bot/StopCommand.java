package fr.raksrinana.rsndiscord.interaction.command.slash.impl.bot;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.permission.IPermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.interaction.command.permission.CreatorPermission.CREATOR_PERMISSION;

@Log4j2
public class StopCommand extends SubSlashCommand{
	@Override
	@NotNull
	public String getId(){
		return "stop";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Stops the bot";
	}
	
	@Override
	@NotNull
	public IPermission getPermission(){
		return CREATOR_PERMISSION;
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		return execute(event);
	}
	
	@Override
	@NotNull
	public CommandResult executeUser(@NotNull SlashCommandInteractionEvent event){
		return execute(event);
	}
	
	@NotNull
	private CommandResult execute(@NotNull SlashCommandInteractionEvent event){
		log.info("BOT STOPPING");
		JDAWrappers.edit(event, "Stopping").submit();
		Main.close();
		return HANDLED;
	}
}
