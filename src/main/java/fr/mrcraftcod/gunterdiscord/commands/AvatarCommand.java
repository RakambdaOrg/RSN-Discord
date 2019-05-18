package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class AvatarCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user to get the avatar from", false);
	}
	
	@Override
	public CommandResult execute(final GuildMessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(!event.getMessage().getMentionedUsers().isEmpty()){
			final var user = event.getMessage().getMentionedUsers().get(0);
			final var builder = new EmbedBuilder();
			builder.setColor(Color.GREEN);
			builder.setAuthor(user.getName(), null, user.getAvatarUrl());
			builder.addField("URL", user.getAvatarUrl(), true);
			builder.setImage(user.getAvatarUrl());
			Actions.reply(event, builder.build());
		}
		else{
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.RED);
			builder.addField("Error", "Please mention a user", true);
			Actions.reply(event, builder.build());
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user>";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Avatar";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("avatar");
	}
	
	@Override
	public String getDescription(){
		return "Gets the avatar of a user";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
