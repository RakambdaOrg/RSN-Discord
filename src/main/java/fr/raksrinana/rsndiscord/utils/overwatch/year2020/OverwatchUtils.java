package fr.raksrinana.rsndiscord.utils.overwatch.year2020;

import fr.raksrinana.rsndiscord.utils.overwatch.year2019.OverwatchMap;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.ResponseContent;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.WeekData;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import lombok.NonNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class OverwatchUtils{
	private static final int MAP_DATA_TIMEOUT = 3600000;
	private static final int DATA_TIMEOUT = 120000;
	private static final Map<Integer, Long> lastChecks = new HashMap<>();
	private static final Map<Integer, Response> lastResponses = new HashMap<>();
	private static long lastCheckMaps = 0;
	private static Collection<OverwatchMap> lastMaps;
	
	@NonNull
	public static Optional<OverwatchMap> getMap(final String guid){
		if(Objects.isNull(lastMaps) || System.currentTimeMillis() - lastCheckMaps > MAP_DATA_TIMEOUT){
			lastCheckMaps = System.currentTimeMillis();
			final var handler = new ObjectGetRequestSender<>(new GenericType<List<OverwatchMap>>(){}, Unirest.get("https://api.overwatchleague.com/maps")).getRequestHandler();
			if(handler.getResult().isSuccess()){
				lastMaps = handler.getRequestResult();
			}
		}
		return Optional.ofNullable(lastMaps).stream().flatMap(Collection::stream).filter(map -> Objects.equals(map.getGuid(), guid)).findFirst();
	}
	
	public static Optional<WeekData> getCurrentStage(List<WeekData> weeksData){
		final var now = LocalDateTime.now();
		return weeksData.stream().filter(weekData -> now.isAfter(weekData.getStartDate()) && now.isBefore(weekData.getEndDate())).findAny();
	}
	
	public static Optional<WeekData> getNextStage(List<WeekData> weeksData){
		final var now = LocalDateTime.now();
		return weeksData.stream().filter(weekData -> now.isBefore(weekData.getStartDate())).min(Comparator.comparing(WeekData::getStartDate));
	}
	
	public static List<WeekData> getWeeksData(){
		final var data = new LinkedList<WeekData>();
		AtomicReference<Integer> nextPage = new AtomicReference<>(1);
		do{
			getData(nextPage.get()).map(Response::getContent).map(ResponseContent::getWeekData).ifPresentOrElse(weekData -> {
				data.add(weekData);
				nextPage.set(weekData.getPagination().getNextPage());
			}, () -> nextPage.set(null));
		}
		while(nextPage.get() != null);
		return data;
	}
	
	@NonNull
	public static Optional<Response> getData(int page){
		if(Objects.isNull(lastResponses.get(page)) || System.currentTimeMillis() - lastChecks.getOrDefault(page, 0L) > DATA_TIMEOUT){
			lastChecks.put(page, System.currentTimeMillis());
			final var handler = new ObjectGetRequestSender<>(new GenericType<Response>(){}, Unirest.get("https://api.overwatchleague.com/schedule").queryString("stage", "regular_season").queryString("page", page).queryString("season", 2020).queryString("locale", "en-us")).getRequestHandler();
			if(handler.getResult().isSuccess()){
				lastResponses.put(page, handler.getRequestResult());
			}
		}
		return Optional.ofNullable(lastResponses.get(page));
	}
}
