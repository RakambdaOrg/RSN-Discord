package fr.mrcraftcod.gunterdiscord.commands.warn;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.RemoveRoleConfig;
import fr.mrcraftcod.gunterdiscord.settings.configurations.DoubleValueConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.configurations.SingleRoleConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.util.LinkedList;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public abstract class WarnCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Utilisateur", "L'utilisateur à warn", false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(event.getMessage().getMentionedUsers().size() > 0){
			final var user = event.getMessage().getMentionedUsers().get(0);
			final var role = getRoleConfig(event.getGuild()).getObject();
			final var builder = new EmbedBuilder();
			builder.setAuthor(user.getName(), null, user.getAvatarUrl());
			if(role == null){
				builder.setColor(Color.RED);
				builder.addField("Erreur", "Merci de configurer le role à donner", true);
			}
			else{
				final double duration = getTimeConfig(event.getGuild()).getObject(1D);
				Actions.giveRole(event.getGuild(), user, role);
				new RemoveRoleConfig(event.getGuild()).addValue(user.getIdLong(), role.getIdLong(), (long) (System.currentTimeMillis() + duration * 24 * 60 * 60 * 1000L));
				builder.setColor(Color.GREEN);
				builder.addField("Congratulations", user.getAsMention() + " à rejoint le role " + role.getAsMention() + " pour une durée de " + duration + " jour(s)", false);
				builder.addField("", "Pour savoir où en est ton ban retiens la formule magique: g?muteinfo " + user.getAsMention(), false);
				getLogger(event.getGuild()).info("{} warned {} for {} days with role {}", Utilities.getUserToLog(event.getAuthor()), Utilities.getUserToLog(user), duration, role);
			}
			Actions.reply(event, builder.build());
		}
		else{
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.RED);
			builder.addField("Erreur", "Merci de mentionner un utilisateur a warn", true);
			Actions.reply(event, builder.build());
		}
		return CommandResult.SUCCESS;
	}
	
	/**
	 * Get the configuration of the role to apply.
	 *
	 * @param guild The guild of the event.
	 * @return The config.
	 */
	protected abstract SingleRoleConfiguration getRoleConfig(Guild guild);
	
	/**
	 * Get the configuration of the length for the role to be applied.
	 *
	 * @param guild The guild of the event.
	 * @return The config.
	 */
	protected abstract DoubleValueConfiguration getTimeConfig(Guild guild);
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@utilisateur>";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@Override
	public String getDescription(){
		return "Warn un utilisateur (en donnant un role) pendant un temps préconfiguré";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
