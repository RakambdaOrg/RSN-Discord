package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.HangmanChannelConfig;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InvalidClassException;
import java.nio.charset.Charset;
import java.util.*;
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
	private static boolean waitingMsg;
	private static long waitingTime;
	private static long waitingID;
	private static int hangStage;
	private static int playerCount;
	private static StringBuilder hiddenWord;
	private static String realWord;
	private static List<Character> badTry;
	private static Role role;
	public static boolean resetting;
	
	public HangmanListener()
	{
		stop();
	}
	
	public static void stop()
	{
		inProgress = false;
		resetting = false;
		waitingMsg = false;
		hangStage = 0;
		playerCount = 0;
		hiddenWord = new StringBuilder();
		realWord = "";
	}
	
	public static boolean onPlayerJoin(Member member) throws InvalidClassException, NoValueDefinedException
	{
		playerCount++;
		Main.getJDA().getTextChannelById(new HangmanChannelConfig().getLong()).sendMessageFormat("%s a rejoint la partie! Pour les règles regardes le message pinné.", member.getUser().getAsMention()).queue();
		return true;
	}
	
	@SuppressWarnings("Duplicates")
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		super.onMessageReceived(event);
		try
		{
			if(inProgress && event.getChannel().getIdLong() == new HangmanChannelConfig().getLong())
			{
				if(waitingMsg)
				{
					boolean OK = false;
					if(event.getAuthor().getIdLong() == waitingID)
					{
						boolean STOP = false;
						waitingMsg = false;
						LinkedList<String> parts = new LinkedList<>(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
						String command = parts.poll();
						if("gh?lettre".equalsIgnoreCase(command))
						{
							if(parts.size() > 0)
							{
								OK = true;
								char c = parts.poll().charAt(0);
								event.getChannel().sendMessageFormat("La lettre choisie est %c.", c).queue();
								int changed = discoverLetter(c);
								if(changed <= 0)
								{
									if(!badTry.contains(c))
										badTry.add(c);
									event.getChannel().sendMessageFormat("Oh ché doumache, ça va finir plus vite que prévu.").queue();
									displayWord(event.getTextChannel());
									hangStage++;
									displayHangman(event.getTextChannel());
									if(hangStage >= MAX_HANG_LEVEL)
									{
										STOP = true;
										event.getChannel().sendMessageFormat(event.getGuild().getEmotesByName("Arold", true).get(0).getAsMention() + " Oh bah alors? On est pas assez bon? Vous voulez un cookie? " + event.getGuild().getEmotesByName("cookie", true).get(0).getAsMention()).queue();
										new Thread(() -> {
											try
											{
												Thread.sleep(10000);
											}
											catch(InterruptedException e)
											{
												e.printStackTrace();
											}
											removeUsers(event.getGuild(), event.getTextChannel());
											stop();
										}).start();
									}
									else
										pickRandomUser();
								}
								else
								{
									event.getChannel().sendMessageFormat("Clap... clap.... clap! Voici le mot: %s", hiddenWord).queue();
									if(realWord.equalsIgnoreCase(hiddenWord.toString()))
									{
										STOP = true;
										event.getChannel().sendMessageFormat(":tada: :tada: Bon on va dire qu'on a rien vu ok? :tada: :tada:").queue();
										new Thread(() -> {
											try
											{
												Thread.sleep(10000);
											}
											catch(InterruptedException e)
											{
												e.printStackTrace();
											}
											removeUsers(event.getGuild(), event.getTextChannel());
											stop();
										}).start();
									}
									else
									{
										displayHangman(event.getTextChannel());
										pickRandomUser();
									}
								}
							}
							else
							{
								event.getChannel().sendMessage("Faut entrer une lettre mon beau!").queue();
							}
						}
						else if("gh?leave".equalsIgnoreCase(command))
						{
							playerLeave(event.getGuild(), event.getMember());
						}
						else if("gh?skip".equalsIgnoreCase(command))
						{
							if(event.getMember().getUser().getIdLong() == waitingID)
								pickRandomUser();
						}
						else if("gh?mot".equalsIgnoreCase(command))
						{
							displayWord(event.getTextChannel());
						}
						if(!STOP)
							waitingMsg = true;
					}
					if(!OK && System.currentTimeMillis() - waitingTime > (MAX_WAIT_TIME * 1000))
					{
						pickRandomUser();
						displayWord(event.getTextChannel());
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void displayWord(TextChannel channel)
	{
		channel.sendMessageFormat("Lettres déjà essayées: %s\nVoici le mot: %s", getTried(), hiddenWord).queue();
	}
	
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
	
	private String getTried()
	{
		return badTry.toString();
	}
	
	private static void displayHangman(TextChannel channel)
	{
		channel.sendFile(Main.class.getResourceAsStream("/hangman/level_" + hangStage + ".png"), hangStage + ".png").queue();
		//channel.sendMessageFormat("Hang level: %d", hangStage).queue();
	}
	
	@Override
	public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event)
	{
		super.onUserUpdateOnlineStatus(event);
		if(inProgress)
		{
			if(event.getMember().getRoles().contains(role))
			{
				try
				{
					playerLeave(event.getGuild(), event.getMember());
				}
				catch(InvalidClassException | NoValueDefinedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private void playerLeave(Guild guild, Member member) throws InvalidClassException, NoValueDefinedException
	{
		playerCount--;
		guild.getController().removeRolesFromMember(member, role).queue();
		if(playerCount < 1)
		{
			removeUsers(guild, Main.getJDA().getTextChannelById(new HangmanChannelConfig().getLong()));
			stop();
		}
	}
	
	private void removeUsers(Guild guild, TextChannel channel)
	{
		resetting = true;
		try
		{
			Thread.sleep(5000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		channel.getMembers().stream().filter(m -> m.getUser().getIdLong() != Main.getJDA().getSelfUser().getIdLong()).forEach(member -> guild.getController().removeRolesFromMember(member, role).queue());
		for(Message message : channel.getIterableHistory().cache(false))
			message.delete().queue();
		try
		{
			Thread.sleep(15000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	private static boolean matchLetter(char c1, char c2)
	{
		return mapChar(c1) == mapChar(c2);
	}
	
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
	
	private static void pickRandomUser()
	{
		try
		{
			TextChannel channel = Main.getJDA().getTextChannelById(new HangmanChannelConfig().getLong());
			List<Member> members = role.getGuild().getMembersWithRoles(role);
			if(members.size() > 1)
				members = members.stream().filter(member -> member.getUser().getIdLong() != waitingID).collect(Collectors.toList());
			Member member = members.get(ThreadLocalRandom.current().nextInt(members.size()));
			waitingID = member.getUser().getIdLong();
			waitingMsg = true;
			waitingTime = System.currentTimeMillis();
			channel.sendMessageFormat("L'élu est %s, c'est a lui d'indiquer la lettre que vous avez choisit grâce à la commande gh?lettre <lettre>\n", member.getAsMention()).queue();
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void setUp(Guild guild)
	{
		if(inProgress)
			return;
		stop();
		List<Role> roles = guild.getRolesByName("pendu", true);
		if(roles.size() <= 0)
			return;
		role = roles.get(0);
		inProgress = true;
		badTry = new ArrayList<>();
		try
		{
			TextChannel channel = Main.getJDA().getTextChannelById(new HangmanChannelConfig().getLong());
			channel.sendMessageFormat("Salut à tous! Si vous êtes la c'est que vous êtes chaud pour un petit pendu. Mais j'espère que vous êtes bons, sinon c'est vous qui allez finir pendu (au bout de %d fautes)!\n\nLe principe est simple. Je vais commencer par choisir un mot dans ma petite tête. Ensuite je vous l'écrirais avec les lettres cachées. Seul " + DISCOVER_START + " lettres seront apparentes au debut. Une fois cela fait, je désignerai une personne afin de me dire la lettre que vous voulez essayer, à vous de vous entendre afin de faire les bons choix.\n\nSi une personne ne déclare pas de choix en " + MAX_WAIT_TIME + "s, écrivez un petit mot et je referai tourner la roue pour désigner un représentant. Si vous désirez quitter, utilisez `gh?leave`. Si vous voulez passer votre tour utilisez `gh?skip`. Pour avoir des infos sur le mot actuel utilisez `gh?mot`.\n\n\nAlley, laissez moi réfléchir!", MAX_HANG_LEVEL).queue(message -> message.pin().queue());
			
			new Thread(() -> {
				try
				{
					Thread.sleep(5000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				realWord = selectRandomWord();
				hiddenWord = genHidden(realWord);
				List<Character> chars = realWord.chars().map(c -> mapChar((char) c)).distinct().boxed().map(i -> (char) (int) i).collect(Collectors.toList());
				Collections.shuffle(chars);
				for(int i = 0; i < DISCOVER_START; i++)
					if(chars.size() > i)
						discoverLetter((char) ((int) chars.get(i)));
				channel.sendMessageFormat("Le mot que j'ai choisi est: %s\n", hiddenWord.toString()).queue();
				displayHangman(channel);
				pickRandomUser();
			}).start();
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			e.printStackTrace();
		}
	}
	
	private static String selectRandomWord()
	{
		try
		{
			List<String> words = new ArrayList<>(IOUtils.readLines(Main.class.getResource("/hangman/words.csv").openStream(), Charset.defaultCharset()));
			return words.get(ThreadLocalRandom.current().nextInt(words.size()));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return "ERROR";
	}
	
	private static StringBuilder genHidden(String word)
	{
		StringBuilder str = new StringBuilder();
		for(char c : word.toCharArray())
			str.append(keepNotHidden(c) ? c : HIDDEN_CHAR);
		return str;
	}
	
	private static boolean keepNotHidden(char c)
	{
		return c == ' ' || c == '-';
	}
}
