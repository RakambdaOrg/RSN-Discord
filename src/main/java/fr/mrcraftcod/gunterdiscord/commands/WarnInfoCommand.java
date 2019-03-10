package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.RemoveRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class WarnInfoCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", "The user to get the infos for (default: @me)", false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var target = event.getMessage().getMentionedUsers().stream().findFirst().orElse(event.getAuthor());
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, "Warns info");
		builder.addField("User", target.getAsMention(), false);
		final var bans = new RemoveRoleConfig(event.getGuild()).getValue(target.getIdLong());
		if(bans == null || bans.isEmpty()){
			builder.setColor(Color.GREEN);
			builder.setDescription("The user have no warns");
		}
		else{
			final var formatter = new SimpleDateFormat("dd MMM at HH:mm:ssZ");
			builder.setDescription("Warns will be removed with a maximum delay of 15 minutes");
			bans.keySet().forEach(key -> builder.addField("Role " + event.getGuild().getRoleById(key).getName(), "Ends the " + formatter.format(new Date(bans.get(key))), false));
		}
		Actions.reply(event, builder.build());
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user]";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Warn info";
	}
	
	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public List<String> getCommand(){
		return List.of("warninfo", "wi");
	}
	
	@Override
	public String getDescription(){
		return "Gets information about the warns in progress";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
