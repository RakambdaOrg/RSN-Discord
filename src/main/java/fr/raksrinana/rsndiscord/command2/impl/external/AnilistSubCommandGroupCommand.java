package fr.raksrinana.rsndiscord.command2.impl.external;

import fr.raksrinana.rsndiscord.command2.base.group.SubCommandGroup;
import fr.raksrinana.rsndiscord.command2.impl.external.anilist.InfoCommand;
import fr.raksrinana.rsndiscord.command2.impl.external.anilist.MediaListDifferencesCommand;
import fr.raksrinana.rsndiscord.command2.impl.external.anilist.RegisterCommand;
import org.jetbrains.annotations.NotNull;

public class AnilistSubCommandGroupCommand extends SubCommandGroup{
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
