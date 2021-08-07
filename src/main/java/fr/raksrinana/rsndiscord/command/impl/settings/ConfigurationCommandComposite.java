package fr.raksrinana.rsndiscord.command.impl.settings;

import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.*;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.reactions.AutoTodoChannelsConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class ConfigurationCommandComposite extends CommandComposite{
	public ConfigurationCommandComposite(){
		addSubCommand(new AniListConfigurationCommandComposite(this));
		addSubCommand(new NicknameConfigurationCommandComposite(this));
		addSubCommand(new TwitchConfigurationCommandComposite(this));
		addSubCommand(new TraktConfigurationCommandComposite(this));
		addSubCommand(new AutoTodoChannelsConfigurationCommand(this));
		addSubCommand(new ReactionsConfigurationCommandComposite(this));
		addSubCommand(new HermitcraftConfigurationCommandComposite(this));
		addSubCommand(new RainbowSixConfigurationCommandComposite(this));
		addSubCommand(new ExternalTodosConfigurationCommandComposite(this));
		addSubCommand(new RandomKickConfigurationCommandComposite(this));
		addSubCommand(new BirthdayConfigurationCommandComposite(this));
		addSubCommand(new TwitterConfigurationCommandComposite(this));
		addSubCommand(new JoinLeaveConfigurationCommandComposite(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.configuration", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.config.name");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("config");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.config.description");
	}
}
