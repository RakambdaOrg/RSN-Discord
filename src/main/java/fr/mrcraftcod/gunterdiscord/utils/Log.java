package fr.mrcraftcod.gunterdiscord.utils;

import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("JavaDoc")
public class Log{
	private static final HashMap<Guild, Logger> LOGGERS = new HashMap<>();
	
	public static void warning(Guild g, String s, Object... args){
		getLogger(g).warn(s, args);
	}
	
	private static Logger getLogger(Guild g){
		return LOGGERS.computeIfAbsent(g, g2 -> {
			if(Objects.nonNull(g2)){
				return LoggerFactory.getLogger(g2.getName());
			}
			return LoggerFactory.getLogger("No Guild");
		});
	}
	
	public static void warning(Guild g, String s){
		getLogger(g).warn(s);
	}
	
	public static void info(Guild g, String s, Object... args){
		getLogger(g).info(s, args);
	}
	
	public static void info(Guild g, String s){
		getLogger(g).info(s);
	}
	
	public static void error(Guild g, String s, Object... args){
		getLogger(g).error(s, args);
	}
	
	public static void error(Guild g, String s){
		getLogger(g).error(s);
	}
}