package fr.mrcraftcod.gunterdiscord.commands.config;

import fr.mrcraftcod.gunterdiscord.commands.config.guild.*;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import java.util.List;

public class ConfigurationCommandComposite extends CommandComposite{
	public ConfigurationCommandComposite(){
		addSubCommand(new AniListConfigurationCommandComposite(this));
		addSubCommand(new AutoRolesConfigurationCommand(this));
		addSubCommand(new DjRoleConfigurationCommand(this));
		addSubCommand(new WarnsConfigurationCommandComposite(this));
		addSubCommand(new ModeratorRolesConfigurationCommand(this));
		addSubCommand(new IdeaChannelsConfigurationCommand(this));
		addSubCommand(new NoXpChannelsConfigurationCommand(this));
		addSubCommand(new PrefixConfigurationCommand(this));
		addSubCommand(new ParticipationConfigurationCommandComposite(this));
		addSubCommand(new ReportChannelConfigurationCommand(this));
		addSubCommand(new NicknameConfigurationCommandComposite(this));
		addSubCommand(new TwitchChannelConfigurationCommand(this));
		addSubCommand(new QuestionsConfigurationCommandComposite(this));
		addSubCommand(new TrombinoscopeConfigurationCommandComposite(this));
		addSubCommand(new QuizChannelConfigurationCommand(this));
		addSubCommand(new LeaverRoleConfigurationCommand(this));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Configuration";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("config");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Interactions with the description";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
