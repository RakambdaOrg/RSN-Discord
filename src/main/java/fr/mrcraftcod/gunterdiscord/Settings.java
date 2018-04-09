package fr.mrcraftcod.gunterdiscord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class Settings
{
	private final File path;
	
	public Settings(File path) throws FileNotFoundException
	{
		this.path = path;
	}
	
	public void save() throws IOException
	{
	}
	
	public void close()
	{
	}
}
