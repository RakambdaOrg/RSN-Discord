package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.listeners.CommandsMessageListener;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class HelpCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("command", translate(guild, "command.help.help.command"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var prefix = Settings.get(event.getGuild()).getPrefix().orElse(CommandsMessageListener.defaultPrefix);
		final var allCommands = event.getJDA().getRegisteredListeners().stream().filter(l -> l instanceof CommandsMessageListener).findFirst().map(CommandsMessageListener.class::cast).map(CommandsMessageListener::getCommands).orElseGet(Set::of);
		if(args.isEmpty()){
			final var builder = new EmbedBuilder();
			builder.setColor(Color.GREEN);
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setTitle(translate(event.getGuild(), "help.available-commands"));
			allCommands.stream().filter(command -> command.isAllowed(event.getMember())).map(command -> new MessageEmbed.Field(prefix + command.getCommandStrings().get(0), command.getDescription(event.getGuild()), false)).filter(message -> Objects.nonNull(message.getName())).sorted(Comparator.comparing(MessageEmbed.Field::getName)).forEach(builder::addField);
			Actions.sendEmbed(event.getChannel(), builder.build());
		}
		else{
			var command = allCommands.stream().filter(command1 -> command1.getCommandStrings().contains(args.get(0).toLowerCase())).filter(command1 -> command1.isAllowed(event.getMember())).findAny();
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
				builder.setTitle(this.getName(event.getGuild()));
				builder.addField(translate(event.getGuild(), "help.name"), command.get().getName(event.getGuild()), true);
				builder.addField(translate(event.getGuild(), "help.description"), command.get().getDescription(event.getGuild()), true);
				builder.addField(translate(event.getGuild(), "help.command"), String.join(", ", command.get().getCommandStrings()), false);
				builder.addField(translate(event.getGuild(), "help.usage"), command.get().getCommandUsage(), false);
				builder.addBlankField(true);
				builder.addField("", translate(event.getGuild(), "help.arguments"), false);
				command.get().addHelp(event.getGuild(), builder);
			}
			else{
				builder.setColor(Color.ORANGE);
				builder.addField(prefix + args.poll(), translate(event.getGuild(), "help.not-exist"), false);
			}
			Actions.sendEmbed(event.getChannel(), builder.build());
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
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.help.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("help", "h");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.help.description");
	}
}
