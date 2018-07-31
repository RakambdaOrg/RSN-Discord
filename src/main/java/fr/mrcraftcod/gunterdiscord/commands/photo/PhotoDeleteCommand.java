package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
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
import java.util.LinkedList;
import java.util.List;

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
	PhotoDeleteCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Optionnel: Utilisateur", "L'utilisateur visé par la suppression (par défaut @me)", false);
		builder.addField("Optionnel: ID", "L'ID de la photo à supprimer (si aucun n'est précisé toutes les photos seront supprimées)", false);
	}
	
	@Override
	public CommandResult execute(@NotNull MessageReceivedEvent event, @NotNull LinkedList<String> args) throws Exception{
		Actions.deleteMessage(event.getMessage());
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED){
			return CommandResult.NOT_ALLOWED;
		}
		if(args.size() > 0){
			User user;
			List<User> users = event.getMessage().getMentionedUsers();
			if(users.size() > 0){
				user = users.get(0);
				args.poll();
			}
			else{
				user = event.getAuthor();
			}
			
			if(user.getIdLong() != event.getAuthor().getIdLong()){
				if(Utilities.isModerator(event.getMember()) || Utilities.isAdmin(event.getMember())){
					new PhotoConfig(event.getGuild()).deleteKeyValue(user.getIdLong(), args.poll());
					if(new PhotoConfig(event.getGuild()).getValue(user.getIdLong()).isEmpty()){
						Actions.removeRole(user, new TrombinoscopeRoleConfig(event.getGuild()).getObject());
					}
					Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Photos de %s supprimées", user.getAsMention());
				}
				else{
					Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Vous ne pouvez pas supprimer la photo de quelqu'un d'autre");
				}
			}
			else{
				new PhotoConfig(event.getGuild()).deleteKeyValue(user.getIdLong(), args.poll());
				if(new PhotoConfig(event.getGuild()).getValue(user.getIdLong()).isEmpty()){
					Actions.removeRole(user, new TrombinoscopeRoleConfig(event.getGuild()).getObject());
				}
				Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Photos supprimées");
			}
		}
		else{
			new PhotoConfig(event.getGuild()).deleteKey(event.getAuthor().getIdLong());
			Actions.removeRole(event.getAuthor(), new TrombinoscopeRoleConfig(event.getGuild()).getObject());
			Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Photos supprimées");
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@utilisateur] [ID]";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Supprimer photo";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("del", "d", "rm", "s");
	}
	
	@Override
	public String getDescription(){
		return "Supprime une ou des photos du trombinoscope";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
