package fr.raksrinana.rsndiscord.command2.impl;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.BotSlashCommand;
import fr.raksrinana.rsndiscord.command2.base.SimpleCommand;
import fr.raksrinana.rsndiscord.command2.impl.configuration.*;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED_NO_MESSAGE;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@BotSlashCommand
@Log4j2
public class ConfigurationCommand extends SimpleCommand{
	private static final String OPERATION_OPTION_ID = "operation";
	private static final String NAME_OPTION_ID = "name";
	private static final String VALUE_OPTION_ID = "value";
	
	private static final String SET_OPERATION_TYPE = "set";
	private static final String RESET_OPERATION_TYPE = "reset";
	private static final String ADD_OPERATION_TYPE = "add";
	private static final String REMOVE_OPERATION_TYPE = "remove";
	private static final String SHOW_OPERATION_TYPE = "show";
	
	private final Map<String, IConfigurationAccessor> accessors;
	
	public ConfigurationCommand(){
		var accessors = new LinkedList<IConfigurationAccessor>();
		accessors.add(new ChannelConfigurationAccessor("announceStartChannel", GuildConfiguration::getAnnounceStartChannel, GuildConfiguration::setAnnounceStartChannel));
		accessors.add(new CategoryConfigurationAccessor("archiveCategory", GuildConfiguration::getArchiveCategory, GuildConfiguration::setArchiveCategory));
		accessors.add(new RoleSetConfigurationAccessor("autoRoles", GuildConfiguration::getAutoRoles));
		accessors.add(new ChannelSetConfigurationAccessor("autoThumbsChannels", GuildConfiguration::getAutoThumbsChannels));
		accessors.add(new ChannelConfigurationAccessor("discordIncidentsChannel", GuildConfiguration::getDiscordIncidentsChannel, GuildConfiguration::setDiscordIncidentsChannel));
		accessors.add(new RoleConfigurationAccessor("eventWinnerRole", s -> s.getEventConfiguration().getWinnerRole(), (s, v) -> s.getEventConfiguration().setWinnerRole(v)));
		accessors.add(new ChannelConfigurationAccessor("generalChannel", GuildConfiguration::getGeneralChannel, GuildConfiguration::setGeneralChannel));
		accessors.add(new DurationConfigurationAccessor("leaveServerBanDuration", GuildConfiguration::getLeaveServerBanDuration, GuildConfiguration::setLeaveServerBanDuration));
		accessors.add(new LocaleConfigurationAccessor("locale", GuildConfiguration::getLocale, GuildConfiguration::setLocale));
		accessors.add(new ChannelConfigurationAccessor("logChannel", GuildConfiguration::getLogChannel, GuildConfiguration::setLogChannel));
		accessors.add(new RoleSetConfigurationAccessor("moderatorRoles", GuildConfiguration::getModeratorRoles));
		
		this.accessors = accessors.stream().collect(Collectors.toMap(IConfigurationAccessor::getName, Function.identity()));
	}
	
	@Override
	@NotNull
	public String getId(){
		return "configuration";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Change the bot's configuration";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(STRING, OPERATION_OPTION_ID, "The operation to perform")
						.addChoice("set", SET_OPERATION_TYPE)
						.addChoice("reset", RESET_OPERATION_TYPE)
						.addChoice("add", ADD_OPERATION_TYPE)
						.addChoice("remove", REMOVE_OPERATION_TYPE)
						.addChoice("show", SHOW_OPERATION_TYPE)
						.setRequired(true),
				new OptionData(STRING, NAME_OPTION_ID, "Name of the configuration to change").setRequired(true),
				new OptionData(STRING, VALUE_OPTION_ID, "Value to set").setRequired(false));
	}
	
	@Override
	public boolean replyEphemeral(){
		return true;
	}
	
	@Override
	public boolean getDefaultPermission(){
		return false;
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var accessor = accessors.get(event.getOption(NAME_OPTION_ID).getAsString());
		if(Objects.isNull(accessor)){
			return CommandResult.BAD_ARGUMENTS;
		}
		
		var operation = event.getOption(OPERATION_OPTION_ID).getAsString();
		var configuration = Settings.get(event.getGuild());
		
		try{
			return ConfigurationOperation.fromValue(operation)
					.map(o -> switch(o){
						case SET -> handleSetOperation(event, accessor, configuration);
						case RESET -> handleResetOperation(event, accessor, configuration);
						case ADD -> handleAddOperation(event, accessor, configuration);
						case REMOVE -> handleRemoveOperation(event, accessor, configuration);
						case SHOW -> handleShowOperation(event, accessor, configuration);
					})
					.orElseGet(() -> handleUnknownOperation(event));
		}
		catch(UnsupportedOperationException e){
			JDAWrappers.reply(event, "Unsupported operation " + operation).submit();
			return HANDLED;
		}
	}
	
	private CommandResult handleSetOperation(SlashCommandEvent event, IConfigurationAccessor accessor, GuildConfiguration configuration){
		if(accessor.set(configuration, event.getOption(VALUE_OPTION_ID).getAsString())){
			JDAWrappers.reply(event, "Value modified").submit();
		}
		else{
			JDAWrappers.reply(event, "Failed to set value").submit();
		}
		return HANDLED;
	}
	
	private CommandResult handleResetOperation(SlashCommandEvent event, IConfigurationAccessor accessor, GuildConfiguration configuration){
		if(accessor.reset(configuration)){
			JDAWrappers.reply(event, "Value reset").submit();
		}
		else{
			JDAWrappers.reply(event, "Failed to reset value").submit();
		}
		return HANDLED;
	}
	
	private CommandResult handleAddOperation(SlashCommandEvent event, IConfigurationAccessor accessor, GuildConfiguration configuration){
		return HANDLED_NO_MESSAGE;
	}
	
	private CommandResult handleRemoveOperation(SlashCommandEvent event, IConfigurationAccessor accessor, GuildConfiguration configuration){
		return HANDLED_NO_MESSAGE;
	}
	
	private CommandResult handleShowOperation(SlashCommandEvent event, IConfigurationAccessor accessor, GuildConfiguration configuration){
		accessor.show(configuration)
				.map(embed -> JDAWrappers.reply(event, embed).submit())
				.orElseGet(() -> JDAWrappers.reply(event, "Failed to get value").submit());
		return HANDLED;
	}
	
	private CommandResult handleUnknownOperation(SlashCommandEvent event){
		JDAWrappers.reply(event, "Unknown operation type").submit();
		return HANDLED;
	}
}
