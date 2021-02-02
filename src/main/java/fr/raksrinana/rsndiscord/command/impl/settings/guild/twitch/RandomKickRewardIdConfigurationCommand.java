package fr.raksrinana.rsndiscord.command.impl.settings.guild.twitch;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.StringConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class RandomKickRewardIdConfigurationCommand extends StringConfigurationCommand{
	public RandomKickRewardIdConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	protected Optional<String> getConfig(@NotNull Guild guild){
		return Settings.get(guild).getTwitchConfiguration().getRandomKickRewardId();
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("id", "The id of the custom reward", false);
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull String value){
		Settings.get(guild).getTwitchConfiguration().setRandomKickRewardId(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).getTwitchConfiguration().setRandomKickRewardId(null);
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [id]";
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Random kick reward id";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("randomKickRewardId");
	}
}
