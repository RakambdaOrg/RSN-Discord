package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuizzChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.InvalidClassException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-19
 */
public class QuizzMessageListener extends ListenerAdapter implements Runnable
{
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		super.onMessageReceived(event);
		try
		{
			if(event.getChannel().getIdLong() == new QuizzChannelConfig().getLong())
			{
			
			}
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		JDA jda = Main.getJDA();
		jda.getPresence().setGame(Game.playing("The Kwizzz"));
		TextChannel quizzChannel = null;
		
		try
		{
			quizzChannel = jda.getTextChannelById(new QuizzChannelConfig().getLong());
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			e.printStackTrace();
		}
		
		if(quizzChannel == null)
		{
			setBack();
			return;
		}
		
		Actions.sendMessage(quizzChannel, "Ok @everyone, j'espère que vous êtes aussi chaud que mon chalumeau pour un petit kwizzz!");
	}
	
	private void setBack()
	{
		JDA jda = Main.getJDA();
		jda.getPresence().setGame(Game.playing(""));
	}
}
