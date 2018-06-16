package fr.mrcraftcod.gunterdiscord.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("JavaDoc")
public class Log
{
	private static Logger logger;
	
	public static Logger getLogger()
	{
		return logger != null ? logger : setAppName("MCCUtils");
	}
	
	public static Logger setAppName(String name)
	{
		logger = Logger.getLogger(name);
		return logger;
	}
	
	public static void warning(String s)
	{
		log(Level.WARNING, s);
	}
	
	public static void warning(Throwable e, String s)
	{
		log(Level.WARNING, e, s);
	}
	
	public static void warning(String s, Object... args)
	{
		warning(String.format(s, args));
	}
	
	public static void warning(Throwable e, String s, Object... args)
	{
		warning(e, String.format(s, args));
	}
	
	public static void info(String s, Object... args)
	{
		log(Level.INFO, String.format(s, args));
	}
	
	public static void info(String s)
	{
		log(Level.INFO, s);
	}
	
	public static void error(String s)
	{
		log(Level.SEVERE, s);
	}
	
	public static void error(Throwable e, String s)
	{
		log(Level.SEVERE, e, s);
	}
	
	public static void error(Throwable e, String s, Object... args)
	{
		error(e, String.format(s, args));
	}
	
	public static void error(String s, Object... args)
	{
		error(String.format(s, args));
	}
	
	public static void log(Level level, String s)
	{
		getLogger().log(level, s);
	}
	
	public static void log(Level level, Throwable e, String s)
	{
		getLogger().log(level, s, e);
	}
}