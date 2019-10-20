package fr.raksrinana.rsndiscord.commands.photo;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-26
 */
public class PhotoListCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	PhotoListCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var builder = new EmbedBuilder();
		builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
		builder.setColor(Color.GREEN);
		builder.setTitle("Users of the trombinoscope");
		NewSettings.getConfiguration(event.getGuild()).getTrombinoscopeConfiguration().getParticipantRole().flatMap(RoleConfiguration::getRole).ifPresent(role -> Utilities.getMembersWithRole(role).stream().map(u -> u.getUser().getName()).forEach(u -> builder.addField("", u, false)));
		Actions.reply(event, builder.build());
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Users";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("l", "list");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Lists the users of the trombinoscope";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
