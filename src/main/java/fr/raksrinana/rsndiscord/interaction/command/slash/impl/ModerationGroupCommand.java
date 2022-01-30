package fr.raksrinana.rsndiscord.interaction.command.slash.impl;

import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubCommandsGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.moderation.*;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.moderation.NicknameCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class ModerationGroupCommand extends SubCommandsGroupSlashCommand{
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
