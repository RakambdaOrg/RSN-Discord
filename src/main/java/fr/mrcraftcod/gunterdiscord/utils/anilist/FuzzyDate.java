package fr.mrcraftcod.gunterdiscord.utils.anilist;

import fr.mrcraftcod.gunterdiscord.utils.GunterDuration;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import org.json.JSONObject;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-12.
 *
 * @author Thomas Couchoud
 * @since 2018-10-12
 */
public class FuzzyDate{
	
	private final Calendar calendar;
	
	private FuzzyDate(){calendar = Calendar.getInstance();}
	
	public static FuzzyDate buildFromJSON(final JSONObject json, final String key){
		if(!json.has(key)){
			return null;
		}
		final var date = json.getJSONObject(key);
		final var fuzzyDate = new FuzzyDate();
		try{
			Integer month = Utilities.getJSONMaybe(date, Integer.class, "month");
			fuzzyDate.setYear(Utilities.getJSONMaybe(date, Integer.class, "year"));
			fuzzyDate.setMonth(month == null ? null : month - 1);
			fuzzyDate.setDay(Utilities.getJSONMaybe(date, Integer.class, "day"));
		}
		catch(final IllegalArgumentException e){
			return null;
		}
		return fuzzyDate;
	}
	
	public GunterDuration durationTo(FuzzyDate toDate){
		return new GunterDuration(Duration.between(this.asDate().toInstant(), toDate.asDate().toInstant()));
	}
	
	private void setDay(final Integer day){
		if(Objects.nonNull(day)){
			calendar.set(Calendar.DAY_OF_MONTH, day);
		}
		else{
			throw new IllegalArgumentException("Integer is null");
		}
	}
	
	private void setMonth(final Integer month){
		if(Objects.nonNull(month)){
			calendar.set(Calendar.MONTH, month);
		}
		else{
			throw new IllegalArgumentException("Integer is null");
		}
	}
	
	private void setYear(final Integer year){
		if(Objects.nonNull(year)){
			calendar.set(Calendar.YEAR, year);
		}
		else{
			throw new IllegalArgumentException("Integer is null");
		}
	}
	
	public Date asDate(){
		return calendar.getTime();
	}
}
