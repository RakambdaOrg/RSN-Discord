package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.RemoveRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
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
public class BanInfoCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("utilisateur", "L'utilisateur ciblé (défaut: @me)", false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		var target = event.getMessage().getMentionedUsers().stream().findFirst().orElse(event.getAuthor());
		var builder = Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, "Information des bans pour " + target.getAsMention());
		var bans = new RemoveRoleConfig(event.getGuild()).getValue(target.getIdLong());
		if(bans == null || bans.isEmpty()){
			builder.setColor(Color.GREEN);
			builder.setDescription("Aucun ban n'est présent");
		}
		else{
			var formatter = new SimpleDateFormat("dd MMM à HH:mm:ssZ");
			builder.setDescription("Les bans seront retirés avec une marge de 15 minutes maximum");
			bans.keySet().forEach(key -> builder.addField("Role " + event.getGuild().getRoleById(key).getName(), "Fin le " + formatter.format(new Date(bans.get(key))), false));
		}
		Actions.reply(event, builder.build());
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@utilisateur]";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Ban info";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("baninfo", "bi");
	}
	
	@Override
	public String getDescription(){
		return "Obtient des informations sur les bans en cours";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
