package fr.mrcraftcod.gunterdiscord.listeners.musicparty;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.mrcraftcod.gunterdiscord.settings.configs.MusicPartyChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import fr.mrcraftcod.gunterdiscord.utils.player.StatusTrackSchedulerListener;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-21
 */
public class MusicPartyListener extends ListenerAdapter implements StatusTrackSchedulerListener{
	private static final ArrayList<MusicPartyListener> parties = new ArrayList<>();
	private static final int REQUIRED_TO_SKIP = 5;
	private static final Pattern CENSOR_CHAR = Pattern.compile("[A-Za-z0-9]");
	private final Guild guild;
	private final VoiceChannel voiceChannel;
	private final TextChannel musicPartyChannel;
	private final HashMap<Long, Integer> scores;
	private MusicPartyMusic currentMusic = null;
	private boolean currentFound = false;
	private Message currentMessage = null;
	
	/**
	 * Constructor.
	 *
	 * @param guild        The guild.
	 * @param voiceChannel The voice channel to play the music in.
	 */
	private MusicPartyListener(final Guild guild, final VoiceChannel voiceChannel){
		this.guild = guild;
		if(guild == null){
			throw new IllegalStateException("Guild cannot be null");
		}
		this.voiceChannel = voiceChannel;
		this.scores = new HashMap<>();
		this.musicPartyChannel = new MusicPartyChannelConfig(guild).getObject(null);
		if(musicPartyChannel == null){
			throw new IllegalStateException("Music party channel not defined");
		}
		
		guild.getJDA().addEventListener(this);
		parties.add(this);
	}
	
