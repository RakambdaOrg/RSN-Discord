package fr.raksrinana.rsndiscord.command2.impl;

import fr.raksrinana.rsndiscord.command2.BotSlashCommand;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommandsGroupCommand;
import fr.raksrinana.rsndiscord.command2.impl.bot.InfoCommand;
import fr.raksrinana.rsndiscord.command2.impl.bot.SayCommand;
import fr.raksrinana.rsndiscord.command2.impl.bot.StopCommand;
import fr.raksrinana.rsndiscord.command2.impl.bot.TimeCommand;
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
	public @NotNull String getId(){
		return "bot";
	}
	
	@Override
	public @NotNull String getShortDescription(){
		return "Infos about the bot";
	}
}
