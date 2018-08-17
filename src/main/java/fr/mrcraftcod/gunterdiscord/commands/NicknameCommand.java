package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.NickDelayConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.NickLastChangeConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class NicknameCommand extends BasicCommand{
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Optionnel: Utilisateur", "L'utilisateur visé par la modification (par défaut @me)", false);
		builder.addField("Optionnel: Surnom", "Le nouveau surnom (si aucun n'est précisé le surnom sera réinitialisé)", false);
	}
	
	@SuppressWarnings("Duplicates")
	@Override
	public CommandResult execute(@NotNull MessageReceivedEvent event, @NotNull LinkedList<String> args) throws Exception{
		super.execute(event, args);
		Member member;
		if(event.getMessage().getMentionedUsers().size() > 0){
			args.pop();
			member = event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0));
			if(event.getAuthor().getIdLong() != member.getUser().getIdLong() && !Utilities.isTeam(event.getMember())){
				EmbedBuilder builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.addField("Utilisateur", member.getAsMention(), true);
				builder.setTitle("T'es cru changer le nom d'un autre mec alors que t'es pas du staff?!");
				builder.setColor(Color.RED);
				Actions.reply(event, builder.build());
				return CommandResult.SUCCESS;
			}
		}
		else{
			member = event.getMember();
		}
		String oldName = member.getNickname();
		Long lastChangeRaw = new NickLastChangeConfig(event.getGuild()).getValue(member.getUser().getIdLong());
		Date lastChange = new Date(lastChangeRaw == null ? 0 : lastChangeRaw);
		Duration delay = Duration.ofMinutes(new NickDelayConfig(event.getGuild()).getObject(6 * 60));
		if(!Utilities.isTeam(event.getMember()) && (lastChange.getTime() + delay.getSeconds() * 1000) >= new Date().getTime()){
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.RED);
			builder.addField("Ancien surnom", oldName == null ? "*AUCUN*" : oldName, true);
			builder.addField("Utilisateur", member.getAsMention(), true);
			builder.addField("Raison", "Vous ne pouvez changer de nickname qu'une fois toutes les " + delay.toString().replace("PT", ""), true);
			builder.addField("Dernier changement", sdf.format(lastChange), true);
			builder.setTimestamp(new Date().toInstant());
			Actions.reply(event, builder.build());
		}
		else{
			String newName;
			if(args.size() == 0){
				newName = null;
			}
			else{
				newName = String.join(" ", args);
			}
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.addField("Ancien surnom", oldName == null ? "*AUCUN*" : oldName, true);
			builder.addField("Nouveau surnom", newName == null ? "*AUCUN*" : newName, true);
			builder.addField("Utilisateur", member.getAsMention(), true);
			try{
				member.getGuild().getController().setNickname(member, newName).complete();
				builder.setColor(Color.GREEN);
				new NickLastChangeConfig(event.getGuild()).addValue(member.getUser().getIdLong(), new Date().getTime());
				getLogger(event.getGuild()).info("{} renamed {} from `{}` to `{}`", Utilities.getUserToLog(event.getAuthor()), Utilities.getUserToLog(member.getUser()), oldName, newName);
			}
			catch(HierarchyException e){
				builder.setColor(Color.RED);
				builder.setTitle("T'as cru changer le nom d'un mec plus haut que moi?!");
			}
			catch(ErrorResponseException e){
				builder.setColor(Color.RED);
				builder.setTitle("Ce pseudo n'est pas valide");
				builder.addField("Raison", e.getMeaning(), false);
			}
			Actions.reply(event, builder.build());
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@utilisateur] [surnom]";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Surnom";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("nickname", "nick");
	}
	
	@Override
	public String getDescription(){
		return "Change le surnom d'un utilisateur";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
