package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.MusicPartyChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-21
 */
public class MusicPartyListener extends ListenerAdapter
{
	private static final ArrayList<MusicPartyListener> parties = new ArrayList<>();
	private final Guild guild;
	private final VoiceChannel voiceChannel;
	private HashMap<Long, Integer> scores;
	private boolean stopped;
	private final TextChannel musicPartyChannel;
	private String currentTitle = null;
	
	/**
	 * Constructor.
	 *
	 * @param guild        The guild.
	 * @param voiceChannel The voice channel to play the music in.
	 */
	private MusicPartyListener(Guild guild, VoiceChannel voiceChannel)
	{
		this.guild = guild;
		if(guild == null)
			throw new IllegalStateException("Guild cannot be null");
		this.voiceChannel = voiceChannel;
		this.stopped = false;
		this.scores = new HashMap<>();
		this.musicPartyChannel = new MusicPartyChannelConfig().getTextChannel(guild);
		if(musicPartyChannel == null)
			throw new IllegalStateException("Music party channel not defined");
		
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
	 * @param guild        The guild.
	 * @param voiceChannel The voice channel to play the music in.
	 *
	 * @return The game of the guild.
	 */
	public static Optional<MusicPartyListener> getParty(Guild guild, VoiceChannel voiceChannel)
	{
		return getParty(guild, voiceChannel, true);
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
	
	public void setMusic(MessageReceivedEvent event, LinkedList<String> args)
	{
		Log.info("Setting party music");
		if(currentTitle == null)
		{
			if(args.size() < 2)
				Actions.replyPrivate(event.getAuthor(), "Nombre de parametres incorrecte");
			else
			{
				GunterAudioManager.play(voiceChannel, args.poll());
				currentTitle = String.join(" ", args);
				
				EmbedBuilder builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Nouveau son");
				builder.setDescription("Essayez de deviner le titre");
				builder.addField("Pour participez, écrivez votre pensée sous la forme:", "artiste - titre", false);
				Actions.sendMessage(musicPartyChannel, builder.build());
			}
		}
		else
		{
			Actions.replyPrivate(event.getAuthor(), "Veuillez attendre que la musique précédente soit terminée");
		}
	}
	
	public void skip()
	{
		GunterAudioManager.skip(getGuild());
		currentTitle = null;
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
		Log.info("Stopping");
		stopped = true;
		GunterAudioManager.leave(getGuild());
		printScores();
	}
	
	/**
	 * Prints the scores.
	 */
	public void printScores()
	{
		Log.info("Print music party scores");
		
		HashMap<Integer, List<String>> bests = new HashMap<>();
		List<Integer> bestsScores = scores.values().stream().sorted(Comparator.reverseOrder()).distinct().limit(5).collect(Collectors.toList());
		for(int score: bestsScores)
			bests.put(score, new ArrayList<>());
		scores.forEach((k, v) -> {
			if(bests.containsKey(v))
				bests.get(v).add(getGuild().getJDA().getUserById(k).getAsMention());
		});
		
		EmbedBuilder builder = Utilities.buildEmbed(getGuild().getJDA().getSelfUser(), Color.PINK, "Leaderboard");
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
				if(currentTitle != null && currentTitle.equalsIgnoreCase(event.getMessage().getContentRaw()))
				{
					Log.info(Actions.getUserToLog(event.getAuthor()) + " found the music `" + currentTitle + "`");
					
					EmbedBuilder builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Son trouvé");
					builder.addField("Titre de la musique", currentTitle, false);
					Actions.sendMessage(musicPartyChannel, builder.build());
					
					currentTitle = null;
					
					scores.compute(event.getAuthor().getIdLong(), (key, value) -> value == null ? 1 : (value + 1));
				}
			}
		}
		catch(Exception e)
		{
			Log.error(e, "");
		}
	}
}
