package fr.mrcraftcod.gunterdiscord.commands.generic;

import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-26
 */
public abstract class CompositeCommand extends BasicCommand{
	private final LinkedHashSet<Command> subCommands;
	
	/**
	 * Constructor.
	 */
	protected CompositeCommand(){
		super();
		subCommands = new LinkedHashSet<>();
	}
	
	/**
	 * Add a sub command.
	 *
	 * @param command The command to add.
	 */
	protected void addSubCommand(final Command command){
		subCommands.add(command);
	}
	
	/**
	 * Get a sub command.
	 *
	 * @param command The command.
	 *
	 * @return The command or null if not found.
	 */
	public Command getSubCommand(@NotNull final String command){
		return subCommands.stream().filter(c -> c.getCommand().contains(command)).findAny().orElse(null);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		builder.addField("Sous-commande", subCommands.stream().flatMap(c -> c.getCommand().stream()).collect(Collectors.joining(", ")), false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		if(!isAllowed(event.getMember())){
			throw new NotAllowedException();
		}
		final var switchStr = args.poll();
		if(switchStr == null){
			Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.RED, "Erreur durant l'execution de la commande").addField("Command", getName(), false).addField("Raison", getCommandUsage(), false).addField("Arguments disponibles", subCommands.stream().flatMap(c -> c.getCommand().stream()).collect(Collectors.joining(", ")), false).build());
		}
		else{
			final var toExecute = subCommands.stream().filter(c -> c.getCommand().contains(switchStr.toLowerCase())).findFirst();
			if(toExecute.isPresent()){
				toExecute.get().execute(event, args);
			}
			else{
				Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, "Erreur durant l'execution de la commande").addField("Command", getName(), false).addField("Raison", "Argument non valide", false).addField("Arguments disponibles", subCommands.stream().flatMap(c -> c.getCommand().stream()).collect(Collectors.joining(", ")), false).build());
			}
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [sous-commande]";
	}
}
