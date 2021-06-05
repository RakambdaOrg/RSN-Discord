package fr.raksrinana.rsndiscord.command;

import fr.raksrinana.rsndiscord.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.util.Objects.nonNull;

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
	protected CommandComposite(@Nullable Command parent){
		super(parent);
		subCommands = new LinkedHashSet<>();
	}
	
	/**
	 * Add a sub command.
	 *
	 * @param command The command to add.
	 */
	protected void addSubCommand(@NotNull Command command){
		if(subCommands.stream()
				.flatMap(subCommand -> subCommand.getCommandStrings().stream())
				.anyMatch(commandStr -> command.getCommandStrings().contains(commandStr))){
			var message = "Duplicate command found when adding %s with inputs %s".formatted(
					command.getClass(),
					command.getCommandStrings());
			throw new IllegalStateException(message);
		}
		subCommands.add(command);
	}
	
	/**
	 * Get a sub command.
	 *
	 * @param commandStr The command.
	 *
	 * @return The command or null if not found.
	 */
	@NotNull
	public Optional<Command> getSubCommand(@NotNull String commandStr){
		return subCommands.stream()
				.filter(command -> command.getCommandStrings().contains(commandStr))
				.findAny();
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder embedBuilder){
		embedBuilder.addField("sub-command", getSubCommandsList(), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws RuntimeException{
		var member = event.getMember();
		if(Objects.isNull(member) || !isAllowed(member)){
			throw new NotAllowedException("You're not allowed to execute this command");
		}
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		var switchStr = args.poll();
		
		if(nonNull(switchStr)){
			var toExecute = subCommands.stream()
					.filter(command -> command.getCommandStrings().contains(switchStr))
					.findFirst();
			
			if(toExecute.isPresent()){
				return toExecute.get().execute(event, args);
			}
			
			sendErrorMessage(guild, event.getChannel(), author, ORANGE, translate(guild, "commands.error.invalid-argument", switchStr));
		}
		else{
			sendErrorMessage(guild, event.getChannel(), author, RED, translate(guild, "commands.error.required-argument"));
		}
		return CommandResult.HANDLED;
	}
	
	private void sendErrorMessage(@NotNull Guild guild, @NotNull TextChannel channel, @NotNull User author, @NotNull Color color, @NotNull String reason){
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(color)
				.setTitle(translate(guild, "commands.error.title"))
				.addField(translate(guild, "commands.error.command"), getName(guild), false)
				.addField(translate(guild, "commands.error.reason"), reason, false)
				.addField(translate(guild, "commands.error.sub-commands"), getSubCommandsList(), false)
				.build();
		
		JDAWrappers.message(channel, embed).submitAndDelete(2);
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [sub-command]";
	}
	
	@NotNull
	private String getSubCommandsList(){
		return subCommands.stream()
				.flatMap(command -> command.getCommandStrings().stream())
				.collect(Collectors.joining(", "));
	}
}
