package fr.raksrinana.rsndiscord.interaction.command.slash.impl;

import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubCommandsGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.bot.InfoCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.bot.SayCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.bot.StopCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.bot.TimeCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class BotGroupCommand extends SubCommandsGroupSlashCommand{
	public BotGroupCommand(){
		addSubcommand(new InfoCommand());
		addSubcommand(new TimeCommand());
		addSubcommand(new StopCommand());
		addSubcommand(new SayCommand());
	}
	
	@Override
	@NotNull
	public String getId(){
		return "bot";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Infos about the bot";
	}
}
