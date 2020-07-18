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
		final var target = event.getMessage()
				.getMentionedMembers()
				.stream()
				.findFirst()
				.map(member -> {
					args.poll();
					return member;
				})
				.orElse(event.getMember());
		
		boolean isBypass = Utilities.isTeam(event.getMember());
		final var oldNickname = target.getNickname();
		final var newNickname = args.isEmpty() ? null : String.join(" ", args);
		
		final var builder = new EmbedBuilder();
		builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
		builder.addField(translate(event.getGuild(), "nickname.user"), target.getAsMention(), true);
		builder.addField(translate(event.getGuild(), "nickname.old-nick"), Optional.ofNullable(oldNickname).orElseGet(() -> translate(event.getGuild(), "nickname.unknown")), true);
		builder.addField(translate(event.getGuild(), "nickname.new-nick"), Optional.ofNullable(newNickname).orElseGet(() -> translate(event.getGuild(), "nickname.unknown")), true);
		builder.setTimestamp(ZonedDateTime.now());
		
		if(!isBypass && !Objects.equals(target.getIdLong(), event.getAuthor().getIdLong())){
			builder.setTitle(translate(event.getGuild(), "nickname.error.permission-other"));
			builder.setColor(Color.RED);
			Actions.sendEmbed(event.getChannel(), builder.build());
			return CommandResult.SUCCESS;
		}
		
		final var nicknameConfiguration = Settings.get(event.getGuild()).getNicknameConfiguration();
		final var lastChange = nicknameConfiguration.getLastChange(target.getUser());
		final var delay = Duration.ofSeconds(nicknameConfiguration.getChangeDelay());
		
		if(Objects.equals(newNickname, oldNickname)){
			builder.setColor(Color.ORANGE);
			builder.setTitle(translate(event.getGuild(), "nickname.no-changes"));
			builder.addField(translate(event.getGuild(), "nickname.reason"), translate(event.getGuild(), "nickname.same-nickname"), false);
			Actions.sendEmbed(event.getChannel(), builder.build());
			return CommandResult.SUCCESS;
		}
		
		if(Objects.nonNull(newNickname) && !isBypass && lastChange.map(date -> date.plus(delay)).map(date -> date.isAfter(ZonedDateTime.now())).orElse(false)){
			builder.setColor(Color.RED);
			builder.addField(translate(event.getGuild(), "nickname.reason"), translate(event.getGuild(), "nickname.cooldown", Utilities.durationToString(delay)), true);
			builder.addField(translate(event.getGuild(), "nickname.last-change"), lastChange.map(date -> date.format(DF)).orElseGet(() -> translate(event.getGuild(), "nickname.unknown")), true);
			builder.addField(translate(event.getGuild(), "nickname.next-allowed"), lastChange.map(date -> date.plus(delay)).map(date -> date.format(DF)).orElseGet(() -> translate(event.getGuild(), "nickname.unknown")), true);
			Actions.sendEmbed(event.getChannel(), builder.build());
			return CommandResult.SUCCESS;
		}
		
		try{
			Actions.changeNickname(target, newNickname)
					.thenAccept(empty -> {
						builder.setColor(Color.GREEN);
						nicknameConfiguration.setLastChange(target.getUser(), Objects.isNull(newNickname) ? null : ZonedDateTime.now());
						nicknameConfiguration.getLastChange(target.getUser())
								.map(d -> d.plus(delay))
								.filter(d -> d.isAfter(ZonedDateTime.now()))
								.ifPresent(d -> builder.addField(translate(event.getGuild(), "nickname.next-allowed"), d.format(DF), false));
						Actions.sendEmbed(event.getChannel(), builder.build());
						Log.getLogger(event.getGuild()).info("{} renamed {} from `{}` to `{}`", event.getAuthor(), target.getUser(), oldNickname, newNickname);
					})
					.exceptionally(e -> {
						builder.setColor(Color.RED);
						builder.setTitle(translate(event.getGuild(), "nickname.invalid"));
						builder.addField(translate(event.getGuild(), "nickname.reason"), e instanceof ErrorResponseException ? ((ErrorResponseException) e).getMeaning() : e.getMessage(), false);
						Actions.sendEmbed(event.getChannel(), builder.build());
						return null;
					});
		}
		catch(final HierarchyException e){
			builder.setColor(Color.RED);
			builder.setTitle(translate(event.getGuild(), "nickname.target-error"));
			Actions.sendEmbed(event.getChannel(), builder.build());
		}
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
