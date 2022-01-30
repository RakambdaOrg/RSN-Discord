package fr.raksrinana.rsndiscord.interaction.command.slash.impl.external;

import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.anilist.InfoCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.anilist.MediaListDifferencesCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.anilist.RegisterCommand;
import org.jetbrains.annotations.NotNull;

public class AnilistSubCommandGroupCommand extends SubGroupSlashCommand{
	public AnilistSubCommandGroupCommand(){
		addSubcommand(new InfoCommand());
		addSubcommand(new MediaListDifferencesCommand());
		addSubcommand(new RegisterCommand());
	}
	
	@Override
	@NotNull
	public String getId(){
		return "anilist";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Anilist services";
	}
}
