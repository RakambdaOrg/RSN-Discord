package fr.raksrinana.rsndiscord.commands.config;

import fr.raksrinana.rsndiscord.commands.config.guild.*;
import fr.raksrinana.rsndiscord.commands.config.guild.reactions.AutoTodoChannelsConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

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
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Configuration";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("config");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Interactions with the description";
	}
}
