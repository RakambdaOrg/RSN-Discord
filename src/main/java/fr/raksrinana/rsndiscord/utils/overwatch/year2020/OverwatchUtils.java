package fr.raksrinana.rsndiscord.utils.overwatch.year2020;

import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
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
	private static final int DATA_TIMEOUT = 120000;
	private static final Map<Integer, Long> lastChecks = new HashMap<>();
	private static final Map<Integer, Response> lastResponses = new HashMap<>();
	
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
			Log.getLogger(null).debug("Fetching overwatch league page {}", page);
			lastChecks.put(page, System.currentTimeMillis());
			final var handler = new ObjectGetRequestSender<>(new GenericType<Response>(){}, Unirest.get("https://wzavfvwgfk.execute-api.us-east-2.amazonaws.com/production/owl/paginator/schedule").queryString("stage", "regular_season").queryString("page", page).queryString("season", 2020).queryString("locale", "en-us").header("Referer", "https://overwatchleague.com/en-us/schedule?stage=regular_season&week=1")).getRequestHandler();
			handler.getResult().getParsingError().ifPresent(error -> {
				Actions.sendPrivateMessage(Utilities.RAKSRINANA_ACCOUNT, "Failed to parse Overwatch league response (page " + page + ")", Utilities.throwableToEmbed(error).build());
				Log.getLogger(null).warn("Failed to parse Overwatch league response for page {}", page, error);
			});
			if(handler.getResult().isSuccess()){
				lastResponses.put(page, handler.getRequestResult());
			}
		}
		return Optional.ofNullable(lastResponses.get(page));
	}
}
