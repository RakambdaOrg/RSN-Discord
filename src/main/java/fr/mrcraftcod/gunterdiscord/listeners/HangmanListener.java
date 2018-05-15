package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.HangmanChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.PrefixConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import fr.mrcraftcod.gunterdiscord.utils.Roles;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.io.IOUtils;
import java.awt.*;
import java.io.IOException;
import java.io.InvalidClassException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-23
 */
public class HangmanListener extends ListenerAdapter
{
	private static final int DISCOVER_START = 2;
	private static final char HIDDEN_CHAR = '-';
	private static final long MAX_WAIT_TIME = 30;
	private static final int MAX_HANG_LEVEL = 6;
	private static boolean inProgress;
	private static boolean isWaitingMsg;
	private static long waitingTime;
	private static long waitingUserID;
	private static int hangStage;
	private static int playerCount;
	private static StringBuilder hiddenWord;
	private static String realWord;
	private static List<Character> badTry;
	private static List<Role> role;
	public static boolean resetting;
	
	/**
	 * Constructor.
	 */
	public HangmanListener()
	{
		stop();
	}
	
	/**
	 * Resets the game.
	 */
	public static void stop()
	{
		inProgress = false;
		resetting = false;
		isWaitingMsg = false;
		hangStage = 0;
		playerCount = 0;
		hiddenWord = new StringBuilder();
		realWord = "";
	}
	
	/**
	 * Called when a player joins the game.
	 *
	 * @param member The member that joined.
	 */
	public static void onPlayerJoin(Member member)
	{
		playerCount++;
		EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor(member.getUser().getName(), null, member.getUser().getAvatarUrl());
		builder.setColor(Color.BLUE);
		builder.setTitle(member.getAsMention() + " a rejoint la partie!");
		builder.setDescription("Pour les règles regardes le message pinné.");
		Actions.sendMessage(new HangmanChannelConfig().getTextChannel(member.getJDA()), builder.build());
	}
	
	@SuppressWarnings("Duplicates")
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		super.onMessageReceived(event);
		try
		{
			if(inProgress && event.getChannel().equals(new HangmanChannelConfig().getTextChannel(event.getJDA())))
			{
				if(isWaitingMsg)
				{
					boolean electedIsDoingSomething = false;
					if(event.getAuthor().getIdLong() == waitingUserID)
					{
						boolean stopWaitingMessage = false;
						isWaitingMsg = false;
						LinkedList<String> parts = new LinkedList<>(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
						String command = parts.poll();
						if(command != null && command.startsWith("h" + new PrefixConfig().getString()))
						{
							command = command.substring(("h" + new PrefixConfig().getString()).length());
							switch(command)
							{
								case "lettre":
									if(parts.size() > 0)
									{
										electedIsDoingSomething = true;
										String argWord = parts.poll();
										if(argWord == null)
											break;
										char c = argWord.charAt(0);
										Actions.sendMessage(event.getTextChannel(), "La lettre choisie est %c.", c);
										int changed = discoverLetter(c);
										if(changed <= 0)
										{
											if(!badTry.contains(c))
												badTry.add(c);
											Actions.reply(event, "Oh ché doumache, ça va finir plus vite que prévu.");
											displayWord(event.getTextChannel());
											hangStage++;
											displayHangman(event.getTextChannel());
											if(hangStage >= MAX_HANG_LEVEL)
											{
												stopWaitingMessage = true;
												Actions.sendMessage(event.getTextChannel(), "%s Oh bah alors? On est pas assez bon? Vous voulez un cookie? %s", event.getGuild().getEmotesByName("Arold", true).get(0).getAsMention(), event.getGuild().getEmotesByName("cookie", true).get(0).getAsMention());
												new Thread(() -> delayEndGame(event.getTextChannel())).start();
											}
											else
												pickRandomUser(event.getJDA());
										}
										else
										{
											Actions.sendMessage(event.getTextChannel(), "Clap... clap.... clap! Voici le mot: %s", hiddenWord);
											if(realWord.equalsIgnoreCase(hiddenWord.toString()))
											{
												stopWaitingMessage = true;
												Actions.reply(event, ":tada: :tada: Bon on va dire qu'on a rien vu ok? :tada: :tada:");
												new Thread(() -> delayEndGame(event.getTextChannel())).start();
											}
											else
											{
												displayHangman(event.getTextChannel());
												pickRandomUser(event.getJDA());
											}
										}
									}
									else
										Actions.reply(event, "Faut entrer une lettre mon beau!");
									break;
								case "leave":
									playerLeave(event.getMember());
									break;
								case "skip":
									if(event.getMember().getUser().getIdLong() == waitingUserID)
										pickRandomUser(event.getJDA());
									break;
								case "mot":
									displayWord(event.getTextChannel());
							}
						}
						if(!stopWaitingMessage)
							isWaitingMsg = true;
					}
					if(!electedIsDoingSomething && System.currentTimeMillis() - waitingTime > (MAX_WAIT_TIME * 1000))
					{
						pickRandomUser(event.getJDA());
						displayWord(event.getTextChannel());
					}
				}
			}
		}
		catch(Exception e)
		{
			Log.error("Error happened in teh hangman", e);
		}
	}
	
