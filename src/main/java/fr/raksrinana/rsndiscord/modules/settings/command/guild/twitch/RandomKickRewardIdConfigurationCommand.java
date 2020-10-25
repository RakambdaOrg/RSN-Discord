package fr.raksrinana.rsndiscord.modules.settings.command.guild.twitch;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.command.helpers.StringConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class RandomKickRewardIdConfigurationCommand extends StringConfigurationCommand{
	public RandomKickRewardIdConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	protected Optional<String> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getTwitchConfiguration().getRandomKickRewardId();
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("id", "The id of the custom reward", false);
	}
	
	@Override
	protected void setConfig(@NonNull Guild guild, @NonNull String value){
		Settings.get(guild).getTwitchConfiguration().setRandomKickRewardId(value);
	}
	
	@Override
	protected void removeConfig(@NonNull Guild guild){
		Settings.get(guild).getTwitchConfiguration().setRandomKickRewardId(null);
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [id]";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Random kick reward id";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("randomKickRewardId");
	}
}
