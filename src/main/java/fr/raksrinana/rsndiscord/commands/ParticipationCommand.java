package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.participation.ChatParticipation;
import fr.raksrinana.rsndiscord.settings.guild.participation.VoiceParticipation;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.time.ZoneOffset.UTC;

@BotCommand
public class ParticipationCommand extends BasicCommand{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("date", translate(guild, "command.participation.help.date"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var maxUserCount = 25;
		final var day = Optional.ofNullable(args.poll()).map(arg -> LocalDate.parse(arg, DATE_FORMATTER)).orElse(LocalDate.now(UTC));
		var participationConfiguration = Settings.get(event.getGuild()).getParticipationConfiguration();
		participationConfiguration.getChatDay(day).ifPresentOrElse(chatParticipation -> sendMessagesReport(maxUserCount, day, chatParticipation, event.getAuthor(), event.getChannel()), () -> Actions.reply(event, translate(event.getGuild(), "participation.chat.no-data"), null));
		participationConfiguration.getVoiceDay(day).ifPresentOrElse(voiceParticipation -> sendVoiceReport(maxUserCount, day, voiceParticipation, event.getAuthor(), event.getChannel()), () -> Actions.reply(event, translate(event.getGuild(), "participation.voice.no-data"), null));
		return CommandResult.SUCCESS;
	}
	
	public static void sendMessagesReport(int maxUserCount, LocalDate day, ChatParticipation chatParticipation, User author, TextChannel channel){
		channel.getGuild().retrieveMembers().thenAccept(empty -> {
			final var position = new AtomicInteger(0);
			final var embed = Utilities.buildEmbed(author, Color.GREEN, translate(channel.getGuild(), "participation.chat.title", day.format(DATE_FORMATTER)), null);
			chatParticipation.getUserCounts().entrySet().stream().sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())).limit(maxUserCount).forEachOrdered(entry -> embed.addField(translate(channel.getGuild(), "participation.chat.entry", position.incrementAndGet(), entry.getValue()), Optional.ofNullable(channel.getGuild().getMemberById(entry.getKey())).map(Member::getAsMention).orElse(Long.toString(entry.getKey())), false));
			Actions.sendMessage(channel, "", embed.build());
		});
	}
	
	public static void sendVoiceReport(int maxUserCount, LocalDate day, VoiceParticipation voiceParticipation, User author, TextChannel channel){
		channel.getGuild().retrieveMembers().thenAccept(empty -> {
			final var position = new AtomicInteger(0);
			final var embed = Utilities.buildEmbed(author, Color.GREEN, translate(channel.getGuild(), "participation.voice.title", day.format(DATE_FORMATTER)), null);
			voiceParticipation.getUserCounts().entrySet().stream().sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())).limit(maxUserCount).forEachOrdered(entry -> embed.addField(translate(channel.getGuild(), "participation.voice.entry", position.incrementAndGet(), Utilities.durationToString(Duration.ofMinutes(entry.getValue()))), Optional.ofNullable(channel.getGuild().getMemberById(entry.getKey())).map(Member::getAsMention).orElse(Long.toString(entry.getKey())), false));
			Actions.sendMessage(channel, "", embed.build());
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
	
	@Override
	public @NonNull AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
}
