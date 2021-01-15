package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.CategoryConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class ArchiveCategoryConfigurationCommand extends CategoryConfigurationCommand{
	public ArchiveCategoryConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final CategoryConfiguration value){
		Settings.get(guild).setArchiveCategory(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).setArchiveCategory(null);
	}
	
	@NonNull
	@Override
	protected Optional<CategoryConfiguration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getArchiveCategory();
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Archive category";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("archiveCategory");
	}
}
