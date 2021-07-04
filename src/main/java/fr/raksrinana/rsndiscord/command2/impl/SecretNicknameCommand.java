package fr.raksrinana.rsndiscord.command2.impl;

import fr.raksrinana.rsndiscord.button.impl.selectionmenu.SecretNicknameMenuHandler;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.BotSlashCommand;
import fr.raksrinana.rsndiscord.command2.base.SimpleCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.schedule.ScheduleService.deleteMessageMins;

@BotSlashCommand
public class SecretNicknameCommand extends SimpleCommand{
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		JDAWrappers.modifyNickname(event.getMember(), "Please ping me :cat:").submit()
				.thenAccept(empty -> Settings.get(event.getGuild()).getNicknameConfiguration().setLastChange(event.getUser(), ZonedDateTime.now()));
		
		JDAWrappers.reply(event, "Choose your new nickname:")
				.addActionRow(new SecretNicknameMenuHandler().asComponent())
				.submit()
				.thenApply(deleteMessageMins(5));
		return HANDLED;
	}
	
	@Override
	public boolean replyEphemeral(){
		return true;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "secretnick";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Choose a secret nickname";
	}
	
	@Override
	public boolean isGuildOnly(){
		return false;
	}
}
