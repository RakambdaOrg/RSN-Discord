package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BotSlashCommand;
import fr.raksrinana.rsndiscord.command.base.group.SubCommandsGroupCommand;
import fr.raksrinana.rsndiscord.command.impl.moderation.*;
import fr.raksrinana.rsndiscord.command.impl.moderation.NicknameCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class ModerationGroupCommand extends SubCommandsGroupCommand{
	public ModerationGroupCommand(){
		addSubcommand(new ClearCommand());
		addSubcommand(new ColorCommand());
		addSubcommand(new ListJoinsCommand());
		addSubcommand(new NicknameCommand());
		addSubcommand(new RandomKickCommand());
		addSubcommand(new RemoveAllRoleCommand());
		addSubcommand(new SoftBanCommand());
	}
	
	@Override
	public @NotNull String getId(){
		return "mod";
	}
	
	@Override
	public @NotNull String getShortDescription(){
		return "Moderation commands";
	}
	
	@Override
	public boolean getDefaultPermission(){
		return false;
	}
}
