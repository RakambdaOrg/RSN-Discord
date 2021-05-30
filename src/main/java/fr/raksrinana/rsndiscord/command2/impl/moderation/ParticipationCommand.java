package fr.raksrinana.rsndiscord.command2.impl.moderation;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.participation.ChatParticipation;
import fr.raksrinana.rsndiscord.settings.guild.participation.VoiceParticipation;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.durationToString;
import static java.awt.Color.GREEN;
import static java.time.Duration.ofMinutes;
import static java.time.ZoneOffset.UTC;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class ParticipationCommand extends SubCommand{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final String DATE_OPTION_ID = "date";
	
	@Override
	@NotNull
	public String getId(){
		return "participation";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Get the participation of a day";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(STRING, DATE_OPTION_ID, "Date to get (format: yyyy-MM-dd, default: today)"));
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var maxUserCount = 25;
		var day = getOptionAs(event.getOption(DATE_OPTION_ID), arg -> LocalDate.parse(arg, DATE_FORMATTER)).orElse(LocalDate.now(UTC));
		
		var participationConfiguration = Settings.get(guild).getParticipationConfiguration();
		participationConfiguration.getChatDay(day)
				.ifPresentOrElse(chatParticipation -> sendMessagesReport(guild, maxUserCount, day, chatParticipation, embed -> JDAWrappers.replyCommand(event, embed).submit()),
						() -> JDAWrappers.replyCommand(event, translate(guild, "participation.chat.no-data")).submit());
		participationConfiguration.getVoiceDay(day)
				.ifPresentOrElse(voiceParticipation -> sendVoiceReport(guild, maxUserCount, day, voiceParticipation, embed -> JDAWrappers.replyCommand(event, embed).submit()),
						() -> JDAWrappers.replyCommand(event, translate(guild, "participation.voice.no-data")).submit());
		return SUCCESS;
	}
	
	public static void sendMessagesReport(@NotNull Guild guild, int maxUserCount, @NotNull LocalDate day, @NotNull ChatParticipation chatParticipation, @NotNull Consumer<MessageEmbed> sendMessage){
		guild.loadMembers().onSuccess(members -> {
			var position = new AtomicInteger(0);
			var builder = new EmbedBuilder().setColor(GREEN)
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
			sendMessage.accept(builder.build());
		});
	}
	
	public static void sendVoiceReport(@NotNull Guild guild, int maxUserCount, @NotNull LocalDate day, @NotNull VoiceParticipation voiceParticipation, @NotNull Consumer<MessageEmbed> sendMessage){
		guild.loadMembers().onSuccess(members -> {
			var position = new AtomicInteger(0);
			var builder = new EmbedBuilder().setColor(GREEN)
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
			sendMessage.accept(builder.build());
		});
	}
}
