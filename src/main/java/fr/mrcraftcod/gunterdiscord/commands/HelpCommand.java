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
import javax.annotation.Nonnull;
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
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Command", "The command to get information for (default: displays the list of the available commands)", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var prefix = new PrefixConfig(event.getGuild()).getObject("");
		if(args.isEmpty()){
			final var builder = new EmbedBuilder();
			builder.setColor(Color.GREEN);
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setTitle("Available commands");
			Arrays.stream(CommandsMessageListener.commands).filter(command -> command.isAllowed(event.getMember())).map(command -> new MessageEmbed.Field(prefix + command.getCommandStrings().get(0), command.getDescription(), false)).filter(message -> Objects.nonNull(message.getName())).sorted(Comparator.comparing(MessageEmbed.Field::getName)).forEach(builder::addField);
			Actions.reply(event, builder.build());
		}
		else{
			var command = Arrays.stream(CommandsMessageListener.commands).filter(command1 -> command1.getCommandStrings().contains(args.get(0).toLowerCase())).filter(command1 -> command1.isAllowed(event.getMember())).findAny();
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
				builder.setTitle(getName());
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
			Actions.reply(event, builder.build());
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [command...]";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Help";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("help", "h");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Gets the help";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
