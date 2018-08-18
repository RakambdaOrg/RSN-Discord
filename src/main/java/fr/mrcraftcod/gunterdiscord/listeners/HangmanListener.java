package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.HangmanChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.HangmanRoleConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.PrefixConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.io.IOUtils;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-23
 */
public class HangmanListener extends ListenerAdapter{
	private static final ArrayList<HangmanListener> games = new ArrayList<>();
	private static final int DISCOVER_START = 2;
	private static final char HIDDEN_CHAR = '-';
	private static final long MAX_WAIT_TIME = 30;
	private static final int MAX_HANG_LEVEL = 6;
	private final Guild guild;
	private final Role role;
	private final ScheduledExecutorService executor;
	private User waitingUser;
	private int hangStage;
	private int playerCount;
	private StringBuilder hiddenWord;
	private String realWord;
	private final List<Character> badTry;
	private ScheduledFuture lastFuture;
	
	/**
	 * Constructor.
	 *
	 * @param guild The guild the game is in.
	 *
	 * @throws NoValueDefinedException If something went wrong.
	 */
	private HangmanListener(final Guild guild) throws NoValueDefinedException{
		this.guild = guild;
		executor = Executors.newSingleThreadScheduledExecutor();
		hangStage = 0;
		playerCount = 0;
		hiddenWord = new StringBuilder();
		realWord = "";
		role = new HangmanRoleConfig(guild).getObject();
		if(role == null){
			throw new IllegalStateException("Hangman role doesn't exists");
		}
		badTry = new ArrayList<>();
		final var prefix = new PrefixConfig(guild).getObject();
		final var channel = new HangmanChannelConfig(guild).getObject();
		if(channel == null){
			throw new IllegalStateException("Hangman channel doesn't exists");
		}
		Actions.sendMessage(channel, Actions.PIN_MESSAGE, "Salut à tous! Si vous êtes la c'est que vous êtes chaud pour un petit pendu. Mais j'espère que vous êtes bons, sinon c'est vous qui allez finir pendu (au bout de %d fautes)!\n\nLe principe est simple. Je vais commencer par choisir un mot dans ma petite tête. Ensuite je vous l'écrirais avec les lettres cachées. Seul %d lettres seront apparentes au debut. Une fois cela fait, je désignerai une personne afin de me dire la lettre que vous voulez essayer, à vous de vous entendre afin de faire les bons choix.\n\nSi une personne ne déclare pas de choix en %ds, écrivez un petit mot et je referai tourner la roue pour désigner un représentant. Si vous désirez quitter, utilisez `%spendu leave`. Si vous voulez passer votre tour utilisez `%spendu skip`. Pour avoir des infos sur le mot actuel utilisez `%spendu mot`.\n\n\nAlley, laissez moi réfléchir!", MAX_HANG_LEVEL, DISCOVER_START, MAX_WAIT_TIME, prefix, prefix, prefix);
		new Thread(() -> {
			try{
				Thread.sleep(5000);
			}
			catch(InterruptedException e){
				getLogger(guild).error("Error sleeping");
			}
			realWord = selectRandomWord();
			hiddenWord = genHidden(realWord);
			var chars = realWord.chars().map(c -> mapChar((char) c)).distinct().boxed().map(i -> (char) (int) i).collect(Collectors.toList());
			Collections.shuffle(chars);
			for(var i = 0; i < DISCOVER_START; i++){
				if(chars.size() > i){
					discoverLetter((char) ((int) chars.get(i)));
				}
			}
			Actions.sendMessage(channel, "Le mot que j'ai choisi est: %s", hiddenWord);
			displayHangman();
			pickRandomUser();
		}).start();
		guild.getJDA().addEventListener(this);
		games.add(this);
		getLogger(guild).info("Crated hangman game");
	}
	
	/**
	 * Stop all running games.
	 */
	static void stopAll(){
		games.forEach(HangmanListener::delayEndGame);
	}
	
	/**
	 * Get the game for a guild.
	 *
	 * @param guild The guild concerned.
	 *
	 * @return The game.
	 */
	public static Optional<HangmanListener> getGame(final Guild guild){
		return getGame(guild, true);
	}
	