	/**
	 * Stop all parties.
	 */
	public static void stopAll(){
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
	public static Optional<MusicPartyListener> getParty(final Guild guild, final VoiceChannel voiceChannel){
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
	public static Optional<MusicPartyListener> getParty(final Guild guild, final VoiceChannel voiceChannel, final boolean shouldCreate){
		return parties.stream().filter(q -> q.getGuild().getIdLong() == guild.getIdLong()).findAny().or(() -> {
			try{
				if(shouldCreate){
					return Optional.of(new MusicPartyListener(guild, voiceChannel));
				}
			}
			catch(final Exception e){
				getLogger(guild).error("Error create a new music party game", e);
			}
			return Optional.empty();
		});
	}
	
	/**
	 * Get the guild.
	 *
	 * @return The guild.
	 */
	private Guild getGuild(){
		return guild;
	}
	
	/**
	 * Try to add a music to the party.
	 *
	 * @param event The event that triggered this add.
	 * @param args  The songs to add.
	 */
	public void addMusic(final MessageReceivedEvent event, final LinkedList<String> args){
		getLogger(getGuild()).info("Setting party music");
		if(args.size() < 1){
			Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Nombre de paramètres incorrecte");
		}
		else{
			args.forEach(url -> GunterAudioManager.play(event.getAuthor(), voiceChannel, this, track -> {
				if(Objects.isNull(track)){
					Actions.replyPrivate(event.getGuild(), event.getAuthor(), "%s, musique inconnue", event.getAuthor().getAsMention());
				}
				else if(track instanceof AudioTrack){
					Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Votre musique a bien été ajoutée dans la file et porte le titre: `" + ((AudioTrack) track).getInfo().title + "`");
				}
				else{
					Actions.replyPrivate(event.getGuild(), event.getAuthor(), track.toString());
				}
			}, 0, url));
		}
	}
	
	@Override
	public void onTrackSchedulerEmpty(){
		stop();
	}
	
	@Override
	public void onTrackEnd(final AudioTrack track){
		getLogger(getGuild()).info("MusicParty track ended");
		if(!currentFound && currentMusic != null){
			final var builder = Utilities.buildEmbed(musicPartyChannel.getJDA().getSelfUser(), Color.RED, "Vous êtes mauvais");
			builder.addField("Titre de la musique", currentMusic.getTitle(), false);
			builder.addField("Lien", currentMusic.getTrack().getInfo().uri, false);
			Actions.sendMessage(musicPartyChannel, builder.build());
		}
		currentMessage = null;
		currentMusic = null;
	}
	
	@Override
	public void onTrackStart(final AudioTrack track){
		getLogger(getGuild()).info("New track is starting: {}", track.getIdentifier());
		printScores();
		
		final var builder = Utilities.buildEmbed(musicPartyChannel.getJDA().getSelfUser(), Color.GREEN, "Nouveau son");
		builder.setDescription("Essayez de deviner le titre");
		builder.addField("Pour participer:", "Ecrivez le titre de la vidéo qui est en cours", false);
		builder.addField("Titre:", censorName(track.getInfo().title), false);
		Actions.sendMessage(musicPartyChannel, builder.build());
		
		currentFound = false;
		currentMusic = new MusicPartyMusic(track);
		getLogger(getGuild()).info("MusicParty track started: {}", currentMusic);
	}
	
	/**
	 * Prints the scores.
	 */
	public void printScores(){
		getLogger(getGuild()).info("Print music party scores");
		
		final HashMap<Integer, List<String>> bests;
		final var bestsScores = scores.values().stream().sorted(Comparator.reverseOrder()).distinct().limit(5).collect(Collectors.toList());
		bests = bestsScores.stream().mapToInt(score -> score).boxed().collect(Collectors.toMap(score -> score, score -> new ArrayList<>(), (a, b) -> b, HashMap::new));
		scores.forEach((k, v) -> {
			if(bests.containsKey(v)){
				bests.get(v).add(getGuild().getJDA().getUserById(k).getAsMention());
			}
		});
		
		final var builder = Utilities.buildEmbed(getGuild().getJDA().getSelfUser(), Color.PINK, "Podium");
		builder.setDescription("Voici le top des scores");
		bests.keySet().stream().sorted(Comparator.reverseOrder()).map(v -> new MessageEmbed.Field("Position " + (1 + bestsScores.indexOf(v)) + " (" + v + " points)", String.join(", ", bests.get(v)), false)).forEach(builder::addField);
		Actions.sendMessage(musicPartyChannel, builder.build());
	}
	
	/**
	 * Get the censored name to display.
	 *
	 * @param name The real name.
	 *
	 * @return The censored name.
	 */
	private static String censorName(final String name){
		return CENSOR_CHAR.matcher(name).replaceAll("?");
	}
	
	/**
	 * Stop the quiz.
	 */
	public void stop(){
		parties.remove(this);
		getLogger(getGuild()).info("Stopping music party");
		GunterAudioManager.leave(getGuild());
		Actions.sendMessage(musicPartyChannel, "Fin de la partie!");
		printScores();
	}
	
	@Override
	public void onGuildMessageReactionAdd(final GuildMessageReactionAddEvent event){
		super.onGuildMessageReactionAdd(event);
		if(currentMessage != null && getGuild().equals(event.getGuild()) && musicPartyChannel.getIdLong() == event.getChannel().getIdLong() && currentMessage.getIdLong() == event.getMessageIdLong()){
			musicPartyChannel.getMessageById(currentMessage.getIdLong()).queue(m -> {
				if(m.getReactions().size() > REQUIRED_TO_SKIP){
					getLogger(getGuild()).info("Enough reactions to skip");
					skip();
				}
			});
		}
	}
	
	/**
	 * Skip the current track.
	 */
	public void skip(){
		GunterAudioManager.skip(getGuild());
	}
	
	@Override
	public void onMessageReceived(final MessageReceivedEvent event){
		super.onMessageReceived(event);
		try{
			if(musicPartyChannel.getIdLong() == event.getMessage().getChannel().getIdLong()){
				if(currentMusic != null && !currentFound && currentMusic.getTitle().equalsIgnoreCase(event.getMessage().getContentRaw())){
					getLogger(getGuild()).info("{} found the music `{}`", Utilities.getUserToLog(event.getAuthor()), currentMusic.getTitle());
					
					final var builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Son trouvé");
					builder.addField("Titre de la musique", currentMusic.getTitle(), false);
					builder.addField("Lien", currentMusic.getTrack().getInfo().uri, false);
					builder.addField("Reaction:", "Ajouter une réaction pour passer la musique (a " + REQUIRED_TO_SKIP + " je la passe)", false);
					currentMessage = Actions.getMessage(musicPartyChannel, builder.build());
					currentMessage.addReaction(BasicEmotes.THUMB_UP.getValue()).complete();
					
					currentFound = true;
					scores.compute(event.getAuthor().getIdLong(), (key, value) -> value == null ? 1 : (value + 1));
				}
			}
		}
		catch(final Exception e){
			getLogger(getGuild()).error("", e);
		}
	}
}
