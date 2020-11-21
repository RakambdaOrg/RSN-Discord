package fr.raksrinana.rsndiscord.commands.generic;

import fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

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
	protected CommandComposite(final Command parent){
		super(parent);
		this.subCommands = new LinkedHashSet<>();
	}
	
	/**
	 * Add a sub command.
	 *
	 * @param command The command to add.
	 */
	protected void addSubCommand(@NonNull final Command command){
		if(this.subCommands.stream()
				.flatMap(c -> c.getCommandStrings().stream())
				.anyMatch(commandStr -> command.getCommandStrings().contains(commandStr))){
			throw new IllegalStateException(MessageFormat.format("Duplicate command found when adding {0} with inputs {1}", command.getClass(), command.getCommandStrings()));
		}
		this.subCommands.add(command);
	}
	
	/**
	 * Get a sub command.
	 *
	 * @param commandStr The command.
	 *
	 * @return The command or null if not found.
	 */
	@NonNull
	public Optional<Command> getSubCommand(final String commandStr){
		return this.subCommands.stream().filter(command -> command.getCommandStrings().contains(commandStr)).findAny();
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder embedBuilder){
		var subCommands = this.subCommands.stream()
				.flatMap(command -> command.getCommandStrings().stream())
				.collect(Collectors.joining(", "));
		embedBuilder.addField("sub-command", subCommands, false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args) throws RuntimeException{
		if(!this.isAllowed(event.getMember())){
			throw new NotAllowedException("You're not allowed to execute this command");
		}
		final var switchStr = args.poll();
		if(Objects.isNull(switchStr)){
			var embed = Utilities.buildEmbed(event.getAuthor(), Color.RED, translate(event.getGuild(), "commands.error.title"), null)
					.addField(translate(event.getGuild(), "commands.error.command"), this.getName(event.getGuild()), false)
					.addField(translate(event.getGuild(), "commands.error.reason"), translate(event.getGuild(), "commands.error.required-argument"), false)
					.addField(translate(event.getGuild(), "commands.error.sub-commands"), this.subCommands.stream()
							.flatMap(command -> command.getCommandStrings().stream())
							.collect(Collectors.joining(", ")), false)
					.build();
			event.getChannel().sendMessage(embed).submit()
					.thenAccept(message -> ScheduleUtils.deleteMessage(message, date -> date.plusMinutes(2)));
		}
		else{
			final var toExecute = this.subCommands.stream().filter(command -> command.getCommandStrings().contains(switchStr)).findFirst();
			if(toExecute.isPresent()){
				return toExecute.get().execute(event, args);
			}
			else{
				var embed = Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, translate(event.getGuild(), "commands.error.title"), null)
						.addField(translate(event.getGuild(), "commands.error.command"), this.getName(event.getGuild()), false)
						.addField(translate(event.getGuild(), "commands.error.reason"), translate(event.getGuild(), "commands.error.invalid-argument", switchStr), false)
						.addField(translate(event.getGuild(), "commands.error.sub-commands"), this.subCommands.stream()
								.flatMap(command -> command.getCommandStrings().stream())
								.collect(Collectors.joining(", ")), false)
						.build();
				event.getChannel().sendMessage(embed).submit()
						.thenAccept(message -> ScheduleUtils.deleteMessage(message, date -> date.plusMinutes(2)));
			}
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [sub-command]";
	}
}
