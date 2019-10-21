package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@BotCommand
public class RemoveAllRoleCommand extends BasicCommand{
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(!event.getMessage().getMentionedRoles().isEmpty()){
			event.getMessage().getMentionedRoles().stream().findFirst().ifPresent(r -> {
				final var members = event.getGuild().getMembersWithRoles(r);
				Actions.replyFormatted(event, "Will remove the role of %d people, this may take a while", members.size());
				members.forEach(m -> Actions.removeRole(m, r));
			});
		}
		else{
			Actions.reply(event, "Please mention a role");
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Remove role from users";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("rafr");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Remove all users from a role";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}