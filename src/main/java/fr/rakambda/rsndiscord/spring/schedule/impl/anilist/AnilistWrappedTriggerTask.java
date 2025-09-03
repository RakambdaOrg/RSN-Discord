package fr.rakambda.rsndiscord.spring.schedule.impl.anilist;

import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.FuzzyDate;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.AnimeMedia;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.MangaMedia;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.Media;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.MediaFormat;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.MediaRank;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.MediaStatus;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.MediaType;
import fr.rakambda.rsndiscord.spring.schedule.WrappedTriggerTask;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jspecify.annotations.NonNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AnilistWrappedTriggerTask extends WrappedTriggerTask{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	private final LocalizationService localizationService;
	
	protected AnilistWrappedTriggerTask(@NonNull JDA jda, @NonNull LocalizationService localizationService){
		super(jda);
		this.localizationService = localizationService;
	}
	
	protected void fillEmbed(@NonNull EmbedBuilder builder, @NonNull Media media, @NonNull DiscordLocale locale){
		builder.setDescription(media.getTitle().getRomaji());
		
		var type = Stream.of(
						Optional.of(media.getType()).filter(MediaType::isShouldDisplay).map(MediaType::getValue),
						Optional.ofNullable(media.getFormat()).map(MediaFormat::getValue),
						Optional.ofNullable(media.getStatus()).map(MediaStatus::getValue),
						Optional.of(media.isAdult()).filter(a -> a).map(a -> "\uD83D\uDD1E Adult")
				)
				.flatMap(Optional::stream)
				.collect(Collectors.joining(" - "));
		
		builder.addField(localizationService.translate(locale, "anilist.type"), type, true);
		
		if(media instanceof AnimeMedia animeMedia){
			fillTypeEmbed(builder, animeMedia, locale);
		}
		else if(media instanceof MangaMedia mangaMedia){
			fillTypeEmbed(builder, mangaMedia, locale);
		}
		
		var dates = Stream.of(
						media.getStartDate().asDate().map(DF::format).map("Start: %s"::formatted),
						media.getEndDate().asDate().map(DF::format).map("End: %s"::formatted)
				)
				.flatMap(Optional::stream)
				.collect(Collectors.joining("\n"));
		builder.addField(localizationService.translate(locale, "anilist.started"), dates, true);
		
		var rankings = Optional.ofNullable(media.getRankings()).orElse(Set.of());
		if(!rankings.isEmpty()){
			var rankingsStr = rankings.stream()
					.map(this::mapRanking)
					.collect(Collectors.joining("\n"));
			builder.addField(localizationService.translate(locale, "anilist.ranking"), rankingsStr, true);
		}
		
		Optional.ofNullable(media.getGenres())
				.filter(g -> !g.isEmpty())
				.map(g -> String.join(", ", g))
				.ifPresent(g -> builder.addField(localizationService.translate(locale, "anilist.genres"), g, true));
		
		Optional.ofNullable(media.getTitle().getEnglish())
				.ifPresent(s -> builder.addField("English title", s, true));
		
		Optional.ofNullable(media.getSynonyms())
				.filter(s -> !s.isEmpty())
				.map(s -> String.join(", ", s))
				.ifPresent(s -> builder.addField(localizationService.translate(locale, "anilist.synonyms"), s, true));
		
		Optional.ofNullable(media.getSource())
				.ifPresent(source -> builder.addField("Source", source.getValue(), true));
		
		builder.setThumbnail(media.getCoverImage().getLarge().toString())
				.setFooter("ID: " + media.getId());
	}
	
	@NonNull
	private String mapRanking(@NonNull MediaRank ranking){
		var sb = new StringBuilder(ranking.getType().getIcon())
				.append(" ")
				.append("#")
				.append(ranking.getRank())
				.append(" ")
				.append(ranking.getContext());
		
		if(Objects.nonNull(ranking.getSeason())){
			sb.append(" ").append(ranking.getSeason());
		}
		
		if(Objects.nonNull(ranking.getYear())){
			sb.append(" ").append(ranking.getYear());
		}
		
		return sb.toString();
	}
	
	private void fillTypeEmbed(@NonNull EmbedBuilder builder, @NonNull AnimeMedia media, @NonNull DiscordLocale locale){
		var year = Optional.ofNullable(media.getStartDate())
				.flatMap(FuzzyDate::asDate)
				.map(LocalDate::getYear);
		
		Optional.ofNullable(media.getEpisodes())
				.map(Object::toString)
				.ifPresent(e -> builder.addField(localizationService.translate(locale, "anilist.episodes"), e, true));
		
		Optional.ofNullable(media.getSeason())
				.map(Enum::toString)
				.map(v -> year.map(y -> "%s %d".formatted(v, y)).orElse(v))
				.ifPresent(s -> builder.addField(localizationService.translate(locale, "anilist.season"), s, true));
	}
	
	private void fillTypeEmbed(@NonNull EmbedBuilder builder, @NonNull MangaMedia media, @NonNull DiscordLocale locale){
		Optional.ofNullable(media.getChapters())
				.map(Object::toString)
				.ifPresent(c -> builder.addField(localizationService.translate(locale, "anilist.chapters"), c, true));
		
		Optional.ofNullable(media.getVolumes())
				.map(Object::toString)
				.ifPresent(v -> builder.addField(localizationService.translate(locale, "anilist.volumes"), v, true));
	}
}
