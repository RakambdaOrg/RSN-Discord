package fr.raksrinana.rsndiscord.commands.config;

import fr.raksrinana.rsndiscord.commands.config.guild.*;
import fr.raksrinana.rsndiscord.commands.config.guild.reactions.AutoTodoChannelsConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class ConfigurationCommandComposite extends CommandComposite{
	public ConfigurationCommandComposite(){
		this.addSubCommand(new AniListConfigurationCommandComposite(this));
		this.addSubCommand(new AutoRolesConfigurationCommand(this));
		this.addSubCommand(new ModeratorRolesConfigurationCommand(this));
		this.addSubCommand(new AutoThumbsChannelsConfigurationCommand(this));
		this.addSubCommand(new AutoReactionsChannelsConfigurationCommand(this));
		this.addSubCommand(new PrefixConfigurationCommand(this));
		this.addSubCommand(new NicknameConfigurationCommandComposite(this));
		this.addSubCommand(new TwitchConfigurationCommandComposite(this));
		this.addSubCommand(new AnnounceStartChannelConfigurationCommand(this));
		this.addSubCommand(new TraktConfigurationCommandComposite(this));
		this.addSubCommand(new ArchiveCategoryConfigurationCommand(this));
		this.addSubCommand(new AutoTodoChannelsConfigurationCommand(this));
		this.addSubCommand(new ReactionsConfigurationCommandComposite(this));
		this.addSubCommand(new HermitcraftConfigurationCommandComposite(this));
		this.addSubCommand(new ParticipationConfigurationCommandComposite(this));
		this.addSubCommand(new TrombinoscopeConfigurationCommandComposite(this));
		this.addSubCommand(new ExternalTodosConfigurationCommandComposite(this));
		this.addSubCommand(new LocaleConfigurationCommand(this));
		this.addSubCommand(new OnlyMediaChannelsConfigurationCommand(this));
		this.addSubCommand(new GeneralChannelConfigurationCommand(this));
		this.addSubCommand(new RandomKickConfigurationCommandComposite(this));
		this.addSubCommand(new LogChannelConfigurationCommand(this));
		this.addSubCommand(new BirthdayConfigurationCommandComposite(this));
		this.addSubCommand(new EventWinnerRoleConfigurationCommand(this));
		this.addSubCommand(new DiscordIncidentsChannelChannelConfigurationCommand(this));
		this.addSubCommand(new LeaveServerBanDurationConfigurationCommand(this));
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.configuration", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.config.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("config");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.config.description");
	}
}
