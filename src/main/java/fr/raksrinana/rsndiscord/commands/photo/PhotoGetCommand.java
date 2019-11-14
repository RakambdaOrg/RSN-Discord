package fr.raksrinana.rsndiscord.commands.photo;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user of the picture (default: @me)", false);
		builder.addField("Number", "The number of the picture (if none are provided, the picture will be picked randomly)", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
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
		if(Objects.isNull(member) || Settings.get(event.getGuild()).getTrombinoscopeConfiguration().getParticipantRole().flatMap(RoleConfiguration::getRole).map(r -> member.getRoles().contains(r)).map(b -> !b).orElse(true)){
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.RED);
			builder.setTitle("The user isn't part of the trombinoscope");
			Actions.reply(event, "", builder.build());
		}
		else if(Settings.get(event.getGuild()).getTrombinoscopeConfiguration().getPhotoChannel().map(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong())).orElse(false)){
			final var paths = Settings.get(event.getGuild()).getTrombinoscopeConfiguration().getPhotos(user);
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
				final var file = Paths.get(paths.get(rnd).getPhoto());
				final var fileName = file.getFileName().toString();
				if(file.toFile().exists()){
					final var ID = fileName.substring(0, fileName.lastIndexOf("."));
					final var builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.GREEN);
					builder.addField("Selection", String.format("%d/%d%s", rnd + 1, paths.size(), randomGen ? " random" : ""), true);
					builder.addField("User", user.getAsMention(), true);
					builder.addField("ID", ID, true);
					Actions.reply(event, "", builder.build());
					try{
						Actions.sendFile(event.getChannel(), file);
					}
					catch(IOException e){
						Log.getLogger(event.getGuild()).error("Failed to send picture", e);
						return CommandResult.FAILED;
					}
				}
				else{
					final var builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.RED);
					builder.setTitle("Image not found");
					Actions.reply(event, "", builder.build());
				}
			}
			else{
				final var builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.setColor(Color.ORANGE);
				builder.setTitle("This user have no picture");
				Actions.reply(event, "", builder.build());
			}
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user] [number]";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Picture";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("photo", "p", "g", "get");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Get a picture from the trombinoscope";
	}
}
