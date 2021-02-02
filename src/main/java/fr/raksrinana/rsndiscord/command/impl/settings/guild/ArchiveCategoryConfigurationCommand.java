package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.CategoryConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class ArchiveCategoryConfigurationCommand extends CategoryConfigurationCommand{
	public ArchiveCategoryConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull CategoryConfiguration value){
		Settings.get(guild).setArchiveCategory(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).setArchiveCategory(null);
	}
	
	@NotNull
	@Override
	protected Optional<CategoryConfiguration> getConfig(@NotNull Guild guild){
		return Settings.get(guild).getArchiveCategory();
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Archive category";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("archiveCategory");
	}
}
