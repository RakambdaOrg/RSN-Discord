package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.durationToString;
import static fr.raksrinana.rsndiscord.utils.Utilities.isModerator;
import static java.awt.Color.*;

@BotCommand
public class NicknameCommand extends BasicCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.nickname.help.user"), false)
				.addField("nickname", translate(guild, "command.nickname.help.nickname"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		var target = getFirstMemberMentioned(event)
				.map(member -> {
					args.poll();
					return member;
				})
				.orElse(event.getMember());
		boolean bypass = isModerator(event.getMember());
		
		var oldNickname = Optional.ofNullable(target.getNickname());
		var newNickname = args.isEmpty() ? Optional.<String> empty() : Optional.of(String.join(" ", args));
		
		var builder = new EmbedBuilder()
				.setAuthor(author.getName(), null, author.getAvatarUrl())
				.addField(translate(guild, "nickname.user"), target.getAsMention(), true)
				.addField(translate(guild, "nickname.old-nick"), oldNickname.orElseGet(() -> translate(guild, "nickname.unknown")), true)
				.addField(translate(guild, "nickname.new-nick"), newNickname.orElseGet(() -> translate(guild, "nickname.unknown")), true)
				.setTimestamp(ZonedDateTime.now());
		
		if(!bypass && !Objects.equals(target.getIdLong(), author.getIdLong())){
			builder.setTitle(translate(guild, "nickname.error.permission-other"))
					.setColor(RED);
			JDAWrappers.message(event, builder.build()).submit()
					.thenAccept(deleteMessage(date -> date.plusDays(1)));
			return SUCCESS;
		}
		
		var nicknameConfiguration = Settings.get(guild).getNicknameConfiguration();
		var lastChange = nicknameConfiguration.getLastChange(target.getUser());
		var delay = Duration.ofSeconds(nicknameConfiguration.getChangeDelay());
		
		if(newNickname.equals(oldNickname)){
			builder.setColor(ORANGE)
					.setTitle(translate(guild, "nickname.no-changes"))
					.addField(translate(guild, "nickname.reason"), translate(guild, "nickname.same-nickname"), false);
			JDAWrappers.message(event, builder.build()).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(10)));
			return SUCCESS;
		}
		
		var isDelayDone = lastChange.map(date -> date.plus(delay))
				.map(date -> date.isAfter(ZonedDateTime.now()))
				.orElse(false);
		if(newNickname.isPresent() && !bypass && isDelayDone){
			var lastChangeStr = lastChange.map(date -> date.format(DF))
					.orElseGet(() -> translate(guild, "nickname.unknown"));
			var nextChange = lastChange.map(date -> date.plus(delay))
					.map(date -> date.format(DF))
					.orElseGet(() -> translate(guild, "nickname.unknown"));
			
			builder.setColor(RED)
					.addField(translate(guild, "nickname.reason"), translate(guild, "nickname.cooldown", durationToString(delay)), true)
					.addField(translate(guild, "nickname.last-change"), lastChangeStr, true)
					.addField(translate(guild, "nickname.next-allowed"), nextChange, true);
			JDAWrappers.message(event, builder.build()).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(10)));
			return SUCCESS;
		}
		
		try{
			JDAWrappers.modifyNickname(target, newNickname.orElse(null)).submit()
					.thenAccept(empty -> {
						Log.getLogger(guild).info("{} renamed {} from `{}` to `{}`", author, target.getUser(), oldNickname, newNickname);
						
						var newLastChange = ZonedDateTime.now();
						nicknameConfiguration.setLastChange(target.getUser(), newLastChange);
						
						builder.setColor(GREEN)
								.addField(translate(guild, "nickname.next-allowed"), newLastChange.plus(delay).format(DF), false);
						
						JDAWrappers.message(event, builder.build()).submit();
					})
					.exceptionally(error -> {
						var errorMessage = error instanceof ErrorResponseException ? ((ErrorResponseException) error).getMeaning() : error.getMessage();
						builder.setColor(RED)
								.setTitle(translate(guild, "nickname.invalid"))
								.addField(translate(guild, "nickname.reason"), errorMessage, false);
						JDAWrappers.message(event, builder.build()).submit()
								.thenAccept(deleteMessage(date -> date.plusMinutes(10)));
						return null;
					});
		}
		catch(HierarchyException e){
			builder.setColor(RED)
					.addField(translate(guild, "nickname.reason"), translate(guild, "nickname.target-error"), false);
			JDAWrappers.message(event, builder.build()).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(10)));
		}
		return SUCCESS;
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user] [nickname]";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.nickname", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.nickname.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.nickname.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("nickname", "nick");
	}
}
