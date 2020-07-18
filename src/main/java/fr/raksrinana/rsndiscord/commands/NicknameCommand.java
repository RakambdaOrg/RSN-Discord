package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import java.awt.Color;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class NicknameCommand extends BasicCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.nickname.help.user"), false);
		builder.addField("nickname", translate(guild, "command.nickname.help.nickname"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final Optional<Member> memberOptional;
		if(event.getMessage().getMentionedUsers().isEmpty()){
			memberOptional = Optional.ofNullable(event.getMember());
		}
		else{
			args.pop();
			memberOptional = Optional.ofNullable(event.getGuild().retrieveMember(event.getMessage().getMentionedUsers().get(0)).complete());
			if(memberOptional.isPresent() && !Objects.equals(event.getAuthor(), memberOptional.get().getUser()) && !Utilities.isTeam(event.getMember())){
				final var builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.addField(translate(event.getGuild(), "nickname.user"), memberOptional.get().getAsMention(), true);
				builder.setTitle(translate(event.getGuild(), "nickname.error.permission-other"));
				builder.setColor(Color.RED);
				Actions.sendEmbed(event.getChannel(), builder.build());
				return CommandResult.SUCCESS;
			}
		}
		memberOptional.ifPresentOrElse(member -> {
			final var oldName = Optional.ofNullable(member.getNickname());
			final var lastChange = Settings.get(event.getGuild()).getNicknameConfiguration().getLastChange(member.getUser());
			final var delay = Duration.ofSeconds(Settings.get(event.getGuild()).getNicknameConfiguration().getChangeDelay());
			final String newName;
			if(args.isEmpty()){
				newName = null;
			}
			else{
				newName = String.join(" ", args);
			}
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			if(Objects.equals(newName, oldName.orElse(null))){
				builder.setColor(Color.ORANGE);
				builder.setTitle(translate(event.getGuild(), "nickname.no-changes"));
				builder.addField(translate(event.getGuild(), "nickname.reason"), translate(event.getGuild(), "nickname.same-nickname"), false);
				Actions.sendEmbed(event.getChannel(), builder.build());
			}
			else if(Objects.nonNull(newName) && !Utilities.isTeam(event.getMember()) && lastChange.map(date -> date.plus(delay)).map(date -> date.isAfter(ZonedDateTime.now())).orElse(false)){
				builder.setColor(Color.RED);
				builder.addField(translate(event.getGuild(), "nickname.old-nick"), oldName.orElseGet(() -> translate(event.getGuild(), "nickname.unknown")), true);
				builder.addField(translate(event.getGuild(), "nickname.user"), member.getAsMention(), true);
				builder.addField(translate(event.getGuild(), "nickname.reason"), translate(event.getGuild(), "nickname.cooldown", Utilities.durationToString(delay)), true);
				builder.addField(translate(event.getGuild(), "nickname.last-change"), lastChange.map(date -> date.format(DF)).orElseGet(() -> translate(event.getGuild(), "nickname.unknown")), true);
				builder.addField(translate(event.getGuild(), "nickname.next-allowed"), lastChange.map(date -> date.plus(delay)).map(date -> date.format(DF)).orElseGet(() -> translate(event.getGuild(), "nickname.unknown")), true);
				builder.setTimestamp(ZonedDateTime.now());
				Actions.sendEmbed(event.getChannel(), builder.build());
			}
			else{
				builder.addField(translate(event.getGuild(), "nickname.old-nick"), oldName.orElseGet(() -> translate(event.getGuild(), "nickname.unknown")), true);
				builder.addField(translate(event.getGuild(), "nickname.new-nick"), Objects.isNull(newName) ? translate(event.getGuild(), "nickname.unknown") : newName, true);
				builder.addField(translate(event.getGuild(), "nickname.user"), member.getAsMention(), true);
				Actions.changeNickname(member, newName)
						.thenAccept(empty -> {
							builder.setColor(Color.GREEN);
							Settings.get(event.getGuild()).getNicknameConfiguration().setLastChange(member.getUser(), Objects.isNull(newName) ? null : ZonedDateTime.now());
							Settings.get(event.getGuild()).getNicknameConfiguration().getLastChange(member.getUser()).map(d -> d.plus(delay)).filter(d -> d.isAfter(ZonedDateTime.now())).ifPresent(d -> builder.addField(translate(event.getGuild(), "nickname.next-allowed"), d.format(DF), false));
							Log.getLogger(event.getGuild()).info("{} renamed {} from `{}` to `{}`", event.getAuthor(), member.getUser(), oldName, newName);
						})
						.exceptionally(exception -> {
							if(exception instanceof HierarchyException){
								builder.setColor(Color.RED);
								builder.setTitle(translate(event.getGuild(), "nickname.target-error"));
							}
							else if(exception instanceof ErrorResponseException){
								builder.setColor(Color.RED);
								builder.setTitle(translate(event.getGuild(), "nickname.invalid"));
								builder.addField(translate(event.getGuild(), "nickname.reason"), ((ErrorResponseException) exception).getMeaning(), false);
							}
							return null;
						}).thenAccept(empty -> Actions.sendEmbed(event.getChannel(), builder.build()));
			}
		}, () -> Actions.reply(event, translate(event.getGuild(), "nickname.target-not-found"), null));
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user] [nickname]";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.nickname.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("nickname", "nick");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.nickname.description");
	}
}
