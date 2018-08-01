package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.ModoRolesConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.VoiceTextChannelsConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.restaction.ChannelAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 31/07/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-07-31
 */
public class VoiceTextChannelsListener extends ListenerAdapter{
	private static final Map<Long, TextChannel> CHANNELS = new HashMap<>();
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event){
		super.onGuildVoiceJoin(event);
		try{
			if(new VoiceTextChannelsConfig(event.getGuild()).contains(event.getChannelJoined().getIdLong())){
				TextChannel channel = CHANNELS.computeIfAbsent(event.getChannelJoined().getIdLong(), id -> {
					ChannelAction creator = event.getGuild().getController().createTextChannel(event.getChannelJoined().getName());
					creator.setParent(event.getChannelJoined().getParent());
					creator.setTopic("Channel pour les utilisateurs du vocal du même nom");
					creator.addPermissionOverride(event.getGuild().getPublicRole(), List.of(), List.of(Permission.MESSAGE_READ));
					for(Role role : new ModoRolesConfig(event.getGuild()).getAsList()){
						creator.addPermissionOverride(role, List.of(Permission.MESSAGE_READ), List.of());
					}
					return (TextChannel) creator.complete();
				});
				
				channel.putPermissionOverride(event.getMember()).setAllow(Permission.MESSAGE_READ).queue();
				Actions.sendMessage(channel, "%s a rejoint le channel", event.getMember().getAsMention());
			}
		}
		catch(Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event){
		super.onGuildVoiceMove(event);
		try{
			leaveVoice(event);
		}
		catch(Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event){
		super.onGuildVoiceLeave(event);
		try{
			leaveVoice(event);
		}
		catch(Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	/**
	 * Handle things when a user leaves a channel.
	 *
	 * @param event The event.
	 */
	private static void leaveVoice(GuildVoiceUpdateEvent event){
		TextChannel channel = CHANNELS.get(event.getChannelLeft().getIdLong());
		if(Objects.nonNull(channel)){
			channel.putPermissionOverride(event.getMember()).setDeny(Permission.MESSAGE_READ).queue();
			if(event.getChannelLeft().getMembers().isEmpty()){
				channel.delete().reason("No more users in the vocal").queue();
				CHANNELS.remove(event.getChannelLeft().getIdLong());
			}
		}
	}
}

