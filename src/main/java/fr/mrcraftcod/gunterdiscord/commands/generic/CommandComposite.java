package fr.mrcraftcod.gunterdiscord.commands.generic;

import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
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
		this.subCommands = new LinkedHashSet<>();
	}
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	protected CommandComposite(@Nullable final Command parent){
		super(parent);
		this.subCommands = new LinkedHashSet<>();
	}
	
	/**
	 * Add a sub command.
	 *
	 * @param command The command to add.
	 */
	protected void addSubCommand(@Nonnull final Command command){
		this.subCommands.add(command);
	}
	
	/**
	 * Get a sub command.
	 *
	 * @param commandStr The command.
	 *
	 * @return The command or null if not found.
	 */
	@Nonnull
	public Optional<Command> getSubCommand(final String commandStr){
		return this.subCommands.stream().filter(command -> command.getCommandStrings().contains(commandStr)).findAny();
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder embedBuilder){
		embedBuilder.addField("Sub-command", this.subCommands.stream().flatMap(command -> command.getCommandStrings().stream()).collect(Collectors.joining(", ")), false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		if(!this.isAllowed(event.getMember())){
			throw new NotAllowedException("You're not allowed to execute this command");
		}
		final var switchStr = args.poll();
		if(Objects.isNull(switchStr)){
			Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.RED, "Error while executing command").addField("Command", this.getName(), false).addField("Reason", this.getCommandUsage(), false).addField("Arguments available", this.subCommands.stream().flatMap(command -> command.getCommandStrings().stream()).collect(Collectors.joining(", ")), false).build());
		}
		else{
			final var toExecute = this.subCommands.stream().filter(command -> command.getCommandStrings().contains(switchStr)).findFirst();
			if(toExecute.isPresent()){
				toExecute.get().execute(event, args);
			}
			else{
				Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, "Error while executing command").addField("Command", this.getName(), false).addField("Reason", "Invalid argument", false).addField("Arguments available", this.subCommands.stream().flatMap(command -> command.getCommandStrings().stream()).collect(Collectors.joining(", ")), false).build());
			}
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [sub-command]";
	}
}
