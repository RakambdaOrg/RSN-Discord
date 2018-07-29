package fr.mrcraftcod.gunterdiscord.commands.config;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-27
 */
public class ConfigurationCommand extends BasicCommand{
	private final ChangeConfigType type;
	private final List<String> commands;
	
	/**
	 * Actions available.
	 */
	public enum ChangeConfigType{
		ADD, REMOVE, SET, SHOW, ERROR
	}
	
	/**
	 * The result of a setting action.
	 */
	public enum ActionResult{
		OK, NONE, ERROR
	}
	
	/**
	 * Constructor.
	 *
	 * @param parent   The parent command.
	 * @param type     The operation that will be done on the configuration.
	 * @param commands The commands to call this command.
	 */
	ConfigurationCommand(Command parent, ChangeConfigType type, List<String> commands){
		super(parent);
		this.type = type;
		this.commands = commands;
	}
	
	@Override
	public CommandResult execute(@NotNull MessageReceivedEvent event, @NotNull LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.size() > 0){
			Configuration configuration = Settings.getSettings(args.pop());
			if(configuration != null){
				List<String> beforeArgs = new LinkedList<>(args);
				ActionResult result = processWithValue(event, configuration, args);
				if(result == ActionResult.ERROR){
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.RED);
					builder.setTitle("Erreur durant l'opération");
					builder.setDescription("Commande: " + getName());
					builder.addField("Raison", "C'est compliqué", false);
					builder.addField("Configuration", configuration.getName(), false);
					Actions.reply(event, builder.build());
					getLogger(event.getGuild()).error("Error handling configuration change");
				}
				else if(result != ActionResult.NONE){
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.GREEN);
					builder.setTitle("Valeur changée");
					builder.setDescription("Commande: " + getName());
					builder.addField("Configuration:", configuration.getName(), false);
					builder.addField("Valeur:", beforeArgs.toString(), false);
					Actions.reply(event, builder.build());
					getLogger(event.getGuild()).info("Config value {} changed", configuration.getName());
				}
			}
			else{
				EmbedBuilder builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.setColor(Color.ORANGE);
				builder.setTitle("Configuration non trouvée");
				builder.addField("Configurations disponibles", Arrays.stream(Settings.SETTINGS).map(Configuration::getName).collect(Collectors.joining(", ")), false);
				Actions.reply(event, builder.build());
			}
		}
		else{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.ORANGE);
			builder.setTitle("Merci de renseigner le nom de la configuration à changer");
			Actions.reply(event, builder.build());
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Configuration", "Le nom de la configuration", false);
		builder.addField("Optionnel: Valeur", "Paramètres de la sous commande", false);
	}
	
	/**
	 * Process the configuration.
	 *
	 * @param event         The event that triggered this change.
	 * @param configuration The configuration to change.
	 * @param args          The args to pass.
	 *
	 * @return The result that happened.
	 */
	private ActionResult processWithValue(MessageReceivedEvent event, Configuration configuration, LinkedList<String> args){
		if(configuration.isActionAllowed(getType())){
			try{
				getLogger(event.getGuild()).info("Handling change {} on config {} with parameters {}", getType().name(), configuration, args);
				return configuration.handleChange(event, getType(), args);
			}
			catch(Exception e){
				EmbedBuilder builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.setColor(Color.RED);
				builder.setTitle("Erreur durant l'opération");
				builder.setDescription("Commande: " + getName());
				builder.addField("Raison", e.getMessage(), false);
				builder.addField("Configuration", configuration.getName(), false);
				Actions.reply(event, builder.build());
				getLogger(event.getGuild()).error("Error handling configuration change", e);
			}
		}
		else{
			Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, "Opération impossible").addField("Raison", "Opération " + getType().name() + " impossible sur cette configuration", false).addField("Configuration", configuration.getName(), false).build());
		}
		return ActionResult.ERROR;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <configuration> [valeur]";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Override
	public String getName(){
		return "Opération " + getType().name();
	}
	
	@Override
	public List<String> getCommand(){
		return commands;
	}
	
	@Override
	public String getDescription(){
		return "Effectue une opération " + getType().name() + " sur cette configuration";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
	
	/**
	 * Get the type of operation done.
	 *
	 * @return The type.
	 */
	private ChangeConfigType getType(){
		return type;
	}
}
