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
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The targeted user (default: @me)", false);
		builder.addField("Nickname", "The new surname (if none are provided, the old nickname will be removed)", false);
	}
	
	@SuppressWarnings("Duplicates")
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final Member member;
		if(event.getMessage().getMentionedUsers().size() > 0){
			args.pop();
			member = event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0));
			if(event.getAuthor().getIdLong() != member.getUser().getIdLong() && !Utilities.isTeam(event.getMember())){
				final var builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.addField("User", member.getAsMention(), true);
				builder.setTitle("You thought changing the name of another guy?");
				builder.setColor(Color.RED);
				Actions.reply(event, builder.build());
				return CommandResult.SUCCESS;
			}
		}
		else{
			member = event.getMember();
		}
		final var oldName = member.getNickname();
		final var lastChangeRaw = new NickLastChangeConfig(event.getGuild()).getValue(member.getUser().getIdLong());
		final var lastChange = new Date(lastChangeRaw == null ? 0 : lastChangeRaw);
		final var delay = Duration.ofMinutes(new NickDelayConfig(event.getGuild()).getObject(6 * 60));
		if(!Utilities.isTeam(event.getMember()) && (lastChange.getTime() + delay.getSeconds() * 1000) >= new Date().getTime()){
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.RED);
			builder.addField("Old nickname", oldName == null ? "*NONE*" : oldName, true);
			builder.addField("User", member.getAsMention(), true);
			builder.addField("Reason", "You can change your nickname once every " + delay.toString().replace("PT", ""), true);
			builder.addField("Last change", sdf.format(lastChange), true);
			builder.setTimestamp(new Date().toInstant());
			Actions.reply(event, builder.build());
		}
		else{
			final String newName;
			if(args.size() == 0){
				newName = null;
			}
			else{
				newName = String.join(" ", args);
			}
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.addField("Old nickname", oldName == null ? "*NONE*" : oldName, true);
			builder.addField("New nickname", newName == null ? "*NONE*" : newName, true);
			builder.addField("User", member.getAsMention(), true);
			try{
				member.getGuild().getController().setNickname(member, newName).complete();
				builder.setColor(Color.GREEN);
				new NickLastChangeConfig(event.getGuild()).addValue(member.getUser().getIdLong(), new Date().getTime());
				getLogger(event.getGuild()).info("{} renamed {} from `{}` to `{}`", event.getAuthor(), member.getUser(), oldName, newName);
			}
			catch(final HierarchyException e){
				builder.setColor(Color.RED);
				builder.setTitle("You thought I can change the nickname of someone higher than me?!");
			}
			catch(final ErrorResponseException e){
				builder.setColor(Color.RED);
				builder.setTitle("Invalid nickname");
				builder.addField("Reason", e.getMeaning(), false);
			}
			Actions.reply(event, builder.build());
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user] [nickname]";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Nickname";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("nickname", "nick");
	}
	
	@Override
	public String getDescription(){
		return "Change the nickname of a user";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
