package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BotSlashCommand;
import fr.raksrinana.rsndiscord.command.base.group.SubCommandsGroupCommand;
import fr.raksrinana.rsndiscord.command.impl.image.UserCommand;
import fr.raksrinana.rsndiscord.command.impl.image.CatCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class ImageGroupCommand extends SubCommandsGroupCommand{
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
