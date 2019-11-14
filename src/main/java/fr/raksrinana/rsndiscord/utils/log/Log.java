package fr.raksrinana.rsndiscord.utils.log;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Objects;

public class Log{
	private static final HashMap<Guild, Logger> LOGGERS = new HashMap<>();
	
	@NonNull
	public static Logger getLogger(final Guild g){
		return LOGGERS.computeIfAbsent(g, g2 -> {
			if(Objects.nonNull(g2)){
				return LoggerFactory.getLogger(g2.getName());
			}
			return LoggerFactory.getLogger("No Guild");
		});
	}
}