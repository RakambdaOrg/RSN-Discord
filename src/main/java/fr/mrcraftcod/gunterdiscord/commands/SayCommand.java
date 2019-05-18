package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class SayCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Message", "The message to say", false);
	}
	
	@Override
	public CommandResult execute(final GuildMessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		Actions.reply(event, String.join(" ", args));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <message...>";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Override
	public String getName(){
		return "Say";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("say");
	}
	
	@Override
	public String getDescription(){
		return "Sends a message as the bot";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
