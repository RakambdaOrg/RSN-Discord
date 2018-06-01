package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.WerewolvesChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-31
 */
public class WerewolvesListener extends ListenerAdapter
{
	public static final int MIN_PLAYER = 4;
	private static final ArrayList<WerewolvesListener> games = new ArrayList<>();
	private final List<Member> willDie;
	private final TextChannel werewolvesTextChannel;
	private final TextChannel textChannel;
	private final HashMap<Member, Integer> votes;
	private final VoiceChannel voiceChannel;
	private final LinkedHashMap<Member, WerewolvesRole> users;
	private final List<Member> waitingMember;
	private int cycle;
	private Function<Message, Boolean> waitingAction = null;
	
	private enum WerewolvesRoleKind
	{
		SPECIAL, WEREWOLVES, VILLAGERS
	}
	
	private enum WerewolvesRole
	{
		SPECTATOR(WerewolvesRoleKind.SPECIAL), VILLAGER(WerewolvesRoleKind.VILLAGERS), WEREWOLF(WerewolvesRoleKind.WEREWOLVES), SEER(WerewolvesRoleKind.VILLAGERS), WITCH(WerewolvesRoleKind.VILLAGERS), HUNTER(WerewolvesRoleKind.VILLAGERS);
		
		private final WerewolvesRoleKind kind;
		private boolean mayor;
		
		WerewolvesRole(WerewolvesRoleKind kind)
		{
			this.kind = kind;
			mayor = false;
		}
		
		public WerewolvesRoleKind getKind()
		{
			return kind;
		}
		
		public boolean isMayor()
		{
			return mayor;
		}
		
		public void setMayor(boolean mayor)
		{
			this.mayor = mayor;
		}
	}
	
	private enum WerewolvesPhase
	{
		DAY, NIGHT
	}
	
	private WerewolvesListener(VoiceChannel channel) throws IllegalStateException
	{
		if(channel.getMembers().size() < MIN_PLAYER)
			throw new IllegalStateException("Pas assez de joueurs");
		textChannel = new WerewolvesChannelConfig().getTextChannel(channel.getGuild());
		if(textChannel == null)
			throw new IllegalStateException("Le channel n'est pas configuré");
		cycle = 0;
		votes = new HashMap<>();
		games.add(this);
		channel.getJDA().addEventListener(this);
		werewolvesTextChannel = (TextChannel) channel.getGuild().getController().createTextChannel("TEMP-WEREWOLVES").addPermissionOverride(channel.getGuild().getPublicRole(), null, List.of(Permission.MESSAGE_READ)).complete();
		this.voiceChannel = channel;
		willDie = new ArrayList<>();
		waitingMember = new ArrayList<>();
		users = new LinkedHashMap<>();
		channel.getMembers().forEach(m -> users.put(m, WerewolvesRole.SPECTATOR));
		asignRoles();
		users.forEach((m, r) -> Actions.replyPrivate(m.getUser(), "Votre role est: %s", r.name()));
		setPhase(WerewolvesPhase.NIGHT);
	}
	
	/**
	 * Get the game for a guild.
	 *
	 * @param channel The voiceChannel concerned.
	 *
	 * @return The game.
	 */
	public static Optional<WerewolvesListener> getGame(VoiceChannel channel)
	{
		return getGame(channel, true);
	}
	
	/**
	 * Get the game for a guild.
	 *
	 * @param channel The voiceChannel concerned.
	 * @param create  If a game is created if it doesn't exists.
	 *
	 * @return The game.
	 *
	 * @throws IllegalStateException If the game couldn't be created.
	 */
	public static Optional<WerewolvesListener> getGame(VoiceChannel channel, boolean create) throws IllegalStateException
	{
		return games.stream().filter(h -> h.getVoiceChannel().getIdLong() == channel.getIdLong()).findAny().or(() -> {
			if(create)
				return Optional.of(new WerewolvesListener(channel));
			return Optional.empty();
		});
	}
	
	private VoiceChannel getVoiceChannel()
	{
		return voiceChannel;
	}
	
	private void asignRoles()
	{
		Log.info("Werewolves: assigning roles");
		LinkedList<WerewolvesRole> roles = new LinkedList<>();
		roles.add(WerewolvesRole.SEER);
		roles.add(WerewolvesRole.HUNTER);
		roles.add(WerewolvesRole.WITCH);
		IntStream.range(0, (int) Math.ceil((users.size() - roles.size()) / 4.0)).forEach(i -> roles.add(WerewolvesRole.WEREWOLF));
		IntStream.range(0, users.size() - roles.size()).forEach(i -> roles.add(WerewolvesRole.VILLAGER));
		Collections.shuffle(roles);
		users.keySet().forEach(m -> users.put(m, roles.poll()));
	}
	
