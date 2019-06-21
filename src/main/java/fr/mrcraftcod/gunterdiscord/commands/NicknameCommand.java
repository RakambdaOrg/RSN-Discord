package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.NickDelayConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.NickLastChangeConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
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
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The targeted user (default: @me)", false);
		builder.addField("Nickname", "The new surname (if none are provided, the old nickname will be removed)", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final Optional<Member> memberOptional;
		if(!event.getMessage().getMentionedUsers().isEmpty()){
			args.pop();
			memberOptional = Optional.ofNullable(event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0)));
			if(memberOptional.isPresent() && Objects.equals(event.getAuthor(), memberOptional.get().getUser()) && !Utilities.isTeam(event.getMember())){
				final var builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.addField("User", memberOptional.get().getAsMention(), true);
				builder.setTitle("You thought changing the name of another guy?");
				builder.setColor(Color.RED);
				Actions.reply(event, builder.build());
				return CommandResult.SUCCESS;
			}
		}
		else{
			memberOptional = Optional.ofNullable(event.getMember());
		}
		memberOptional.ifPresentOrElse(member -> {
			final var oldName = Optional.ofNullable(member.getNickname());
			final var lastChangeRaw = new NickLastChangeConfig(event.getGuild()).getValue(member.getUser().getIdLong());
			final var lastChange = new Date(Objects.isNull(lastChangeRaw) ? 0 : lastChangeRaw);
			final var delay = Duration.ofMinutes(new NickDelayConfig(event.getGuild()).getObject(6 * 60));
			if(!Utilities.isTeam(event.getMember()) && (lastChange.getTime() + delay.getSeconds() * 1000) >= new Date().getTime()){
				final var builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.setColor(Color.RED);
				builder.addField("Old nickname", oldName.orElse("*NONE*"), true);
				builder.addField("User", member.getAsMention(), true);
				builder.addField("Reason", "You can change your nickname once every " + delay.toString().replace("PT", ""), true);
				builder.addField("Last change", sdf.format(lastChange), true);
				builder.setTimestamp(new Date().toInstant());
				Actions.reply(event, builder.build());
			}
			else{
				final String newName;
				if(args.isEmpty()){
					newName = null;
				}
				else{
					newName = String.join(" ", args);
				}
				final var builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.addField("Old nickname", oldName.orElse("*NONE*"), true);
				builder.addField("New nickname", Objects.isNull(newName) ? "*NONE*" : newName, true);
				builder.addField("User", member.getAsMention(), true);
				try{
					member.getGuild().modifyNickname(member, newName).complete();
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
		}, () -> Actions.reply(event, "Member not found"));
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user] [nickname]";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Nickname";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("nickname", "nick");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Change the nickname of a user";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
