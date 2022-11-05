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
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class AnilistWrappedTriggerTask extends WrappedTriggerTask{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	private final LocalizationService localizationService;
	
	protected AnilistWrappedTriggerTask(@NotNull JDA jda, @NotNull LocalizationService localizationService){
		super(jda);
		this.localizationService = localizationService;
	}
	
	protected void fillEmbed(@NotNull EmbedBuilder builder, @NotNull Media media, @NotNull DiscordLocale locale){
		builder.setDescription(media.getTitle().getRomaji());
		
		Optional.of(media.getType())
				.filter(MediaType::isShouldDisplay)
				.ifPresent(t -> builder.addField(localizationService.translate(locale, "anilist.type"), t.getValue(), true));
		
		Optional.ofNullable(media.getFormat())
				.map(MediaFormat::getValue)
				.ifPresent(f -> builder.addField(localizationService.translate(locale, "anilist.format"), f, true));
		
		Optional.ofNullable(media.getStatus())
				.map(MediaStatus::getValue)
				.ifPresent(f -> builder.addField(localizationService.translate(locale, "anilist.status"), f, true));
		
		Optional.of(media.isAdult())
				.filter(a -> a)
				.ifPresent(a -> builder.addField(localizationService.translate(locale, "anilist.adult"), "", true));
		
		if(media instanceof AnimeMedia animeMedia){
			fillTypeEmbed(builder, animeMedia, locale);
		}
		else if(media instanceof MangaMedia mangaMedia){
			fillTypeEmbed(builder, mangaMedia, locale);
		}
		
		media.getStartDate().asDate()
				.map(DF::format)
				.ifPresent(d -> builder.addField(localizationService.translate(locale, "anilist.started"), d, true));
		
		media.getEndDate().asDate()
				.map(DF::format)
				.ifPresent(d -> builder.addField(localizationService.translate(locale, "anilist.ended"), d, true));
		
		Optional.ofNullable(media.getRankings()).orElse(Set.of()).stream()
				.map(this::mapRanking)
				.forEach(ranking -> builder.addField("anilist.ranking", ranking, true));
		
		Optional.ofNullable(media.getGenres())
				.filter(g -> !g.isEmpty())
				.map(g -> String.join(", ", g))
				.ifPresent(g -> builder.addField(localizationService.translate(locale, "anilist.genres"), g, true));
		
		Optional.ofNullable(media.getSynonyms())
				.filter(s -> !s.isEmpty())
				.map(s -> String.join(", ", s))
				.ifPresent(s -> builder.addField(localizationService.translate(locale, "anilist.synonyms"), s, true));
		
		Optional.ofNullable(media.getSource())
				.ifPresent(source -> builder.addField("Source", source.toString(), true));
		
		builder.setThumbnail(media.getCoverImage().getLarge().toString())
				.setFooter("ID: " + getId());
	}
	
	@NotNull
	private String mapRanking(@NotNull MediaRank ranking){
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
	
	private void fillTypeEmbed(@NotNull EmbedBuilder builder, @NotNull AnimeMedia media, @NotNull DiscordLocale locale){
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
	
	private void fillTypeEmbed(@NotNull EmbedBuilder builder, @NotNull MangaMedia media, @NotNull DiscordLocale locale){
		Optional.ofNullable(media.getChapters())
				.map(Object::toString)
				.ifPresent(c -> builder.addField(localizationService.translate(locale, "anilist.chapters"), c, true));
		
		Optional.ofNullable(media.getVolumes())
				.map(Object::toString)
				.ifPresent(v -> builder.addField(localizationService.translate(locale, "anilist.volumes"), v, true));
	}
}
