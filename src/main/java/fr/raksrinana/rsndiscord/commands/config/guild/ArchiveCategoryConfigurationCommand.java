package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.CategoryConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;

public class ArchiveCategoryConfigurationCommand extends CategoryConfigurationCommand{
	public ArchiveCategoryConfigurationCommand(final Command parent){
		super(parent);
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
	public String getName(){
		return "Archive category";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("archiveCategory");
	}
}
