package fr.mrcraftcod.gunterdiscord.listeners.quiz;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-21
 */
public class MusicPartyListener extends ListenerAdapter
{
	private static final ArrayList<QuizListener> parties = new ArrayList<>();
	private final Guild guild;
	private final VoiceChannel voiceChannel;
	private HashMap<Long, Integer> answers;
	private boolean stopped;
	
	/**
	 * Constructor.
	 *
	 * @param guild  The guild.
	 * @param voiceChannel The voice channel to play the music in.
	 */
	private QuizListener(Guild guild, VoiceChannel voiceChannel)
	{
		this.guild = guild;
		this.voiceChannel = voiceChannel;
		this.stopped = false;
		
		guild.getJDA().addEventListener(this);
		parties.add(this);
		
		//TODO connect voice
	}
	
	/**
	 * Stop all parties.
	 */
	public static void stopAll()
	{
		parties.forEach(MusicPartyListener::stop);
	}
	
	/**
	 * Get the current instance of the game.
	 *
	 * @param guild  The guild.
	 * @param voiceChannel The voice channel to play the music in.
	 *
	 * @return The game of the guild.
	 */
	public static Optional<MusicPartyListener> getParty(Guild guild, VoiceChannel voiceChannel)
	{
		return getQuiz(guild, true);
	}
	
	/**
	 * Get the current instance of the game.
	 *
	 * @param guild        The guild.
	 * @param voiceChannel The voice channel to play the music in.
	 * @param shouldCreate If a new game should be created if not found.
	 *
	 * @return The game of the guild.
	 */
	public static Optional<MusicPartyListener> getParty(Guild guild, VoiceChannel voiceChannel, boolean shouldCreate)
	{
		return parties.stream().filter(q -> q.getGuild().getIdLong() == guild.getIdLong()).findAny().or(() -> {
			try
			{
				if(shouldCreate)
					return Optional.of(new MusicPartyListener(guild, voiceChannel));
			}
			catch(Exception e)
			{
				Log.error(e, "Error create a new music party game");
			}
			return Optional.empty();
		});
	}
	
	/**
	 * Get the guild.
	 *
	 * @return The guild.
	 */
	private Guild getGuild()
	{
		return guild;
	}
	
	/**
	 * Stop the quiz.
	 */
	public void stop()
	{
		stopped = true;
		//TODO Disconnect voice
		printScores();
	}
	
	/**
	 * Prints the scores.
	 */
	 public void printScores()
	 {
	    TextChannel channel = new MusicPartyChannelConfig().getTextChannel(getGuild());
	    //TODO Send results
	 }
}
