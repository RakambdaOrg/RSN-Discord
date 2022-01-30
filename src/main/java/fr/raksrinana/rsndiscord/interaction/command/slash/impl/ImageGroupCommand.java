package fr.raksrinana.rsndiscord.interaction.command.slash.impl;

import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubCommandsGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.image.UserCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.image.CatCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class ImageGroupCommand extends SubCommandsGroupSlashCommand{
	public ImageGroupCommand(){
		addSubcommand(new UserCommand());
		addSubcommand(new CatCommand());
	}
	
	@Override
	public @NotNull String getId(){
		return "image";
	}
	
	@Override
	public @NotNull String getShortDescription(){
		return "Get different kind of images";
	}
}
