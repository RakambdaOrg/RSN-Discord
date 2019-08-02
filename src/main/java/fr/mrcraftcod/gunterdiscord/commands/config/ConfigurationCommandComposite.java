package fr.mrcraftcod.gunterdiscord.commands.config;

import fr.mrcraftcod.gunterdiscord.commands.config.guild.*;
import fr.mrcraftcod.gunterdiscord.commands.generic.BotCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
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
		this.addSubCommand(new TwitchChannelConfigurationCommand(this));
		this.addSubCommand(new QuestionsConfigurationCommandComposite(this));
		this.addSubCommand(new TrombinoscopeConfigurationCommandComposite(this));
		this.addSubCommand(new QuizChannelConfigurationCommand(this));
		this.addSubCommand(new LeaverRoleConfigurationCommand(this));
		this.addSubCommand(new IrcForwardConfigurationCommand(this));
		this.addSubCommand(new OverwatchConfigurationCommandComposite(this));
		this.addSubCommand(new PoopRoleConfigurationCommand(this));
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
