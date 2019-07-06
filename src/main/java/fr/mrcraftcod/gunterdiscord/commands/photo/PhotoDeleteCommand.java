package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.TrombinoscopeRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class PhotoDeleteCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	PhotoDeleteCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user that will have its picture deleted (default: @me)", false);
		builder.addField("ID", "The ID of the picture to delete (if none is provided, every picture will be deleted)", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		Actions.deleteMessage(event.getMessage());
		super.execute(event, args);
		if(!args.isEmpty()){
			final User user;
			final var users = event.getMessage().getMentionedUsers();
			if(users.size() > 0){
				user = users.get(0);
				args.poll();
			}
			else{
				user = event.getAuthor();
			}
			if(!Objects.equals(user, event.getAuthor())){
				if(Utilities.isModerator(event.getMember()) || Utilities.isAdmin(event.getMember())){
					new PhotoConfig(event.getGuild()).deleteKeyValue(user.getIdLong(), args.poll());
					if(new PhotoConfig(event.getGuild()).getValue(user.getIdLong()).isEmpty()){
						new TrombinoscopeRoleConfig(event.getGuild()).getObject().ifPresent(role -> Actions.removeRole(user, role));
					}
					Actions.replyPrivate(event.getGuild(), event.getAuthor(), "%s's pictures deleted", user.getAsMention());
				}
				else{
					Actions.replyPrivate(event.getGuild(), event.getAuthor(), "You can't delete the picture of someone else");
				}
			}
			else{
				new PhotoConfig(event.getGuild()).deleteKeyValue(user.getIdLong(), args.poll());
				if(new PhotoConfig(event.getGuild()).getValue(user.getIdLong()).isEmpty()){
					new TrombinoscopeRoleConfig(event.getGuild()).getObject().ifPresent(role -> Actions.removeRole(user, role));
				}
				Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Picture deleted");
			}
		}
		else{
			new PhotoConfig(event.getGuild()).deleteKey(event.getAuthor().getIdLong());
			new TrombinoscopeRoleConfig(event.getGuild()).getObject().ifPresent(role -> Actions.removeRole(event.getAuthor(), role));
			Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Pictures deleted");
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user] [ID]";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Delete picture";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("del", "d", "rm", "s");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Deletes pictures from the trombinoscope";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
