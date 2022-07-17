package fr.raksrinana.rsndiscord.utils;

import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

@Log4j2
public class LangUtils{
	private static final Map<Locale, ResourceBundle> bundles = new HashMap<>();
	private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
	
	@NotNull
	public static String translate(@Nullable Guild guild, @NotNull String key, Object... args){
		if(Objects.isNull(guild)){
			return translate(DEFAULT_LOCALE, key, args);
		}
		return translate(Settings.get(guild).getLocale().orElseGet(() ->  Locale.forLanguageTag(guild.getLocale().getLocale())), key, args);
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
