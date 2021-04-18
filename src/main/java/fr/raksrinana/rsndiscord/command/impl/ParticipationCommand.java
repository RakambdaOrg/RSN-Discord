package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.participation.ChatParticipation;
import fr.raksrinana.rsndiscord.settings.guild.participation.VoiceParticipation;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
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
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("date", translate(guild, "command.participation.help.date"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		var channel = event.getChannel();
		var maxUserCount = 25;
		var day = getArgumentAs(args, arg -> LocalDate.parse(arg, DATE_FORMATTER)).orElse(LocalDate.now(UTC));
		
		var participationConfiguration = Settings.get(guild).getParticipationConfiguration();
		participationConfiguration.getChatDay(day)
				.ifPresentOrElse(chatParticipation -> sendMessagesReport(maxUserCount, day, chatParticipation, author, channel),
						() -> JDAWrappers.message(channel, translate(guild, "participation.chat.no-data")).submit()
								.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		participationConfiguration.getVoiceDay(day)
				.ifPresentOrElse(voiceParticipation -> sendVoiceReport(maxUserCount, day, voiceParticipation, author, channel),
						() -> JDAWrappers.message(channel, translate(guild, "participation.voice.no-data")).submit()
								.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		return SUCCESS;
	}
	
	public static void sendMessagesReport(int maxUserCount, @NotNull LocalDate day, @NotNull ChatParticipation chatParticipation, @NotNull User author, @NotNull TextChannel channel){
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
			JDAWrappers.message(channel, builder.build()).submit();
		});
	}
	
	public static void sendVoiceReport(int maxUserCount, @NotNull LocalDate day, @NotNull VoiceParticipation voiceParticipation, @NotNull User author, @NotNull TextChannel channel){
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
			JDAWrappers.message(channel, builder.build()).submit();
		});
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [date]";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.participation", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.participation.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.participation.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("participation");
	}
}
