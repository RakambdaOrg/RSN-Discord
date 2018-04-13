package fr.mrcraftcod.gunterdiscord;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class Settings
{
	private static final String BANNED_WORDS_KEY = "bannedRegexes";
	private static final String PREFIX_KEY = "prefix";
	private static final String IMAGES_ONLY_KEY = "onlyImage";
	private static final String REPORT_CHANNEL_KEY = "reportChannel";
	private static final String MODO_ROLES_KEY = "modoRoles";
	private final Path path;
	private final JSONObject settings;
	
	public Settings(Path path) throws IOException
	{
		this.path = path;
		if(path.toFile().exists())
			settings = new JSONObject(Files.readAllLines(path).stream().collect(Collectors.joining("")));
		else
			settings = new JSONObject(IOUtils.toString(Main.class.getResourceAsStream("/settings/default.json"), "UTF-8"));
	}
	
	public void save() throws IOException
	{
		Files.write(path, Arrays.asList(settings.toString(4).split("\n")), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
	}
	
	public void close()
	{
	}
	
	public boolean setPrefix(String prefix)
	{
		if(prefix.length() > 0)
		{
			settings.put(PREFIX_KEY, true);
			return true;
		}
		return false;
	}
	
	public boolean isImageOnly(long id)
	{
		if(settings.has(IMAGES_ONLY_KEY))
		{
			for(Object obj : settings.getJSONArray(IMAGES_ONLY_KEY))
			{
				if(obj instanceof Long)
					if((Long) obj == id)
						return true;
					else
						System.out.println(obj.getClass().getName());
			}
		}
		return false;
	}
	
	public void addModoRole(String role)
	{
		if(!settings.has(MODO_ROLES_KEY))
			settings.put(MODO_ROLES_KEY, new JSONArray());
		settings.getJSONArray(MODO_ROLES_KEY).put(role);
	}
	
	public void removeModoRole(String role)
	{
		if(settings.has(MODO_ROLES_KEY))
		{
			int index = settings.getJSONArray(MODO_ROLES_KEY).toList().indexOf(role);
			if(index != -1)
				settings.getJSONArray(MODO_ROLES_KEY).remove(index);
		}
	}
	
	public List<String> getModeratorsRoles()
	{
		List<String> roles = new ArrayList<>();
		if(settings.has(MODO_ROLES_KEY))
		{
			for(Object obj : settings.getJSONArray(MODO_ROLES_KEY))
			{
				if(obj instanceof String)
					roles.add((String) obj);
				else
					System.out.println(obj.getClass().getName());
			}
		}
		return roles;
	}
	
	public long getReportChannel()
	{
		return settings.getLong(REPORT_CHANNEL_KEY);
	}
	
	public void setReportChannel(long ID)
	{
		settings.put(REPORT_CHANNEL_KEY, ID);
	}
	
	public String getPrefix()
	{
		if(settings.has(PREFIX_KEY))
			return settings.getString(PREFIX_KEY);
		return "g?";
	}
	
	public List<String> getBannedWords()
	{
		List<String> banned = new ArrayList<>();
		if(settings.has(BANNED_WORDS_KEY))
		{
			for(Object obj : settings.getJSONArray(BANNED_WORDS_KEY))
			{
				if(obj instanceof String)
					banned.add((String) obj);
				else
					System.out.println(obj.getClass().getName());
			}
		}
		return banned;
	}
}
