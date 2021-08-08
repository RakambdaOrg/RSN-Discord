package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BotSlashCommand;
import fr.raksrinana.rsndiscord.command.base.group.SubCommandsGroupCommand;
import fr.raksrinana.rsndiscord.command.impl.bot.InfoCommand;
import fr.raksrinana.rsndiscord.command.impl.bot.SayCommand;
import fr.raksrinana.rsndiscord.command.impl.bot.StopCommand;
import fr.raksrinana.rsndiscord.command.impl.bot.TimeCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class BotGroupCommand extends SubCommandsGroupCommand{
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