	private void electMayor()
	{
		Log.info("Werewolves: voting mayor");
		Actions.sendMessage(textChannel, Utilities.buildEmbed(null, Color.GREEN, "Vote du maire").addField("Le vote du maire est ouvert", "Envoyez en mp au bot votre vote", false).build());
		votes.clear();
		waitingMember.addAll(users.keySet().stream().filter(m -> users.get(m).getKind() != WerewolvesRoleKind.SPECIAL).collect(Collectors.toList()));
		waitingAction = s -> {
			Member m2 = getMember(s.getContentRaw());
			if(m2 == null)
				Actions.replyPrivate(s.getAuthor(), "Désolé, je ne connais pas cette personne ou l'action est inconnue");
			else
			{
				if(users.get(m2).getKind() == WerewolvesRoleKind.SPECIAL)
				{
					Actions.replyPrivate(s.getAuthor(), "Désolé, vous ne pouvez pas cibler cette personne");
					return false;
				}
				if(!votes.containsKey(m2))
					votes.put(m2, 0);
				votes.put(m2, votes.get(m2) + 1);
				waitingMember.remove(s.getMember());
				boolean finished = waitingMember.isEmpty();
				if(finished)
				{
					votes.keySet().stream().min(Comparator.comparingInt(votes::get)).ifPresent(m -> {
						users.get(m).setMayor(true);
						Actions.sendMessage(textChannel, "Le maire est maintenant %s", m.getAsMention());
					});
					electKilled();
				}
				else
					Actions.sendMessage(textChannel, "Encore %d votes", waitingMember.size());
				return finished;
			}
			return false;
		};
	}
	
	private void electKilled()
	{
		Log.info("Werewolves: voting kill");
		Actions.sendMessage(textChannel, Utilities.buildEmbed(null, Color.GREEN, "Vote du tué du jour").addField("Le vote du maire est ouvert", "Envoyez en mp au bot votre vote", false).build());
		votes.clear();
		waitingMember.addAll(users.keySet().stream().filter(m -> users.get(m).getKind() != WerewolvesRoleKind.SPECIAL).collect(Collectors.toList()));
		waitingAction = s -> {
			Member m2 = getMember(s.getContentRaw());
			if(m2 == null)
				Actions.replyPrivate(s.getAuthor(), "Désolé, je ne connais pas cette personne ou l'action est inconnue");
			else
			{
				if(users.get(m2).getKind() == WerewolvesRoleKind.SPECIAL)
				{
					Actions.replyPrivate(s.getAuthor(), "Désolé, vous ne pouvez pas cibler cette personne");
					return false;
				}
				if(!votes.containsKey(m2))
					votes.put(m2, 0);
				votes.put(m2, votes.get(m2) + (users.get(s.getMember()).isMayor() ? 2 : 1));
				waitingMember.remove(s.getMember());
				boolean finished = waitingMember.isEmpty();
				if(finished)
				{
					votes.keySet().stream().min(Comparator.comparingInt(votes::get)).ifPresent(willDie::add);
					kill(() -> setPhase(WerewolvesPhase.NIGHT));
				}
				else
					Actions.sendMessage(textChannel, "Encore %d votes", waitingMember.size());
				return finished;
			}
			return false;
		};
	}
	
	private void kill(Runnable runnable)
	{
		AtomicBoolean run = new AtomicBoolean(true);
		willDie.forEach(m -> {
			users.put(m, WerewolvesRole.SPECTATOR);
			Actions.replyPrivate(m.getUser(), "Vous êtes mort");
			if(users.get(m).isMayor())
			{
				Log.info("Werewolves: mayor is dead");
				Actions.sendMessage(textChannel, "Votre maire est mort, nous attendons un nouveau maire");
				Actions.replyPrivate(m.getUser(), "Veuillez désigner un nouveau maire");
				run.set(false);
				waitingMember.clear();
				waitingMember.add(m);
				waitingAction = s -> {
					Member m2 = getMember(s.getContentRaw());
					if(m2 == null)
						Actions.replyPrivate(m.getUser(), "Désolé, je ne connais pas cette personne ou l'action est inconnue");
					else
					{
						if(users.get(m2).getKind() == WerewolvesRoleKind.SPECIAL)
						{
							Actions.replyPrivate(m.getUser(), "Désolé, vous ne pouvez pas cibler cette personne");
							return false;
						}
						users.get(m2).setMayor(true);
						Actions.sendMessage(textChannel, "Le nouveau maire est: %s", m2.getAsMention());
						runnable.run();
						return true;
					}
					return false;
				};
			}
		});
		Actions.sendMessage(textChannel, "Les morts sont : %s", willDie.stream().map(IMentionable::getAsMention).collect(Collectors.joining(", ")));
		willDie.clear();
		if(!verifyEnd() && run.get())
			runnable.run();
	}
	
