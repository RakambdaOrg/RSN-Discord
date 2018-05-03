package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.PrefixConfig;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class CommandsMessageListener extends ListenerAdapter
{
	private final List<Command> commands;
	
	public CommandsMessageListener()
	{
		commands = getAnnotatedClasses(CallableCommand.class, "fr.mrcraftcod.gunterdiscord.commands").stream().map(c -> {
			try
			{
				Log.info("Found command " + c.getSimpleName());
				//noinspection unchecked
				return (Command) c.getConstructor().newInstance();
			}
			catch(Exception e)
			{
				Log.error("Error creating command", e);
			}
			return null;
		}).collect(Collectors.toList());
	}
	
	/**
	 * Get all the classes annotated with the given annotation.
	 *
	 * @param annotation  The annotation to search.
	 * @param packageName The name of the package to search in.
	 *
	 * @return The classes found.
	 */
	private List<Class> getAnnotatedClasses(Class<? extends Annotation> annotation, String packageName)
	{
		List<Class> classes = new ArrayList<>();
		try
		{
			classes.addAll(getClasses(packageName));
		}
		catch(ClassNotFoundException | IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}
		Iterator<Class> it = classes.iterator();
		while(it.hasNext())
			if(!it.next().isAnnotationPresent(annotation))
				it.remove();
		return classes;
	}
	
	/**
	 * Get all the classes inside a package.
	 *
	 * @param packageName The name of the package.
	 *
	 * @return The classes.
	 *
	 * @throws ClassNotFoundException If something wrong happened.
	 * @throws IOException            If something wrong happened.
	 * @throws URISyntaxException     If something wrong happened.
	 */
	private List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException, URISyntaxException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<>();
		while(resources.hasMoreElements())
		{
			URL resource = resources.nextElement();
			URI uri = new URI(resource.toString());
			try
			{
				dirs.add(new File(uri.getPath()));
			}
			catch(NullPointerException e)
			{
				Log.error("Error getting class in " + uri + " // " + resource, e);
			}
		}
		List<Class> classes = new ArrayList<>();
		for(File directory : dirs)
			classes.addAll(findClasses(directory, packageName));
		
		return classes;
	}
	
	/**
	 * Find the classes of a package inside a directory.
	 *
	 * @param directory   The directory to search in (recursively).
	 * @param packageName The name of the package.
	 *
	 * @return The classes found.
	 *
	 * @throws ClassNotFoundException If something wrong happened.
	 */
	private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException
	{
		List<Class> classes = new ArrayList<>();
		if(!directory.exists())
			return classes;
		File[] files = directory.listFiles();
		if(files != null)
			for(File file : files)
				if(file.isDirectory())
					classes.addAll(findClasses(file, packageName + "." + file.getName()));
				else if(file.getName().endsWith(".class"))
					classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - ".class".length())));
		return classes;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		super.onMessageReceived(event);
		try
		{
			if(isCommand(event.getMessage().getContentRaw()))
			{
				LinkedList<String> args = new LinkedList<>();
				args.addAll(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
				Command command = getCommand(args.pop().substring(new PrefixConfig().getString("g?").length()));
				if(command != null && (command.getScope() == -5 || command.getScope() == event.getChannel().getType().getId()))
				{
					try
					{
						command.execute(event, args);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						event.getChannel().sendMessage("Cette fonctionnalité doit encore être configuré. Veuillez en avertir un modérateur.").complete();
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private boolean isCommand(String text)
	{
		try
		{
			return text.startsWith(new PrefixConfig().getString());
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	private Command getCommand(String commandText)
	{
		for(Command command : commands)
			if(command.getCommand().equalsIgnoreCase(commandText))
				return command;
		
		return null;
	}
}