	/**
	 * Delay the ending then remove users and messages.
	 *
	 * @param channel The hangman channel.
	 */
	private void delayEndGame(TextChannel channel)
	{
		try
		{
			Thread.sleep(10000);
		}
		catch(InterruptedException e)
		{
			Log.error("Error sleeping", e);
		}
		removeUsers(channel);
		stop();
	}
	
	/**
	 * Display the current cord with letters tried.
	 *
	 * @param channel The channel.
	 */
	private void displayWord(TextChannel channel)
	{
		Actions.sendMessage(channel, "Lettres déjà essayées: %s\nVoici le mot: %s", badTry, hiddenWord);
	}
	
	/**
	 * Discover a letter in the word.
	 *
	 * @param c The letter to try.
	 *
	 * @return The number of letters discovered in teh word.
	 */
	private static int discoverLetter(char c)
	{
		c = Character.toLowerCase(c);
		int count = 0;
		if(realWord != null)
			for(int i = 0; i < realWord.length(); i++)
				if(matchLetter(realWord.toLowerCase().charAt(i), c))
				{
					count++;
					hiddenWord.replace(i, i + 1, "" + realWord.charAt(i));
				}
		return count;
	}
	
	/**
	 * Display the hangman in the chat.
	 *
	 * @param channel The channel to send into.
	 */
	private static void displayHangman(TextChannel channel)
	{
		Actions.sendFile(channel, "/hangman/level_" + hangStage + ".png", hangStage + ".png");
	}
	
