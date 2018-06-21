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
	private final TextChannel musicPartyChannel;
	private String currentTitle = null;
	
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
		this.musicPartyChannel = new MusicPartyChannelConfig().getTextChannel(guild);
		
		guild.getJDA().addEventListener(this);
		parties.add(this);
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
		GunterAudioManager.leave(getGuild());
		printScores();
	}
	
	/**
	 * Prints the scores.
	 */
	 public void printScores()
	 {
	     HashMap<Integer, List<String>> bests = new HashMap<>();
		List<Integer> bestsScores = scores.values().stream().sorted(Comparator.reverseOrder()).distinct().limit(5).collect(Collectors.toList());
		for(int score: bestsScores)
			bests.put(score, new ArrayList<>());
		scores.forEach((k, v) -> {
			if(bests.containsKey(v))
				bests.get(v).add(jda.getUserById(k).getAsMention());
		});
	     
	    EmbedBuilder builder = Utilities.buildEmbed(guild.getJDA().getSelfUser().getUser(), Color.PINK, "Le jeu est terminé");
	    builder.setDescription("Voici le top des scores");
	    bests.keySet().stream().sorted(Comparator.reverseOrder()).map(v -> new MessageEmbed.Field("Position " + (1 + bestsScores.indexOf(v)) + " (" + v + " points)", String.join(", ", bests.get(v)), false)).forEach(builder::addField);
		Actions.sendMessage(musicPartyChannel, builder.build());
	 }
	 
	 @Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		super.onMessageReceived(event);
		try
		{
			if(musicPartyChannel.getIdLong() == event.getMessage().getChannel().getIdLong())
			{
			    if(Utilities.isModerator(event.getMember())
			    {
			        LinkedList<String> args = new LinkedList<>();
			        args.addAll(event.getMessage().getRawContent().split(" "));
			        if(args.size() > 1)
			        {
			            if(args.poll().equals("mp"))
			            {
			                if(args.size() < 2)
			                    Actions.replyPrivate(event.getAuthor(), "Nombre de parametres incorrecte");
			                else
			                {
			                    GunterAudioManager.play(voiceChannel, args.poll());
			                    currentTitle = String.joining(args, " ");
			                    
			                    EmbedBuilder builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Nouveau son");
                        	    builder.setDescription("Essayez de deviner le titre de la musique sous la forme");
                        	    builder.addField("Pour participez, écrivez votre pensée sous la forme:", "artiste - titre", false);
                        	    Actions.sendMessage(musicPartyChannel, builder.build());
			                }
			            }
			        }    
			    }
			    else
			    {
			        if(currentTitle != null & currentTitle.equalsIgnoreCase(event.getMessage().getRawContent()))
			        {
			            EmbedBuilder builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Son trouvé");
                        builder.addField("Titre de la musique", currentTitle, false);
                        Actions.sendMessage(musicPartyChannel, builder.build());
                        
                        currentTitle = null;
                        
                        answers.compute(event.getAuthor().getIdLong(), (key, value) -> value == null ? 1 : (value + 1));
			        }
			    }
			}
		}
		catch(Exception e)
		{
			Log.error(e, "");
		}
	}
}
