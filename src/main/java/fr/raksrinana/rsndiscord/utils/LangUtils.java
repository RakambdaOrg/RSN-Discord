package fr.raksrinana.rsndiscord.utils;

import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
public class LangUtils{
	private static final Map<Locale, ResourceBundle> bundles = new HashMap<>();
	
	public static String translate(@NonNull Guild guild, @NonNull String key, Object... args){
		return translate(Settings.get(guild).getLocale(), key, args);
	}
	
	public static String translate(@NonNull Locale locale, @NonNull String key, Object... args){
		try{
			var bundle = bundles.computeIfAbsent(locale, mapKey -> ResourceBundle.getBundle("lang/rsn", locale));
			var message = bundle.getString(key).replace("'", "’");
			return MessageFormat.format(message, args);
		}
		catch(Exception e){
			Utilities.reportException(e);
		}
		return "<<UNKNOWN MESSAGE>>";
	}
}
