package fr.mrcraftcod.gunterdiscord.listeners.musicparty;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.mrcraftcod.gunterdiscord.settings.configs.MusicPartyChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import fr.mrcraftcod.gunterdiscord.utils.player.StatusTrackSchedulerListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
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
public class MusicPartyListener extends ListenerAdapter implements StatusTrackSchedulerListener
{
	private static final ArrayList<MusicPartyListener> parties = new ArrayList<>();
	private final Guild guild;
	private final VoiceChannel voiceChannel;
	private HashMap<Long, Integer> scores;
	private final List<MusicPartyMusic> musics;
	private boolean stopped;
	private final TextChannel musicPartyChannel;
	private MusicPartyMusic currentMusic = null;
	private static final int REQUIRED_TO_SKIP = 5;
	private boolean currentFound = false;
	private Message currentMessage = null;
	
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
		this.musics = new LinkedList<>();
		
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
	
	public void addMusic(MessageReceivedEvent event, LinkedList<String> args)
	{
		Log.info("Setting party music");
		if(args.size() < 1)
			Actions.replyPrivate(event.getAuthor(), "Nombre de paramètres incorrecte");
		else
		{
			args.forEach(url -> GunterAudioManager.play(voiceChannel, this, track -> {
				MusicPartyMusic musicPartyMusic = new MusicPartyMusic(track);
				musics.add(musicPartyMusic);
				Actions.replyPrivate(event.getAuthor(), "Votre musique a bien été ajoutée dans la file et porte le titre: `" + musicPartyMusic.getTitle() + "`");
			}, url));
		}
	}
	
	public void skip()
	{
		GunterAudioManager.skip(getGuild());
	}
	
	@Override
	public void onTrackSchedulerEmpty()
	{
		stop();
	}
	
	@Override
	public void onTrackEnd(AudioTrack track)
	{
		Log.info("MusicParty track ended");
		if(!currentFound && currentMusic != null)
		{
			EmbedBuilder builder = Utilities.buildEmbed(musicPartyChannel.getJDA().getSelfUser(), Color.RED, "Vous êtes mauvais");
			builder.addField("Titre de la musique", currentMusic.getTitle(), false);
			Actions.sendMessage(musicPartyChannel, builder.build());
		}
		currentMessage = null;
		currentMusic = null;
	}
	
	@Override
	public void onTrackStart(AudioTrack track)
	{
		Log.info("New track is starting: %s", track.getIdentifier());
		Log.info("Tracks available: %s", musics.stream().map(t -> t.getTrack().getIdentifier() + " -> " + t.getTitle()).collect(Collectors.joining(" | ")));
		printScores();
		
		EmbedBuilder builder = Utilities.buildEmbed(musicPartyChannel.getJDA().getSelfUser(), Color.GREEN, "Nouveau son");
		builder.setDescription("Essayez de deviner le titre");
		builder.addField("Pour participez:", "Ecrivez le titre de la vidéo qui est en cours", false);
		Actions.sendMessage(musicPartyChannel, builder.build());
		
		currentFound = false;
		currentMusic = musics.stream().filter(m -> track.getIdentifier().equals(m.getTrack().getIdentifier())).findFirst().orElse(null);
		Log.info("MusicParty track started: %s", currentMusic);
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
		parties.remove(this);
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
		
		EmbedBuilder builder = Utilities.buildEmbed(getGuild().getJDA().getSelfUser(), Color.PINK, "Podium");
		builder.setDescription("Voici le top des scores");
		bests.keySet().stream().sorted(Comparator.reverseOrder()).map(v -> new MessageEmbed.Field("Position " + (1 + bestsScores.indexOf(v)) + " (" + v + " points)", String.join(", ", bests.get(v)), false)).forEach(builder::addField);
		Actions.sendMessage(musicPartyChannel, builder.build());
	}
	
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
	{
		super.onGuildMessageReactionAdd(event);
		if(currentMessage != null && getGuild().equals(event.getGuild()) && musicPartyChannel.getIdLong() == event.getChannel().getIdLong() && currentMessage.getIdLong() == event.getMessageIdLong())
		{
			musicPartyChannel.getMessageById(currentMessage.getIdLong()).queue(m -> {
				if(m.getReactions().size() > REQUIRED_TO_SKIP)
				{
					Log.info("Enough reactions to skip");
					skip();
				}
			});
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		super.onMessageReceived(event);
		try
		{
			if(musicPartyChannel.getIdLong() == event.getMessage().getChannel().getIdLong())
			{
				if(currentMusic != null && !currentFound && currentMusic.getTitle().equalsIgnoreCase(event.getMessage().getContentRaw()))
				{
					Log.info(Actions.getUserToLog(event.getAuthor()) + " found the music `" + currentMusic.getTitle() + "`");
					
					EmbedBuilder builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Son trouvé");
					builder.addField("Titre de la musique", currentMusic.getTitle(), false);
					builder.addField("Reaction:", "Ajouter une réaction pour passer la musique (a " + REQUIRED_TO_SKIP + " je la passe)", false);
					currentMessage = Actions.getMessage(musicPartyChannel, builder.build());
					currentMessage.addReaction(BasicEmotes.THUMB_UP.getValue()).complete();
					
					currentFound = true;
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
