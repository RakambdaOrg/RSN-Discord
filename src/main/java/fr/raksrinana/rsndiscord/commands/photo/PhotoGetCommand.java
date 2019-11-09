package fr.raksrinana.rsndiscord.commands.photo;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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
	PhotoGetCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user of the picture (default: @me)", false);
		builder.addField("Number", "The number of the picture (if none are provided, the picture will be picked randomly)", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		Actions.deleteMessage(event.getMessage());
		super.execute(event, args);
		final User user;
		final var users = event.getMessage().getMentionedUsers();
		if(!users.isEmpty()){
			user = users.get(0);
			args.poll();
		}
		else{
			user = event.getAuthor();
		}
		final var member = event.getGuild().getMember(user);
		if(Objects.isNull(member) || Settings.getConfiguration(event.getGuild()).getTrombinoscopeConfiguration().getParticipantRole().flatMap(RoleConfiguration::getRole).map(r -> Utilities.hasRole(member, r)).map(b -> !b).orElse(true)){
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.RED);
			builder.setTitle("The user isn't part of the trombinoscope");
			Actions.reply(event, builder.build());
		}
		else if(Settings.getConfiguration(event.getGuild()).getTrombinoscopeConfiguration().getPhotoChannel().map(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong())).orElse(false)){
			final var paths = Settings.getConfiguration(event.getGuild()).getTrombinoscopeConfiguration().getPhotos(user);
			if(!paths.isEmpty()){
				var randomGen = true;
				var rnd = ThreadLocalRandom.current().nextInt(paths.size());
				if(!args.isEmpty()){
					try{
						rnd = Math.max(0, Math.min(paths.size(), Integer.parseInt(args.pop())) - 1);
						randomGen = false;
					}
					catch(final Exception e){
						Log.getLogger(event.getGuild()).warn("Provided photo index isn't an integer", e);
					}
				}
				final var file = new File(paths.get(rnd).getPhoto());
				if(file.exists()){
					final var ID = file.getName().substring(0, file.getName().lastIndexOf("."));
					final var builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.GREEN);
					builder.addField("Selection", String.format("%d/%d%s", rnd + 1, paths.size(), randomGen ? " random" : ""), true);
					builder.addField("User", user.getAsMention(), true);
					builder.addField("ID", ID, true);
					Actions.reply(event, builder.build());
					Actions.sendFile(event.getChannel(), file);
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
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user] [number]";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Picture";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("photo", "p", "g", "get");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Get a picture from the trombinoscope";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
