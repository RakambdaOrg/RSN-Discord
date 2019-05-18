package fr.mrcraftcod.gunterdiscord.commands.generic;

import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-26
 */
public abstract class CommandComposite extends BasicCommand{
	private final LinkedHashSet<Command> subCommands;
	
	/**
	 * Constructor.
	 */
	protected CommandComposite(){
		super();
		subCommands = new LinkedHashSet<>();
	}
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	protected CommandComposite(final Command parent){
		super(parent);
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
		builder.addField("Sub-command", subCommands.stream().flatMap(c -> c.getCommand().stream()).collect(Collectors.joining(", ")), false);
	}
	
	@Override
	public CommandResult execute(final GuildMessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		if(!isAllowed(event.getMember())){
			throw new NotAllowedException();
		}
		final var switchStr = args.poll();
		if(Objects.isNull(switchStr)){
			Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.RED, "Error while executing command").addField("Command", getName(), false).addField("Reason", getCommandUsage(), false).addField("Arguments available", subCommands.stream().flatMap(c -> c.getCommand().stream()).collect(Collectors.joining(", ")), false).build());
		}
		else{
			final var toExecute = subCommands.stream().filter(c -> c.getCommand().contains(switchStr.toLowerCase())).findFirst();
			if(toExecute.isPresent()){
				toExecute.get().execute(event, args);
			}
			else{
				Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, "Error while executing command").addField("Command", getName(), false).addField("Reason", "Invalid argument", false).addField("Arguments available", subCommands.stream().flatMap(c -> c.getCommand().stream()).collect(Collectors.joining(", ")), false).build());
			}
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [sub-command]";
	}
}
