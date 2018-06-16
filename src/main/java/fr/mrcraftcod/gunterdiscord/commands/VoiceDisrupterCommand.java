package fr.mrcraftcod.gunterdiscord.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.AudioPlayerSendHandler;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class VoiceDisrupterCommand extends BasicCommand
{
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " <@utilisateur>";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		event.getMessage().getMentionedUsers().stream().findAny().ifPresentOrElse(u -> {
			Member member = event.getGuild().getMember(u);
			if(member.getVoiceState().inVoiceChannel())
			{
				AudioManager audioManager = event.getGuild().getAudioManager();
				audioManager.openAudioConnection(member.getVoiceState().getChannel());
				AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
				audioPlayerManager.loadItem("https://youtu.be/KwrEx9046jM", new AudioLoadResultHandler()
				{
					@Override
					public void trackLoaded(AudioTrack audioTrack)
					{
						AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
						AudioPlayerSendHandler audioPlayerSendHandler = new AudioPlayerSendHandler(audioPlayer);
						audioManager.setSendingHandler(audioPlayerSendHandler);
						audioPlayer.playTrack(audioTrack);
						audioPlayer.addListener(audioEvent -> {
							if(!audioPlayerSendHandler.canProvide())
							{
								audioPlayer.destroy();
								audioPlayerManager.shutdown();
								audioManager.closeAudioConnection();
								Log.info("Shutdown sound");
							}
						});
					}
					
					@Override
					public void playlistLoaded(AudioPlaylist audioPlaylist)
					{
					
					}
					
					@Override
					public void noMatches()
					{
					
					}
					
					@Override
					public void loadFailed(FriendlyException e)
					{
						audioPlayerManager.shutdown();
						audioManager.closeAudioConnection();
						Log.warning("Shutdown sound - load failed", e);
					}
				});
			}
			else
				Actions.reply(event, "Cet utilisateur n'est pas dans un channel vocal");
		}, () -> Actions.reply(event, "Merci de mentionner un utilisateur valide"));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public void addHelp(Guild guild, EmbedBuilder builder)
	{
		super.addHelp(guild, builder);
		builder.addField("utilisateur", "L'utilisateur que le bot tentera de rejoindre", false);
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Fais chier";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("annoy");
	}
	
	@Override
	public String getDescription()
	{
		return "Rejoins un channel et fait du bruit";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ADMIN;
	}
}
