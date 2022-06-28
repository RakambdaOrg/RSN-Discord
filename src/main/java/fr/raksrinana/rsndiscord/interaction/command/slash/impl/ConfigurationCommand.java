package fr.raksrinana.rsndiscord.interaction.command.slash.impl;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.SimpleSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.IConfigurationAccessor;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.set.ChannelSetConfigurationAccessor;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.set.RoleSetConfigurationAccessor;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.set.StringSetConfigurationAccessor;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.set.URLSetConfigurationAccessor;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.value.CategoryConfigurationAccessor;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.value.ChannelConfigurationAccessor;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.value.DoubleConfigurationAccessor;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.value.LocaleConfigurationAccessor;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.value.RoleConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@BotSlashCommand
@Log4j2
public class ConfigurationCommand extends SimpleSlashCommand{
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
		accessors.add(new ChannelSetConfigurationAccessor("autoThumbsChannels", GuildConfiguration::getAutoThumbsChannels));
		accessors.add(new LocaleConfigurationAccessor("locale", GuildConfiguration::getLocale, GuildConfiguration::setLocale));
		accessors.add(new RoleSetConfigurationAccessor("moderatorRoles", GuildConfiguration::getModeratorRoles));
		
		accessors.add(new ChannelConfigurationAccessor("anilist.mediaChangeChannel", s -> s.getAniListConfiguration().getMediaChangeChannel(), (s, v) -> s.getAniListConfiguration().setMediaChangeChannel(v)));
		accessors.add(new ChannelConfigurationAccessor("anilist.notificationChannel", s -> s.getAniListConfiguration().getNotificationsChannel(), (s, v) -> s.getAniListConfiguration().setNotificationsChannel(v)));
		accessors.add(new ChannelConfigurationAccessor("birthday.notificationChannel", s -> s.getBirthdays().getNotificationChannel(), (s, v) -> s.getBirthdays().setNotificationChannel(v)));
		
		accessors.add(new ChannelConfigurationAccessor("hermitcraft.streamChannel", s -> s.getHermitcraftConfiguration().getStreamingNotificationChannel(), (s, v) -> s.getHermitcraftConfiguration().setStreamingNotificationChannel(v)));
		accessors.add(new ChannelConfigurationAccessor("hermitcraft.videoChannel", s -> s.getHermitcraftConfiguration().getVideoNotificationChannel(), (s, v) -> s.getHermitcraftConfiguration().setVideoNotificationChannel(v)));
		
		accessors.add(new ChannelConfigurationAccessor("joinleave.channel", s -> s.getJoinLeaveConfiguration().getChannel(), (s, v) -> s.getJoinLeaveConfiguration().setChannel(v)));
		accessors.add(new StringSetConfigurationAccessor("joinleave.joinImages", s -> s.getJoinLeaveConfiguration().getJoinImages()));
		accessors.add(new StringSetConfigurationAccessor("joinleave.leaveImages", s -> s.getJoinLeaveConfiguration().getLeaveImages()));
		
		accessors.add(new RoleSetConfigurationAccessor("randomkick.kickableRoles", s -> s.getRandomKick().getKickableRoles()));
		accessors.add(new RoleConfigurationAccessor("randomkick.kickedRole", s -> s.getRandomKick().getKickedRole(), (s, v) -> s.getRandomKick().setKickedRole(v)));
		accessors.add(new DoubleConfigurationAccessor("randomkick.kickRoleProbability", s -> s.getRandomKick().getKickRoleProbability(), (s, v) -> s.getRandomKick().setKickRoleProbability(v)));
		accessors.add(new RoleSetConfigurationAccessor("randomkick.randomKickRolesPing", s -> s.getRandomKick().getRandomKickRolesPing()));
		
		accessors.add(new ChannelSetConfigurationAccessor("reactions.autoTodoChannels", s -> s.getReactionsConfiguration().getAutoTodoChannels()));
		
		accessors.add(new ChannelConfigurationAccessor("trakt.mediaChangeChannel", s -> s.getTraktConfiguration().getMediaChangeChannel(), (s, v) -> s.getTraktConfiguration().setMediaChangeChannel(v)));
		
		accessors.add(new ChannelConfigurationAccessor("twitter.searchChannel", s -> s.getTwitterConfiguration().getSearchChannel(), (s, v) -> s.getTwitterConfiguration().setSearchChannel(v)));
		accessors.add(new StringSetConfigurationAccessor("twitter.searches", s -> s.getTwitterConfiguration().getSearches()));
		accessors.add(new ChannelConfigurationAccessor("twitter.usersChannel", s -> s.getTwitterConfiguration().getUsersChannel(), (s, v) -> s.getTwitterConfiguration().setUsersChannel(v)));
		accessors.add(new StringSetConfigurationAccessor("twitter.users", s -> s.getTwitterConfiguration().getUserIds()));
		
