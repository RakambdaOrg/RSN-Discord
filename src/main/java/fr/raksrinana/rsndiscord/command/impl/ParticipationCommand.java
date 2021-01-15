package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.participation.ChatParticipation;
import fr.raksrinana.rsndiscord.settings.guild.participation.VoiceParticipation;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.durationToString;
import static java.awt.Color.GREEN;
import static java.time.Duration.ofMinutes;
import static java.time.ZoneOffset.UTC;

@BotCommand
public class ParticipationCommand extends BasicCommand{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("date", translate(guild, "command.participation.help.date"), false);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.participation", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		var channel = event.getChannel();
		var maxUserCount = 25;
		var day = getArgumentAs(args, arg -> LocalDate.parse(arg, DATE_FORMATTER)).orElse(LocalDate.now(UTC));
		
		var participationConfiguration = Settings.get(guild).getParticipationConfiguration();
		participationConfiguration.getChatDay(day)
				.ifPresentOrElse(chatParticipation -> sendMessagesReport(maxUserCount, day, chatParticipation, author, channel),
						() -> channel.sendMessage(translate(guild, "participation.chat.no-data")).submit()
								.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		participationConfiguration.getVoiceDay(day)
				.ifPresentOrElse(voiceParticipation -> sendVoiceReport(maxUserCount, day, voiceParticipation, author, channel),
						() -> channel.sendMessage(translate(guild, "participation.voice.no-data")).submit()
								.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		return SUCCESS;
	}
	
	public static void sendMessagesReport(int maxUserCount, LocalDate day, ChatParticipation chatParticipation, User author, TextChannel channel){
		var guild = channel.getGuild();
		guild.loadMembers().onSuccess(members -> {
			var position = new AtomicInteger(0);
			var builder = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
					.setColor(GREEN)
					.setTitle(translate(guild, "participation.chat.title", day.format(DATE_FORMATTER)));
			chatParticipation.getUserCounts().entrySet().stream()
					.sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
					.limit(maxUserCount)
					.forEachOrdered(entry -> {
						var userMention = Optional.ofNullable(guild.getMemberById(entry.getKey()))
								.map(Member::getAsMention)
								.orElse(Long.toString(entry.getKey()));
						builder.addField(translate(guild, "participation.chat.entry", position.incrementAndGet(), entry.getValue()), userMention, false);
					});
			channel.sendMessage(builder.build()).submit();
		});
	}
	
	public static void sendVoiceReport(int maxUserCount, LocalDate day, VoiceParticipation voiceParticipation, User author, TextChannel channel){
		var guild = channel.getGuild();
		guild.loadMembers().onSuccess(members -> {
			var position = new AtomicInteger(0);
			var builder = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
					.setColor(GREEN)
					.setTitle(translate(guild, "participation.voice.title", day.format(DATE_FORMATTER)));
			voiceParticipation.getUserCounts().entrySet().stream()
					.sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
					.limit(maxUserCount)
					.forEachOrdered(entry -> {
						var userMention = Optional.ofNullable(guild.getMemberById(entry.getKey()))
								.map(Member::getAsMention)
								.orElse(Long.toString(entry.getKey()));
						var title = translate(guild, "participation.voice.entry",
								position.incrementAndGet(),
								durationToString(ofMinutes(entry.getValue())));
						builder.addField(title, userMention, false);
					});
			channel.sendMessage(builder.build()).submit();
		});
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [date]";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.participation.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("participation");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.participation.description");
	}
}