	/**
	 * Get the game for a guild.
	 *
	 * @param guild  The guild concerned.
	 * @param create If a game is created if it doesn't exists.
	 *
	 * @return The game.
	 */
	public static Optional<HangmanListener> getGame(final Guild guild, final boolean create){
		return games.stream().filter(h -> h.getGuild().getIdLong() == guild.getIdLong()).findAny().or(() -> {
			try{
				if(create){
					return Optional.of(new HangmanListener(guild));
				}
			}
			catch(final Exception e){
				getLogger(guild).error("Error create a new hangman game", e);
			}
			return Optional.empty();
		});
	}
	
	/**
	 * Get the guild of teh game.
	 *
	 * @return The guild.
	 */
	private Guild getGuild(){
		return guild;
	}
	
	/**
	 * Tells if 2 letters are the same.
	 *
	 * @param c1 First letter.
	 * @param c2 Second letter.
	 *
	 * @return True if matches, false otherwise.
	 */
	private static boolean matchLetter(final char c1, final char c2){
		return mapChar(c1) == mapChar(c2);
	}
	
	/**
	 * Get a char configurations on its parent.
	 * For example letters with accent will become the letter without the accent.
	 *
	 * @param c The letter.
	 *
	 * @return The mapped letter.
	 */
	private static char mapChar(final char c){
		if(c == 'é' || c == 'è' || c == 'ê'){
			return 'e';
		}
		if(c == 'à' || c == 'â'){
			return 'a';
		}
		if(c == 'ì' || c == 'î'){
			return 'i';
		}
		if(c == 'ô' || c == 'ò'){
			return 'o';
		}
		if(c == 'û' || c == 'ù'){
			return 'u';
		}
		return c;
	}
	
	/**
	 * Get the word as being hidden.
	 *
	 * @param word The word to hide.
	 *
	 * @return The hidden word.
	 */
	private static StringBuilder genHidden(final String word){
		final var str = new StringBuilder();
		for(final var c : word.toCharArray()){
			str.append(keepNotHidden(c) ? c : HIDDEN_CHAR);
		}
		return str;
	}
	
	/**
	 * tell if a character should be hidden or not.
	 *
	 * @param c The character.
	 *
	 * @return True if should not be hidden, false otherwise.
	 */
	private static boolean keepNotHidden(final char c){
		return c == ' ' || c == '-';
	}
	
	/**
	 * Select a random word.
	 *
	 * @return The chosen word.
	 */
	private String selectRandomWord(){
		try{
			final List<String> words = new ArrayList<>(IOUtils.readLines(Main.class.getResource("/hangman/words.csv").openStream(), Charset.defaultCharset()));
			return words.get(ThreadLocalRandom.current().nextInt(words.size()));
		}
		catch(final IOException e){
			getLogger(getGuild()).error("Error getting random hangman word", e);
		}
		return "ERROR";
	}
	
	/**
	 * Discover a letter in the word.
	 *
	 * @param c The letter to try.
	 *
	 * @return The number of letters discovered in teh word.
	 */
	private int discoverLetter(char c){
		c = Character.toLowerCase(c);
		var count = 0;
		if(realWord != null){
			for(var i = 0; i < realWord.length(); i++){
				if(matchLetter(realWord.toLowerCase().charAt(i), c)){
					count++;
					hiddenWord.replace(i, i + 1, "" + realWord.charAt(i));
				}
			}
		}
		return count;
	}
	
	/**
	 * Display the hangman in the chat.
	 */
	private void displayHangman(){
		Actions.sendFile(new HangmanChannelConfig(guild).getObject(null), "/hangman/level_" + hangStage + ".png", hangStage + ".png");
	}
	
	/**
	 * Pick a random user to say a letter.
	 */
	private void pickRandomUser(){
		final var channel = new HangmanChannelConfig(guild).getObject(null);
		var members = Utilities.getMembersWithRole(role);
		if(members.size() > 1){
			members = members.stream().filter(member -> member.getUser().getIdLong() != waitingUser.getIdLong()).collect(Collectors.toList());
		}
		if(members.size() > 0){
			final var member = members.get(ThreadLocalRandom.current().nextInt(members.size()));
			waitingUser = member.getUser();
			try{
				Actions.sendMessage(channel, "L'élu est %s, c'est a lui d'indiquer la lettre que vous avez choisit grâce à la commande %spendu l <lettre>\n", member.getAsMention(), new PrefixConfig(guild).getObject("g?"));
			}
			catch(final IllegalArgumentException e){
				getLogger(getGuild()).error("Error getting prefix", e);
			}
			if(lastFuture != null){
				lastFuture.cancel(true);
			}
			lastFuture = executor.schedule(() -> {
				pickRandomUser();
				displayWord();
			}, 30, TimeUnit.SECONDS);
		}
	}
	