	private void roleHunter()
	{
		willDie.stream().filter(m -> users.get(m) == WerewolvesRole.HUNTER).findAny().ifPresentOrElse(m -> {
			Actions.sendMessage(textChannel, "En attente de: " + WerewolvesRole.HUNTER.name());
			Actions.replyPrivate(m.getUser(), "Vous allez mourrir, désignez la personne que vous tuez avec votre pistolet.");
			waitingMember.clear();
			waitingMember.add(m);
			waitingAction = s -> {
				Member m2 = getMember(s.getContentRaw());
				if(m2 == null)
					Actions.replyPrivate(m.getUser(), "Désolé, je ne connais pas cette personne ou l'action est inconnue");
				else
				{
					if(users.get(m2).getKind() == WerewolvesRoleKind.SPECIAL || willDie.contains(m2))
					{
						Actions.replyPrivate(m.getUser(), "Désolé, vous ne pouvez pas cibler cette personne");
						return false;
					}
					willDie.add(m2);
					setPhase(WerewolvesPhase.DAY);
					return true;
				}
				return false;
			};
		}, () -> setPhase(WerewolvesPhase.DAY));
	}
	
	private void roleWitch()
	{
		users.keySet().stream().filter(m -> users.get(m) == WerewolvesRole.WITCH).findAny().ifPresentOrElse(m -> {
			Actions.sendMessage(textChannel, "En attente de: " + WerewolvesRole.WITCH.name());
			Actions.replyPrivate(m.getUser(), "Si tu veux ressuciter la victime, envoie `R`, si tu veux empoisoner quelqu'un, envoie son nom `K<nom>`, sinon envoi moi `0`");
			waitingAction = s -> {
				Member m2;
				if(s.getContentRaw().equals("0"))
				{
				}
				else if(s.getContentRaw().equals("R"))
					willDie.clear();
				else if(s.getContentRaw().startsWith("K") && (m2 = getMember(s.getContentRaw().substring(1))) != null)
				{
					if(users.get(m2).getKind() == WerewolvesRoleKind.SPECIAL)
					{
						Actions.replyPrivate(m.getUser(), "Désolé, vous ne pouvez pas cibler cette personne");
						return false;
					}
					else
						willDie.add(m2);
				}
				else
				{
					Actions.replyPrivate(m.getUser(), "Désolé, je ne connais pas cette personne ou l'action est inconnue");
					return false;
				}
				setRole(WerewolvesRole.HUNTER);
				return true;
			};
			waitingMember.clear();
			waitingMember.add(m);
		}, () -> setRole(WerewolvesRole.HUNTER));
	}
	
	private void roleSeer()
	{
		users.keySet().stream().filter(m -> users.get(m) == WerewolvesRole.SEER).findAny().ifPresent(m -> Actions.replyPrivate(m.getUser(), "La personne qui va mourir est: %s", willDie.stream().map(IMentionable::getAsMention).collect(Collectors.joining(", "))));
		setRole(WerewolvesRole.WITCH);
	}
	
	private Member getMember(String name)
	{
		return voiceChannel.getMembers().stream().filter(m -> (m.getNickname() != null && m.getNickname().equals(name)) || m.getUser().getName().equals(name)).findAny().orElse(null);
	}
	
	private void roleWerewolves()
	{
		users.keySet().stream().filter(m -> users.get(m).getKind() == WerewolvesRoleKind.WEREWOLVES).peek(m -> {
			try
			{
				werewolvesTextChannel.putPermissionOverride(m).setAllow(Permission.MESSAGE_READ).queue();
			}
			catch(Exception e)
			{
				Log.error("", e);
			}
		}).findAny().ifPresent(m -> {
			Actions.sendMessage(textChannel, "En attente de: " + WerewolvesRole.WEREWOLF.name());
			Actions.replyPrivate(m.getUser(), "C'est à votre tour les loups de choisir votre victime. Décidez entre vous et donne moi le nom de la personne que vous avez choisie");
			waitingAction = s -> {
				Member m2 = getMember(s.getContentRaw());
				if(m2 == null)
					Actions.replyPrivate(m.getUser(), "Désolé, je ne connais pas cette personne");
				else
				{
					WerewolvesRole r2 = users.get(m2);
					if(r2.getKind() == WerewolvesRoleKind.WEREWOLVES || r2.getKind() == WerewolvesRoleKind.SPECIAL)
						Actions.replyPrivate(m.getUser(), "Désolé, vous ne pouvez pas cibler cette personne");
					else
					{
						willDie.add(m2);
						users.keySet().stream().filter(mm -> users.get(mm).getKind() == WerewolvesRoleKind.WEREWOLVES).forEach(mm -> {
							try
							{
								werewolvesTextChannel.putPermissionOverride(mm).setDeny(Permission.MESSAGE_READ).queue();
							}
							catch(Exception e)
							{
								Log.error("", e);
							}
						});
						clearChannel(werewolvesTextChannel);
						setRole(WerewolvesRole.SEER);
						return true;
					}
				}
				return false;
			};
			waitingMember.clear();
			waitingMember.add(m);
		});
	}
	
