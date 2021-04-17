package fr.raksrinana.rsndiscord.log;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Log{
	private static final Map<Guild, Logger> LOGGERS = new ConcurrentHashMap<>();
	private static final Logger NO_GUILD = LoggerFactory.getLogger("No Guild");
	
	@NotNull
	public static Logger getLogger(){
		return NO_GUILD;
	}
	
	@NotNull
	public static Logger getLogger(@Nullable Guild guild){
		if(Objects.isNull(guild)){
			return NO_GUILD;
		}
		return LOGGERS.computeIfAbsent(guild, key -> LoggerFactory.getLogger(key.getName()));
	}
}