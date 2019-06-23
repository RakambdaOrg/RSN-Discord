package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.RemoveRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class WarnInfoCommand extends BasicCommand{
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", "The user to get the infos for (default: @me)", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var target = event.getMessage().getMentionedUsers().stream().findFirst().orElse(event.getAuthor());
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, "Warns info");
		builder.addField("User", target.getAsMention(), false);
		new RemoveRoleConfig(event.getGuild()).getValue(target.getIdLong()).filter(bans -> !bans.isEmpty()).ifPresentOrElse(bans -> {
			final var formatter = new SimpleDateFormat("dd MMM at HH:mm:ssZ");
			builder.setDescription("Warns will be removed with a maximum delay of 15 minutes");
			bans.keySet().stream().map(key -> event.getGuild().getRoleById(key)).filter(Objects::nonNull).forEach(role -> builder.addField("Role " + role.getName(), "Ends the " + formatter.format(new Date(bans.get(role.getIdLong()))), false));
		}, () -> {
			builder.setColor(Color.GREEN);
			builder.setDescription("The user have no warns");
		});
		Actions.reply(event, builder.build());
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user]";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Warn info";
	}
	
	@Nonnull
	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public List<String> getCommandStrings(){
		return List.of("warninfo", "wi");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Gets information about the warns in progress";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
