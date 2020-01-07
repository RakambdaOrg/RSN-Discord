package fr.raksrinana.rsndiscord.commands.photo;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.commands.generic.NotAllowedException;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PhotoDeleteCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	PhotoDeleteCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user that will have its picture deleted (default: @me)", false);
		builder.addField("ID", "The ID of the picture to delete (if none is provided, every picture will be deleted)", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		Actions.deleteMessage(event.getMessage());
		super.execute(event, args);
		if(!args.isEmpty()){
			final User user;
			final var users = event.getMessage().getMentionedUsers();
			if(!users.isEmpty()){
				user = users.get(0);
				args.poll();
			}
			else{
				user = event.getAuthor();
			}
			if(!Objects.equals(user, event.getAuthor())){
				if(Utilities.isModerator(Objects.requireNonNull(event.getMember())) || Utilities.isAdmin(event.getMember())){
					final var toDel = args.pop();
					Settings.get(event.getGuild()).getTrombinoscopeConfiguration().removePhoto(user, toDel);
					if(Settings.get(event.getGuild()).getTrombinoscopeConfiguration().getPhotos(user).isEmpty()){
						Optional.ofNullable(event.getGuild().getMember(user)).ifPresent(member -> Settings.get(event.getGuild()).getTrombinoscopeConfiguration().getParticipantRole().flatMap(RoleConfiguration::getRole).ifPresent(role -> Actions.removeRole(member, role)));
					}
					Actions.replyPrivate(event.getGuild(), event.getAuthor(), MessageFormat.format("{0}'s pictures deleted", user.getAsMention()), null);
				}
				else{
					throw new NotAllowedException("You can't delete the picture of someone else");
				}
			}
			else{
				final var toDel = args.pop();
				Settings.get(event.getGuild()).getTrombinoscopeConfiguration().removePhoto(event.getAuthor(), toDel);
				if(Settings.get(event.getGuild()).getTrombinoscopeConfiguration().getPhotos(event.getAuthor()).isEmpty()){
					Settings.get(event.getGuild()).getTrombinoscopeConfiguration().getParticipantRole().flatMap(RoleConfiguration::getRole).ifPresent(role -> Actions.removeRole(event.getMember(), role));
				}
				Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Picture deleted", null);
			}
		}
		else{
			Settings.get(event.getGuild()).getTrombinoscopeConfiguration().removePhoto(event.getAuthor());
			Settings.get(event.getGuild()).getTrombinoscopeConfiguration().getParticipantRole().flatMap(RoleConfiguration::getRole).ifPresent(role -> Actions.removeRole(event.getMember(), role));
			Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Pictures deleted", null);
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user] [ID]";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Delete picture";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("del", "d", "rm", "s");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Deletes pictures from the trombinoscope";
	}
}
