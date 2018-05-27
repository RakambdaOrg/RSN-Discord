package fr.mrcraftcod.gunterdiscord.commands.generic;

import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-26
 */
public abstract class CompositeCommand extends BasicCommand
{
	private final LinkedHashSet<Command> subCommands;
	
	/**
	 * Constructor.
	 */
	public CompositeCommand()
	{
		super();
		subCommands = new LinkedHashSet<>();
	}
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	public CompositeCommand(Command parent)
	{
		super(parent);
		subCommands = new LinkedHashSet<>();
	}
	
	/**
	 * Add a sub command.
	 *
	 * @param command The command to add.
	 *
	 * @return This object.
	 */
	public CompositeCommand addSubCommand(Command command)
	{
		subCommands.add(command);
		return this;
	}
	
	/**
	 * Get a sub command.
	 *
	 * @param command The command.
	 *
	 * @return The command or null if not found.
	 */
	public Command getSubCommand(String command)
	{
		return subCommands.stream().filter(c -> c.getCommand().contains(command)).findAny().orElse(null);
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		if(!isAllowed(event.getMember()))
			throw new NotAllowedException();
		String switchStr = args.poll();
		if(switchStr == null)
			Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.RED, "Erreur durant l'execution de la commande").addField("Command", getName(), false).addField("Raison", getCommandUsage(), false).addField("Arguments disponibles", subCommands.stream().flatMap(c -> c.getCommand().stream()).collect(Collectors.joining(", ")), false).build());
		else
		{
			Optional<Command> toExecute = subCommands.stream().filter(c -> c.getCommand().contains(switchStr.toLowerCase())).findFirst();
			if(toExecute.isPresent())
				toExecute.get().execute(event, args);
			else
				Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, "Erreur durant l'execution de la commande").addField("Command", getName(), false).addField("Raison", "Argument non valide", false).addField("Arguments disponibles", subCommands.stream().flatMap(c -> c.getCommand().stream()).collect(Collectors.joining(", ")), false).build());
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public void addHelp(Guild guild, EmbedBuilder builder)
	{
		builder.addField("Sous-commande", subCommands.stream().flatMap(c -> c.getCommand().stream()).collect(Collectors.joining(", ")), false);
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " [sous-commande]";
	}
}
