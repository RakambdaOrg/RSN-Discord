package fr.raksrinana.rsndiscord.command2.impl;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.BotSlashCommand;
import fr.raksrinana.rsndiscord.command2.base.SimpleCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
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
						.addChoice("show", SHOW_OPERATION_TYPE),
				new OptionData(STRING, NAME_OPTION_ID, "Name of the configuration to change"),
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
		return switch(event.getOption(OPERATION_OPTION_ID).getAsString()){
			case SET_OPERATION_TYPE -> handleSetOperation(event);
			case RESET_OPERATION_TYPE -> handleResetOperation(event);
			case ADD_OPERATION_TYPE -> handleAddOperation(event);
			case REMOVE_OPERATION_TYPE -> handleRemoveOperation(event);
			case SHOW_OPERATION_TYPE -> handleShowOperation(event);
			default -> handleUnknownOperation(event);
		};
	}
	
	private CommandResult handleSetOperation(SlashCommandEvent event){
		return HANDLED_NO_MESSAGE;
	}
	
	private CommandResult handleResetOperation(SlashCommandEvent event){
		return HANDLED_NO_MESSAGE;
	}
	
	private CommandResult handleAddOperation(SlashCommandEvent event){
		return HANDLED_NO_MESSAGE;
	}
	
	private CommandResult handleRemoveOperation(SlashCommandEvent event){
		return HANDLED_NO_MESSAGE;
	}
	
	private CommandResult handleShowOperation(SlashCommandEvent event){
		return HANDLED_NO_MESSAGE;
	}
	
	private CommandResult handleUnknownOperation(SlashCommandEvent event){
		JDAWrappers.reply(event, "Unknown operation type").submit();
		return HANDLED;
	}
}
