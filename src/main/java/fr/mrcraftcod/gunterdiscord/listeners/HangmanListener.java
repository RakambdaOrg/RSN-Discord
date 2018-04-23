package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.HangmanChannelConfig;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.InvalidClassException;
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
	private static final long MAX_WAIT_TIME = 1000 * 60;
	private static final int MAX_HANG_LEVEL = 7;
	private static boolean inProgress;
	private static boolean waitingMsg;
	private static long waitingTime;
	private static long waitingID;
	private static int hangStage;
	private static StringBuilder hiddenWord;
	private static String realWord;
	private static List<Character> badTry;
	private static Role role;
	
	public HangmanListener()
	{
		stop();
	}
	
	public static void stop()
	{
		inProgress = false;
		waitingMsg = false;
		hangStage = 0;
		hiddenWord = new StringBuilder();
		realWord = "";
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
						if("gh?lettre".equalsIgnoreCase(parts.poll()))
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
									event.getChannel().sendMessageFormat("Oh ché doumache, ça va finir plus vite que prévu.\nLettres déjà essayées: %s\nVoici le mot: %s", getTried(), hiddenWord).queue();
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
										pickRandomUser();
								}
							}
							else
							{
								event.getChannel().sendMessage("Faut entrer une lettre mon beau!").queue();
							}
						}
						if(!STOP)
							waitingMsg = true;
					}
					if(!OK && System.currentTimeMillis() - waitingTime > MAX_WAIT_TIME)
					{
						pickRandomUser();
						event.getChannel().sendMessageFormat("Voici le mot: %s", hiddenWord).queue();
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static int discoverLetter(char c)
	{
		c = Character.toLowerCase(c);
		int count = 0;
		if(realWord != null)
			for(int i = 0; i < realWord.length(); i++)
				if(realWord.toLowerCase().charAt(i) == c)
				{
					count++;
					hiddenWord.replace(i, i + 1, "" + c);
				}
		return count;
	}
	
	private String getTried()
	{
		return badTry.toString();
	}
	
	private void displayHangman(TextChannel channel)
	{
		channel.sendMessageFormat("Hang level: %d", hangStage).queue();
	}
	
	private void removeUsers(Guild guild, TextChannel channel)
	{
		try
		{
			Thread.sleep(5000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		channel.getMembers().stream().filter(m -> m.getUser().getIdLong() != Main.getJDA().getSelfUser().getIdLong()).forEach(member -> guild.getController().removeRolesFromMember(member, role).queue());
	}
	
	private static void pickRandomUser()
	{
		try
		{
			TextChannel channel = Main.getJDA().getTextChannelById(new HangmanChannelConfig().getLong());
			List<Member> members = role.getGuild().getMembersWithRoles(role);
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
			channel.sendMessageFormat("Salut à tous @here! Si vous êtes la c'est que vous êtes chaud pour un petit pendu. Mais j'espère que vous êtes bons, sinon c'est vous qui allez finir pendu!\n\nLe principe est simple. Je vais commencer par choisir un mot dans ma petite tête. Ensuite je vous l'écrirais avec les lettres cachées. Seul " + DISCOVER_START + " lettres seront apparentes au debut. Une fois cela fait, je désignerai une personne afin de me dire la lettre que vous voulez essayer, à vous de vous entendre afin de faire les bons choix.\n\nSi une personne ne déclare pas de choix en 60s, écrivez un petit mot et je referai tourner la roue pour désigner un représentant.\n\n\nAlley, laissez moi réfléchir!").queue();
			
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
				List<Character> chars = realWord.chars().distinct().boxed().map(i -> (char) (int) i).collect(Collectors.toList());
				Collections.shuffle(chars);
				for(int i = 0; i < DISCOVER_START; i++)
					if(chars.size() > i)
						discoverLetter((char) ((int) chars.get(i)));
				channel.sendMessageFormat("Le mot que j'ai choisi est: %s\n", hiddenWord.toString()).queue();
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
		List<String> words = new ArrayList<>();
		words.add("flavescent");
		words.add("chalumeau");
		words.add("caligineux");
		words.add("immarcescible");
		words.add("hapax");
		words.add("nitescence");
		words.add("peccamineux");
		words.add("épectase");
		words.add("nivaal");
		words.add("perclus");
		words.add("obombrer");
		words.add("amphigouri");
		words.add("cyprine");
		return words.get(ThreadLocalRandom.current().nextInt(words.size()));
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