	private void clearChannel(TextChannel channel)
	{
		for(Message message : channel.getIterableHistory().cache(false))
			message.delete().queue();
	}
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
	{
		super.onPrivateMessageReceived(event);
		if(waitingMember != null && waitingMember.stream().anyMatch(m -> m.getUser().getIdLong() == event.getAuthor().getIdLong()))
		{
			Log.info("Werewolves: " + Actions.getUserToLog(event.getAuthor()) + " said `" + event.getMessage().getContentRaw() + "`");
			if(waitingAction != null)
				waitingAction.apply(event.getMessage());
		}
	}
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event)
	{
		super.onGuildVoiceJoin(event);
		if(event.getChannelJoined().getIdLong() == voiceChannel.getIdLong())
		{
			users.put(event.getMember(), WerewolvesRole.SPECTATOR);
			event.getGuild().getController().setMute(event.getMember(), true).queue();
			Log.info("Werewolves: User " + Actions.getUserToLog(event.getMember().getUser()) + " joined as spectator");
		}
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event)
	{
		super.onGuildVoiceLeave(event);
		if(event.getChannelLeft().getIdLong() == werewolvesTextChannel.getIdLong())
		{
			users.remove(event.getMember());
			verifyEnd();
		}
	}
	
	private boolean verifyEnd()
	{
		if(users.isEmpty())
		{
			Actions.sendMessage(textChannel, "La partie de loups garous est terminée car plus personne n'est présent");
			stop();
			return true;
		}
		else if(users.values().stream().filter(r -> r.getKind() == WerewolvesRoleKind.WEREWOLVES).count() < 1)
		{
			Actions.sendMessage(textChannel, "La partie de loups garous est terminée car plus aucun villageois n'est présent");
			stop();
			return true;
		}
		else if(users.values().stream().filter(r -> r.getKind() == WerewolvesRoleKind.VILLAGERS).count() < 1)
		{
			Actions.sendMessage(textChannel, "La partie de loups garous est terminée car plus aucun loup-garou n'est présent");
			stop();
			return true;
		}
		return false;
	}
	
	public void stop()
	{
		Log.info("Werewolves: stopping");
		games.remove(this);
		voiceChannel.getJDA().removeEventListener(this);
		werewolvesTextChannel.delete().queue();
	}
	
	private void setPhase(WerewolvesPhase phase)
	{
		if(phase == WerewolvesPhase.NIGHT)
		{
			Actions.sendMessage(textChannel, "Bienvenue dans la nuit!");
			users.keySet().stream().filter(m -> users.get(m).getKind() != WerewolvesRoleKind.SPECIAL).forEach(m -> {
				try
				{
					voiceChannel.getGuild().getController().setDeafen(m, true).queue();
				}
				catch(Exception e)
				{
					Log.error("", e);
				}
			});
			setRole(WerewolvesRole.WEREWOLF);
		}
		else
		{
			Actions.sendMessage(textChannel, "Bienvenue dans le jour!");
			cycle++;
			users.keySet().stream().filter(m -> users.get(m).getKind() != WerewolvesRoleKind.SPECIAL).forEach(m -> {
				try
				{
					voiceChannel.getGuild().getController().setDeafen(m, false).queue();
				}
				catch(Exception e)
				{
					Log.error("", e);
				}
			});
			kill(() -> {
				if(cycle == 1)
					electMayor();
				else
					electKilled();
			});
		}
	}
	
	private void setRole(WerewolvesRole role)
	{
		switch(role)
		{
			case WEREWOLF:
				roleWerewolves();
				break;
			case SEER:
				roleSeer();
				break;
			case WITCH:
				roleWitch();
				break;
			case HUNTER:
				roleHunter();
				break;
		}
	}
}
