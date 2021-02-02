package fr.raksrinana.rsndiscord.utils;

import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.text.MessageFormat;
import java.util.*;

@Slf4j
public class LangUtils{
	private static final Map<Locale, ResourceBundle> bundles = new HashMap<>();
	
	@NotNull
	public static String translate(@NotNull Guild guild, @NotNull String key, Object... args){
		return translate(Settings.get(guild).getLocale().orElseGet(guild::getLocale), key, args);
	}
	
	@NotNull
	public static String translate(@NotNull Locale locale, @NotNull String key, Object... args){
		try{
			var bundle = Optional.ofNullable(bundles.computeIfAbsent(locale, mapKey -> {
				try{
					return ResourceBundle.getBundle("lang/rsn", locale);
				}
				catch(Exception e){
					Utilities.reportException("Failed to get resource bundle for language " + locale, e);
					return null;
				}
			}));
			var message = bundle.map(b -> b.getString(key))
					.map(value -> value.replace("'", "’"))
					.orElse(key);
			return MessageFormat.format(message, args);
		}
		catch(Exception e){
			Utilities.reportException("Failed to translate message " + key, e);
		}
		return "<<UNKNOWN MESSAGE>>";
	}
}
