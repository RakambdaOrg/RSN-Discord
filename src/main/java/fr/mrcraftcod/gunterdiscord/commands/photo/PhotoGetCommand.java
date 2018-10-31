package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.TrombinoscopeRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class PhotoGetCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	PhotoGetCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user of the picture (default: @me)", false);
		builder.addField("Number", "The number of the picture (if none are provided, the pictue will be picked randomly)", false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		Actions.deleteMessage(event.getMessage());
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED){
			return CommandResult.NOT_ALLOWED;
		}
		final User user;
		final var users = event.getMessage().getMentionedUsers();
		if(users.size() > 0){
			user = users.get(0);
			args.poll();
		}
		else{
			user = event.getAuthor();
		}
		
		final var member = event.getGuild().getMember(user);
		if(member == null || !Utilities.hasRole(member, new TrombinoscopeRoleConfig(event.getGuild()).getObject())){
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.RED);
			builder.setTitle("The user isn't part of the trombinoscope");
			Actions.reply(event, builder.build());
		}
		else if(new PhotoChannelConfig(event.getGuild()).isChannel(event.getTextChannel())){
			final var paths = new PhotoConfig(event.getGuild()).getValue(user.getIdLong());
			if(paths != null && !paths.isEmpty()){
				var randomGen = true;
				var rnd = ThreadLocalRandom.current().nextInt(paths.size());
				if(args.peek() != null){
					try{
						rnd = Math.max(0, Math.min(paths.size(), Integer.parseInt(args.pop())) - 1);
						randomGen = false;
					}
					catch(final Exception e){
						getLogger(event.getGuild()).warn("Provided photo index isn't an integer", e);
					}
				}
				final var file = new File(paths.get(rnd));
				if(file.exists()){
					final var ID = file.getName().substring(0, file.getName().lastIndexOf("."));
					final var builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.GREEN);
					builder.addField("Selection", String.format("%d/%d%s", rnd + 1, paths.size(), randomGen ? " random" : ""), true);
					builder.addField("User", user.getAsMention(), true);
					builder.addField("ID", ID, true);
					Actions.reply(event, builder.build());
					Actions.sendFile(event.getTextChannel(), file);
				}
				else{
					final var builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.RED);
					builder.setTitle("Image not found");
					Actions.reply(event, builder.build());
				}
			}
			else{
				final var builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.setColor(Color.ORANGE);
				builder.setTitle("This user have no picture");
				Actions.reply(event, builder.build());
			}
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user] [number]";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Picture";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("photo", "p", "g", "get");
	}
	
	@Override
	public String getDescription(){
		return "Get a picture from the trombinoscope";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
