package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.ModoRolesConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.VoiceTextChannelsAssociationConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.VoiceTextChannelsConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.*;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.List;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 31/07/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-07-31
 */
public class VoiceTextChannelsListener extends ListenerAdapter{
	@Override
	public void onGuildVoiceJoin(final GuildVoiceJoinEvent event){
		super.onGuildVoiceJoin(event);
		try{
			joinVoice(event, event.getChannelJoined());
		}
		catch(final InsufficientPermissionException ignored){
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceMove(final GuildVoiceMoveEvent event){
		super.onGuildVoiceMove(event);
		try{
			leaveVoice(event);
			joinVoice(event, event.getChannelJoined());
		}
		catch(final InsufficientPermissionException ignored){
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	/**
	 * Handle things when a user leaves a channel.
	 *
	 * @param event The event.
	 */
	private static void leaveVoice(final GuildVoiceUpdateEvent event){
		final var config = new VoiceTextChannelsAssociationConfig(event.getGuild());
		final var channelID = config.getValue(event.getChannelLeft().getIdLong());
		if(channelID != null){
			final var channel = event.getGuild().getTextChannelById(channelID);
			if(channel != null){
				if(event.getChannelLeft().getMembers().isEmpty()){
					channel.delete().reason("No more users in the vocal").queue();
					config.deleteKey(event.getChannelLeft().getIdLong());
					getLogger(event.getGuild()).info("Text channel {} deleted", channel);
				}
				else{
					channel.putPermissionOverride(event.getMember()).setDeny(Permission.MESSAGE_READ).queue();
					Actions.sendMessage(channel, "%s est parti", event.getMember().getAsMention());
				}
			}
		}
	}
	
	@Override
	public void onGuildVoiceLeave(final GuildVoiceLeaveEvent event){
		super.onGuildVoiceLeave(event);
		try{
			leaveVoice(event);
		}
		catch(final InsufficientPermissionException ignored){
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	/**
	 * Handle things when a user joins a channel.
	 *
	 * @param event        The event.
	 * @param voiceChannel The voice channel joined.
	 */
	private static void joinVoice(final GenericGuildVoiceEvent event, final VoiceChannel voiceChannel){
		if(new VoiceTextChannelsConfig(event.getGuild()).contains(voiceChannel.getIdLong())){
			final var config = new VoiceTextChannelsAssociationConfig(event.getGuild());
			TextChannel channel = null;
			final var channelID = config.getValue(voiceChannel.getIdLong());
			if(channelID != null){
				channel = event.getGuild().getTextChannelById(channelID);
			}
			
			if(channel == null){
				final var creator = event.getGuild().getController().createTextChannel(voiceChannel.getName()).setParent(voiceChannel.getParent()).setTopic("Channel pour les utilisateurs du vocal du même nom").addPermissionOverride(event.getGuild().getPublicRole(), List.of(), List.of(Permission.MESSAGE_READ));
				new ModoRolesConfig(event.getGuild()).getAsList().forEach(role -> creator.addPermissionOverride(role, List.of(Permission.MESSAGE_READ), List.of()).queue());
				channel = (TextChannel) creator.addPermissionOverride(event.getMember(), List.of(Permission.MESSAGE_READ), List.of()).complete();
				config.addValue(voiceChannel.getIdLong(), channel.getIdLong());
				getLogger(event.getGuild()).info("Text channel {} created", channel);
			}
			else{
				channel.putPermissionOverride(event.getMember()).setAllow(Permission.MESSAGE_READ).queue();
			}
			Actions.sendMessage(channel, "%s#%s a rejoint le channel", event.getMember().getUser().getName(), event.getMember().getUser().getDiscriminator());
		}
	}
}

