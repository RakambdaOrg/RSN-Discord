package fr.rakambda.rsndiscord.spring.util;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

@Slf4j
@Service
public class LocalizationService{
	private static final Map<Locale, ResourceBundle> bundles = new HashMap<>();
	private static final String UNKNOWN_MESSAGE = "<<UNKNOWN MESSAGE>>";
	
	@NonNull
	public String translate(@NonNull Locale locale, @NonNull String key, Object... args){
		var bundleOptional = getBundle(locale);
		if(bundleOptional.isEmpty()){
			return UNKNOWN_MESSAGE;
		}
		
		var message = bundleOptional
				.map(b -> b.getString(key))
				.map(value -> value.replace("'", "’"))
				.orElse(key);
		return MessageFormat.format(message, args);
	}
	
	@NonNull
	public String translate(@NonNull DiscordLocale locale, @NonNull String key, Object... args){
		return translate(Locale.forLanguageTag(locale.getLocale()), key, args);
	}
	
	@NonNull
	private Optional<ResourceBundle> getBundle(@NonNull Locale locale){
		var bundle = bundles.computeIfAbsent(locale, this::buildBundle);
		return Optional.ofNullable(bundle);
	}
	
	@Nullable
	private ResourceBundle buildBundle(@NonNull Locale locale){
		try{
			return ResourceBundle.getBundle("lang/rsn", locale);
		}
		catch(Exception e){
			log.warn("Failed to get resource bundle for language " + locale, e);
			return null;
		}
	}
}