	/**
	 * Try to skip the current user.
	 *
	 * @param member The user that wants to skip.
	 */
	public void voteSkip(final Member member){
		if(member.getUser().getIdLong() == waitingUser.getIdLong()){
			pickRandomUser();
		}
	}
	
	/**
	 * Make a guess.
	 *
	 * @param member The user making the guess.
	 * @param letter The letter.
	 */
	public void guess(final Member member, final char letter){
		if(waitingUser != null && member.getUser().getIdLong() == waitingUser.getIdLong()){
			final var channel = new HangmanChannelConfig(guild).getObject(null);
			Actions.sendMessage(channel, "La lettre choisie est %c.", letter);
			final var changed = discoverLetter(letter);
			if(changed <= 0){
				if(!badTry.contains(letter)){
					badTry.add(letter);
				}
				Actions.sendMessage(channel, "Oh ché doumache, ça va finir plus vite que prévu.");
				displayWord();
				hangStage++;
				displayHangman();
				if(hangStage >= MAX_HANG_LEVEL){
					Actions.sendMessage(channel, "%s Oh bah alors? On est pas assez bon? Vous voulez un cookie? %s", Utilities.getEmoteMention("Arold"), Utilities.getEmoteMention("cookie"));
					new Thread(this::delayEndGame).start();
				}
				else{
					pickRandomUser();
				}
			}
			else{
				Actions.sendMessage(channel, "Clap... clap.... clap! Voici le mot: %s", hiddenWord);
				if(realWord.equalsIgnoreCase(hiddenWord.toString())){
					Actions.sendMessage(channel, ":tada: :tada: Bon on va dire qu'on a rien vu ok? :tada: :tada:");
					new Thread(this::delayEndGame).start();
				}
				else{
					displayHangman();
					pickRandomUser();
				}
			}
		}
	}
	
	/**
	 * Display the current cord with letters tried.
	 */
	public void displayWord(){
		Actions.sendMessage(new HangmanChannelConfig(guild).getObject(null), "Lettres déjà essayées: %s\nVoici le mot: %s", badTry, hiddenWord);
	}
	
	/**
	 * Called when a player joins the game.
	 *
	 * @param member The member that joined.
	 */
	public void onPlayerJoin(final Member member){
		playerCount++;
		final var builder = new EmbedBuilder();
		builder.setAuthor(member.getUser().getName(), null, member.getUser().getAvatarUrl());
		builder.setColor(Color.BLUE);
		builder.setTitle("Un nouveau joueur a rejoint la partie!");
		builder.setDescription("Pour les règles regardes le message pinné.");
		builder.addField("Utilisateur", member.getUser().getAsMention(), false);
		Actions.sendMessage(new HangmanChannelConfig(guild).getObject(null), builder.build());
	}
	
	@Override
	public void onUserUpdateOnlineStatus(final UserUpdateOnlineStatusEvent event){
		super.onUserUpdateOnlineStatus(event);
		if(event.getNewOnlineStatus() == OnlineStatus.OFFLINE && event.getMember().getGuild().getIdLong() == guild.getIdLong() && Utilities.hasRole(event.getMember(), role)){
			playerLeave(event.getMember());
		}
	}
	
	/**
	 * Make a player leave.
	 *
	 * @param member The member leaving.
	 */
	public void playerLeave(final Member member){
		playerCount--;
		Actions.removeRole(member, role);
		if(playerCount < 1){
			delayEndGame();
		}
	}
	
	/**
	 * Delay the ending then remove users and messages.
	 */
	private void delayEndGame(){
		getLogger(getGuild()).info("Ending hangman game");
		guild.getJDA().removeEventListener(this);
		waitingUser = null;
		executor.shutdownNow();
		try{
			Thread.sleep(10000);
		}
		catch(final InterruptedException e){
			getLogger(getGuild()).error("Error sleeping", e);
		}
		removeUsers();
	}
	
	/**
	 * Remove the users from the channel.
	 */
	private void removeUsers(){
		final var channel = new HangmanChannelConfig(guild).getObject(null);
		channel.getMembers().stream().filter(m -> m.getUser().getIdLong() != channel.getJDA().getSelfUser().getIdLong()).forEach(member -> channel.getGuild().getController().removeRolesFromMember(member, role).queue());
		games.remove(this);
		for(final var message : channel.getIterableHistory().cache(false)){
			message.delete().queue();
		}
	}
}
