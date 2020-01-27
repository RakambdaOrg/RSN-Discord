package fr.raksrinana.rsndiscord.commands.config;

import fr.raksrinana.rsndiscord.commands.config.guild.*;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

@BotCommand
public class ConfigurationCommandComposite extends CommandComposite{
	public ConfigurationCommandComposite(){
		this.addSubCommand(new AniListConfigurationCommandComposite(this));
		this.addSubCommand(new AutoRolesConfigurationCommand(this));
		this.addSubCommand(new DjRoleConfigurationCommand(this));
		this.addSubCommand(new WarnsConfigurationCommandComposite(this));
		this.addSubCommand(new ModeratorRolesConfigurationCommand(this));
		this.addSubCommand(new IdeaChannelsConfigurationCommand(this));
		this.addSubCommand(new NoXpChannelsConfigurationCommand(this));
		this.addSubCommand(new PrefixConfigurationCommand(this));
		this.addSubCommand(new ParticipationConfigurationCommandComposite(this));
		this.addSubCommand(new ReportChannelConfigurationCommand(this));
		this.addSubCommand(new NicknameConfigurationCommandComposite(this));
		this.addSubCommand(new TwitchConfigurationCommandComposite(this));
		this.addSubCommand(new QuestionsConfigurationCommandComposite(this));
		this.addSubCommand(new TrombinoscopeConfigurationCommandComposite(this));
		this.addSubCommand(new QuizChannelConfigurationCommand(this));
		this.addSubCommand(new LeaverRoleConfigurationCommand(this));
		this.addSubCommand(new IrcForwardConfigurationCommand(this));
		this.addSubCommand(new OverwatchConfigurationCommandComposite(this));
		this.addSubCommand(new PoopRoleConfigurationCommand(this));
		this.addSubCommand(new AnnounceStartChannelConfigurationCommand(this));
		this.addSubCommand(new TraktConfigurationCommandComposite(this));
		this.addSubCommand(new ChristmasRoleConfigurationCommand(this));
		this.addSubCommand(new NewYearRoleConfigurationCommand(this));
		this.addSubCommand(new ArchiveCategoryConfigurationCommand(this));
		this.addSubCommand(new AutoTodoChannelsConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
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
