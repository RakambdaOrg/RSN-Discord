package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.*;
import fr.raksrinana.rsndiscord.event.CommandsEventListener;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;
import static java.util.Comparator.comparing;

@BotCommand
public class HelpCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("command", translate(guild, "command.help.help.command"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		
		var prefix = Settings.get(event.getGuild()).getPrefix()
				.orElse(CommandsEventListener.DEFAULT_PREFIX);
		
		var allCommands = event.getJDA().getRegisteredListeners().stream()
				.filter(CommandsEventListener.class::isInstance).findFirst()
				.map(CommandsEventListener.class::cast)
				.map(CommandsEventListener::getCommands)
				.orElseGet(Set::of);
		
		if(args.isEmpty()){
			rootHelp(event, prefix, allCommands);
		}
		else{
			commandHelp(event, args, prefix, allCommands);
		}
		return CommandResult.SUCCESS;
	}
	
	private void rootHelp(GuildMessageReceivedEvent event, String prefix, Set<Command> allCommands){
		var guild = event.getGuild();
		var author = event.getAuthor();
		var builder = new EmbedBuilder()
				.setColor(GREEN)
				.setAuthor(author.getName(), null, author.getAvatarUrl())
				.setTitle(translate(guild, "help.available-commands"));
		
		allCommands.stream()
				.filter(command -> command.isAllowed(event.getMember()))
				.sorted(comparing(command -> command.getCommandStrings().get(0)))
				.forEach(command -> builder.addField(prefix + command.getCommandStrings().get(0), command.getDescription(guild), false));
		
		JDAWrappers.message(event, builder.build()).submit()
				.thenAccept(deleteMessage(date -> date.plusMinutes(10)));
	}
	
	private void commandHelp(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args, String prefix, Set<Command> allCommands){
		var commandName = args.pop();
		
		var rootCommandName = commandName.toLowerCase();
		var command = allCommands.stream()
				.filter(command1 -> command1.getCommandStrings().contains(rootCommandName))
				.filter(command1 -> command1.isAllowed(event.getMember()))
				.findAny();
		
		while(!args.isEmpty() && command.isPresent() && command.get() instanceof CommandComposite){
			commandName = args.pop();
			var subCommand = ((CommandComposite) command.get()).getSubCommand(commandName.toLowerCase());
			if(subCommand.isPresent()){
				command = subCommand;
			}
			else{
				break;
			}
		}
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		var builder = new EmbedBuilder()
				.setAuthor(author.getName(), null, author.getAvatarUrl());
		
		if(command.isPresent()){
			builder.setColor(GREEN)
					.setTitle(getName(guild))
					.addField(translate(guild, "help.name"), command.get().getName(guild), true)
					.addField(translate(guild, "help.description"), command.get().getDescription(guild), true)
					.addField(translate(guild, "help.command"), String.join(", ", command.get().getCommandStrings()), false)
					.addField(translate(guild, "help.usage"), command.get().getCommandUsage(), false)
					.addBlankField(true)
					.addField("", translate(guild, "help.arguments"), false);
			
			command.get().addHelp(guild, builder);
		}
		else{
			builder.setColor(Color.ORANGE)
					.addField(prefix + commandName, translate(guild, "help.not-exist"), false);
		}
		
		JDAWrappers.message(event, builder.build()).submit()
				.thenAccept(deleteMessage(date -> date.plusMinutes(10)));
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [command...]";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.help", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.help.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.help.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("help", "h");
	}
}
