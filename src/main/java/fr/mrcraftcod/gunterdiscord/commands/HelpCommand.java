package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.CommandsMessageListener;
import fr.mrcraftcod.gunterdiscord.settings.configs.PrefixConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.*;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class HelpCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Command", "The command to get information for (default: displays the list of the available commands)", false);
	}
	
	@Override
	public CommandResult execute(final GuildMessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var prefix = new PrefixConfig(event.getGuild()).getObject("");
		if(args.isEmpty()){
			final var builder = new EmbedBuilder();
			builder.setColor(Color.GREEN);
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setTitle("Available commands");
			Arrays.stream(CommandsMessageListener.commands).filter(c -> c.isAllowed(event.getMember())).map(s -> new MessageEmbed.Field(prefix + s.getCommandStrings().get(0), s.getDescription(), false)).sorted(Comparator.comparing(MessageEmbed.Field::getName)).forEach(builder::addField);
			Actions.reply(event, builder.build());
		}
		else{
			var command = Arrays.stream(CommandsMessageListener.commands).filter(s -> s.getCommandStrings().contains(args.get(0).toLowerCase())).filter(c -> c.isAllowed(event.getMember())).findAny().orElse(null);
			args.poll();
			while(!args.isEmpty() && command instanceof CommandComposite){
				final var command2 = ((CommandComposite) command).getSubCommand(args.get(0).toLowerCase());
				if(Objects.isNull(command2)){
					break;
				}
				command = command2;
				args.poll();
			}
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			if(Objects.nonNull(command)){
				builder.setColor(Color.GREEN);
				builder.setTitle(getName());
				builder.addField("Name", command.getName(), true);
				builder.addField("Description", command.getDescription(), true);
				builder.addField("Command", String.join(", ", command.getCommandStrings()), false);
				builder.addField("Usage", command.getCommandUsage(), false);
				builder.addBlankField(true);
				builder.addField("", "Arguments", false);
				command.addHelp(event.getGuild(), builder);
			}
			else{
				builder.setColor(Color.ORANGE);
				builder.addField(prefix + args.poll(), "This command doesn't exist or you don't have access to it", false);
			}
			Actions.reply(event, builder.build());
		}
		
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [command...]";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Help";
	}
	
	@Override
	public List<String> getCommandStrings(){
		return List.of("help", "h");
	}
	
	@Override
	public String getDescription(){
		return "Gets the help";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
