package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.generic.*;
import fr.raksrinana.rsndiscord.listeners.CommandsMessageListener;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.reflections.Reflections;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@BotCommand
public class HelpCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Command", "The command to get information for (default: displays the list of the available commands)", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var prefix = Settings.get(event.getGuild()).getPrefix().orElse(CommandsMessageListener.defaultPrefix);
		final var commands = new Reflections(Main.class.getPackage().getName() + ".commands").getTypesAnnotatedWith(BotCommand.class).stream().map(c -> {
			try{
				return c.getConstructor().newInstance();
			}
			catch(InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
				Log.getLogger(null).error("Failed to create instance of {}", c.getName());
			}
			return null;
		}).filter(Objects::nonNull).filter(c -> c instanceof Command).map(c -> (Command) c);
		if(args.isEmpty()){
			final var builder = new EmbedBuilder();
			builder.setColor(Color.GREEN);
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setTitle("Available commands");
			commands.filter(command -> command.isAllowed(event.getMember())).map(command -> new MessageEmbed.Field(prefix + command.getCommandStrings().get(0), command.getDescription(), false)).filter(message -> Objects.nonNull(message.getName())).sorted(Comparator.comparing(MessageEmbed.Field::getName)).forEach(builder::addField);
			Actions.reply(event, "", builder.build());
		}
		else{
			var command = commands.filter(command1 -> command1.getCommandStrings().contains(args.get(0).toLowerCase())).filter(command1 -> command1.isAllowed(event.getMember())).findAny();
			args.poll();
			while(!args.isEmpty() && command.isPresent() && command.get() instanceof CommandComposite){
				final var command2 = ((CommandComposite) command.get()).getSubCommand(args.get(0).toLowerCase());
				if(command2.isPresent()){
					command = command2;
				}
				else{
					break;
				}
				args.poll();
			}
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			if(command.isPresent()){
				builder.setColor(Color.GREEN);
				builder.setTitle(this.getName());
				builder.addField("Name", command.get().getName(), true);
				builder.addField("Description", command.get().getDescription(), true);
				builder.addField("Command", String.join(", ", command.get().getCommandStrings()), false);
				builder.addField("Usage", command.get().getCommandUsage(), false);
				builder.addBlankField(true);
				builder.addField("", "Arguments", false);
				command.get().addHelp(event.getGuild(), builder);
			}
			else{
				builder.setColor(Color.ORANGE);
				builder.addField(prefix + args.poll(), "This command doesn't exist or you don't have access to it", false);
			}
			Actions.reply(event, "", builder.build());
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [command...]";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Help";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("help", "h");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Gets the help";
	}
}
