package fr.raksrinana.rsndiscord.commands.generic;

import fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
				.flatMap(subCommand -> subCommand.getCommandStrings().stream())
				.anyMatch(commandStr -> command.getCommandStrings().contains(commandStr))){
			var message = MessageFormat.format("Duplicate command found when adding {0} with inputs {1}",
					command.getClass(),
					command.getCommandStrings());
			throw new IllegalStateException(message);
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
		return this.subCommands.stream()
				.filter(command -> command.getCommandStrings().contains(commandStr))
				.findAny();
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder embedBuilder){
		embedBuilder.addField("sub-command", getSubCommandsList(), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args) throws RuntimeException{
		if(!this.isAllowed(event.getMember())){
			throw new NotAllowedException("You're not allowed to execute this command");
		}
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		var switchStr = args.poll();
		
		if(nonNull(switchStr)){
			var toExecute = this.subCommands.stream()
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
		return CommandResult.SUCCESS;
	}
	
	private void sendErrorMessage(Guild guild, TextChannel channel, User author, Color color, String reason){
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(color)
				.setTitle(translate(guild, "commands.error.title"))
				.addField(translate(guild, "commands.error.command"), this.getName(guild), false)
				.addField(translate(guild, "commands.error.reason"), reason, false)
				.addField(translate(guild, "commands.error.sub-commands"), getSubCommandsList(), false)
				.build();
		channel.sendMessage(embed).submit()
				.thenAccept(message -> ScheduleUtils.deleteMessage(message, date -> date.plusMinutes(2)));
	}
	
	@NonNull
	private String getSubCommandsList(){
		return this.subCommands.stream()
				.flatMap(command -> command.getCommandStrings().stream())
				.collect(Collectors.joining(", "));
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [sub-command]";
	}
}