	@Override
	public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event)
	{
		super.onUserUpdateOnlineStatus(event);
		if(inProgress && Utilities.hasRole(event.getMember(), role))
			playerLeave(event.getMember());
	}
	
	/**
	 * Make a player leave.
	 *
	 * @param member The member leaving.
	 */
	private void playerLeave(Member member)
	{
		playerCount--;
		Actions.removeRole(member, role);
		if(playerCount < 1)
		{
			removeUsers(new HangmanChannelConfig().getTextChannel(member.getJDA()));
			stop();
		}
	}
	
	/**
	 * Remove the users from the channel.
	 *
	 * @param channel The text channel.
	 */
	private void removeUsers(TextChannel channel)
	{
		resetting = true;
		try
		{
			Thread.sleep(5000);
		}
		catch(InterruptedException e)
		{
			Log.error("Error sleeping");
		}
		channel.getMembers().stream().filter(m -> m.getUser().getIdLong() != channel.getJDA().getSelfUser().getIdLong()).forEach(member -> channel.getGuild().getController().removeRolesFromMember(member, role).queue());
		for(Message message : channel.getIterableHistory().cache(false))
			message.delete().queue();
		try
		{
			Thread.sleep(15000);
		}
		catch(InterruptedException e)
		{
			Log.error("Error sleeping");
		}
	}
	
	/**
	 * Tells if 2 letters are the same.
	 *
	 * @param c1 First letter.
	 * @param c2 Second letter.
	 *
	 * @return True if matches, false otherwise.
	 */
	private static boolean matchLetter(char c1, char c2)
	{
		return mapChar(c1) == mapChar(c2);
	}
	
	/**
	 * Get a char base on its parent.
	 * For example letters with accent will become the letter without the accent.
	 *
	 * @param c The letter.
	 *
	 * @return The mapped letter.
	 */
	private static char mapChar(char c)
	{
		if(c == 'é' || c == 'è' || c == 'ê')
			return 'e';
		if(c == 'à' || c == 'â')
			return 'a';
		if(c == 'ì' || c == 'î')
			return 'i';
		if(c == 'ô' || c == 'ò')
			return 'o';
		if(c == 'û' || c == 'ù')
			return 'u';
		return c;
	}
	
	/**
	 * Pick a random user to say a letter.
	 *
	 * @param jda The JDA.
	 */
	private static void pickRandomUser(JDA jda)
	{
		TextChannel channel = new HangmanChannelConfig().getTextChannel(jda);
		List<Member> members = Utilities.getMembersRole(role);
		if(members.size() > 1)
			members = members.stream().filter(member -> member.getUser().getIdLong() != waitingUserID).collect(Collectors.toList());
		if(members.size() > 0)
		{
			Member member = members.get(ThreadLocalRandom.current().nextInt(members.size()));
			waitingUserID = member.getUser().getIdLong();
			isWaitingMsg = true;
			waitingTime = System.currentTimeMillis();
			try
			{
				Actions.sendMessage(channel, "L'élu est %s, c'est a lui d'indiquer la lettre que vous avez choisit grâce à la commande %c%slettre <lettre>\n", member.getAsMention(), 'h', new PrefixConfig().getString("g?"));
			}
			catch(InvalidClassException | IllegalArgumentException e)
			{
				Log.error("Error getting prefix", e);
			}
		}
	}
	
	/**
	 * Set up a new game.
	 *
	 * @param guild The guild the channel is in.
	 */
	public static void setUp(Guild guild)
	{
		if(inProgress)
			return;
		stop();
		List<Role> roles = Utilities.getRole(guild, Roles.HANGMAN);
		if(roles.size() <= 0)
			return;
		role = roles;
		inProgress = true;
		badTry = new ArrayList<>();
		try
		{
			String prefix = new PrefixConfig().getString();
			TextChannel channel = new HangmanChannelConfig().getTextChannel(guild.getJDA());
			Actions.sendMessage(channel, Actions.PIN_MESSAGE, "Salut à tous! Si vous êtes la c'est que vous êtes chaud pour un petit pendu. Mais j'espère que vous êtes bons, sinon c'est vous qui allez finir pendu (au bout de %d fautes)!\n\nLe principe est simple. Je vais commencer par choisir un mot dans ma petite tête. Ensuite je vous l'écrirais avec les lettres cachées. Seul %d lettres seront apparentes au debut. Une fois cela fait, je désignerai une personne afin de me dire la lettre que vous voulez essayer, à vous de vous entendre afin de faire les bons choix.\n\nSi une personne ne déclare pas de choix en %ds, écrivez un petit mot et je referai tourner la roue pour désigner un représentant. Si vous désirez quitter, utilisez `h%sleave`. Si vous voulez passer votre tour utilisez `h%sskip`. Pour avoir des infos sur le mot actuel utilisez `" + "h%smot`.\n\n\nAlley, laissez moi réfléchir!", MAX_HANG_LEVEL, DISCOVER_START, MAX_WAIT_TIME, prefix, prefix, prefix);
			
			new Thread(() -> {
				try
				{
					Thread.sleep(5000);
				}
				catch(InterruptedException e)
				{
					Log.error("Error sleeping");
				}
				realWord = selectRandomWord();
				hiddenWord = genHidden(realWord);
				List<Character> chars = realWord.chars().map(c -> mapChar((char) c)).distinct().boxed().map(i -> (char) (int) i).collect(Collectors.toList());
				Collections.shuffle(chars);
				for(int i = 0; i < DISCOVER_START; i++)
					if(chars.size() > i)
						discoverLetter((char) ((int) chars.get(i)));
				Actions.sendMessage(channel, "Le mot que j'ai choisi est: %s", hiddenWord);
				displayHangman(channel);
				pickRandomUser(channel.getJDA());
			}).start();
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			Log.error("Error getting prefix", e);
		}
	}
	
	/**
	 * Select a random word.
	 *
	 * @return The chosen word.
	 */
	private static String selectRandomWord()
	{
		try
		{
			List<String> words = new ArrayList<>(IOUtils.readLines(Main.class.getResource("/hangman/words.csv").openStream(), Charset.defaultCharset()));
			return words.get(ThreadLocalRandom.current().nextInt(words.size()));
		}
		catch(IOException e)
		{
			Log.error("Error getting random hangman word", e);
		}
		return "ERROR";
	}
	
	/**
	 * Get the word as being hidden.
	 *
	 * @param word The word to hide.
	 *
	 * @return The hidden word.
	 */
	private static StringBuilder genHidden(String word)
	{
		StringBuilder str = new StringBuilder();
		for(char c : word.toCharArray())
			str.append(keepNotHidden(c) ? c : HIDDEN_CHAR);
		return str;
	}
	
	/**
	 * tell if a character should be hidden or not.
	 *
	 * @param c The character.
	 *
	 * @return True if should not be hidden, false otherwise.
	 */
	private static boolean keepNotHidden(char c)
	{
		return c == ' ' || c == '-';
	}
}
