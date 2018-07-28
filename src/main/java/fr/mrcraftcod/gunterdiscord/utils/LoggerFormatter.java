package fr.mrcraftcod.gunterdiscord.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Formatter for the logger.
 * <p>
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 21/05/2017.
 *
 * @author Thomas Couchoud
 * @since 2017-05-21
 */
public class LoggerFormatter extends Formatter{
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSSS");
	private static final Pattern PACKAGES = Pattern.compile("^fr\\.mrcraftcod\\.gunterdiscord(/fr\\.mrcraftcod\\.gunterdiscord)?\\.?");
	
	@Override
	public String format(LogRecord record){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(record.getLevel().getLocalizedName());
		stringBuilder.append(": ");
		stringBuilder.append(dateFormat.format(new Date(record.getMillis())));
		stringBuilder.append(" ");
		int stackIndex;
		StackTraceElement trace = null;
		for(stackIndex = 0; stackIndex < Thread.currentThread().getStackTrace().length; stackIndex++){
			trace = Thread.currentThread().getStackTrace()[stackIndex];
			if(trace.getClassName().startsWith("fr.mrcraftcod.gunterdiscord") && !trace.getClassName().startsWith("fr.mrcraftcod.gunterdiscord.utils.LoggerFormatter") && !trace.getClassName().startsWith("fr.mrcraftcod.gunterdiscord.utils.Log")){
				break;
			}
		}
		if(trace != null){
			stringBuilder.append(PACKAGES.matcher(trace.toString()).replaceAll(""));
		}
		
		stringBuilder.append(" -> ");
		
		stringBuilder.append(record.getMessage());
		if(record.getThrown() != null){
			stringBuilder.append(" (");
			stringBuilder.append(record.getThrown().toString());
			stringBuilder.append("\n");
			stringBuilder.append(Arrays.stream(record.getThrown().getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n")));
			stringBuilder.append(")");
		}
		
		return stringBuilder.append("\n").toString();
	}
}