		accessors.add(new ChannelConfigurationAccessor("rss.channel", s -> s.getRss().getChannel(), (s, v) -> s.getRss().setChannel(v)));
		accessors.add(new URLSetConfigurationAccessor("rss.feed", s -> s.getRss().getFeeds()));
		
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
				new OptionData(STRING, NAME_OPTION_ID, "Name of the configuration to change").setRequired(true).setAutoComplete(true),
				new OptionData(STRING, VALUE_OPTION_ID, "Value to set").setRequired(false));
	}
	
	@Override
	public boolean replyEphemeral(){
		return true;
	}
	
	@Override
	public DefaultMemberPermissions getDefaultPermission(){
		return DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR);
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var accessor = accessors.get(event.getOption(NAME_OPTION_ID).getAsString());
		if(Objects.isNull(accessor)){
			JDAWrappers.reply(event, "Unknown configuration. Available: " + accessors.keySet().stream().sorted().collect(Collectors.joining("\n"))).submit();
			return HANDLED;
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
		catch(RuntimeException e){
			log.error("Failed to apply configuration change", e);
			return BAD_ARGUMENTS;
		}
	}
	
	@NotNull
	private CommandResult handleSetOperation(@NotNull SlashCommandInteraction event, @NotNull IConfigurationAccessor accessor, @NotNull GuildConfiguration configuration){
		if(accessor.set(configuration, event.getOption(VALUE_OPTION_ID).getAsString())){
			JDAWrappers.reply(event, "Value modified").submit();
		}
		else{
			JDAWrappers.reply(event, "Failed to set value").submit();
		}
		return HANDLED;
	}
	
	@NotNull
	private CommandResult handleResetOperation(@NotNull SlashCommandInteraction event, @NotNull IConfigurationAccessor accessor, @NotNull GuildConfiguration configuration){
		if(accessor.reset(configuration)){
			JDAWrappers.reply(event, "Value reset").submit();
		}
		else{
			JDAWrappers.reply(event, "Failed to reset value").submit();
		}
		return HANDLED;
	}
	
	@NotNull
	private CommandResult handleAddOperation(@NotNull SlashCommandInteraction event, @NotNull IConfigurationAccessor accessor, @NotNull GuildConfiguration configuration){
		if(accessor.add(configuration, event.getOption(VALUE_OPTION_ID).getAsString())){
			JDAWrappers.reply(event, "Value added").submit();
		}
		else{
			JDAWrappers.reply(event, "Failed to add value").submit();
		}
		return HANDLED;
	}
	
	@NotNull
	private CommandResult handleRemoveOperation(@NotNull SlashCommandInteraction event, @NotNull IConfigurationAccessor accessor, @NotNull GuildConfiguration configuration){
		if(accessor.remove(configuration, event.getOption(VALUE_OPTION_ID).getAsString())){
			JDAWrappers.reply(event, "Value removed").submit();
		}
		else{
			JDAWrappers.reply(event, "Failed to remove value").submit();
		}
		return HANDLED;
	}
	
	@NotNull
	private CommandResult handleShowOperation(@NotNull SlashCommandInteraction event, @NotNull IConfigurationAccessor accessor, @NotNull GuildConfiguration configuration){
		accessor.show(configuration)
				.map(embed -> JDAWrappers.reply(event, embed).submit())
				.orElseGet(() -> JDAWrappers.reply(event, "Failed to get value").submit());
		return HANDLED;
	}
	
	@NotNull
	private CommandResult handleUnknownOperation(@NotNull SlashCommandInteraction event){
		JDAWrappers.reply(event, "Unknown operation type").submit();
		return HANDLED;
	}
	
	@Override
	public void autoCompleteGuild(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		if(Objects.equals(NAME_OPTION_ID, event.getFocusedOption().getName())){
			autoCompleteName(event);
		}
	}
	
	private void autoCompleteName(@NotNull CommandAutoCompleteInteractionEvent event){
		var value = event.getFocusedOption().getValue();
		var nameChoices = value.isBlank() ? getTopLevelChoices() : getStartingWithChoices(value);
		var choices = nameChoices.limit(OptionData.MAX_CHOICES)
				.sorted()
				.map(name -> new Command.Choice(name, name))
				.toList();
		JDAWrappers.reply(event, choices).submit();
	}
	
	@NotNull
	private Stream<String> getTopLevelChoices(){
		return accessors.values().stream()
				.map(IConfigurationAccessor::getTopLevel)
				.distinct();
	}
	
	@NotNull
	private Stream<String> getStartingWithChoices(@NotNull String value){
		return accessors.values().stream()
				.map(IConfigurationAccessor::getName)
				.filter(name -> name.startsWith(value));
	}
}
