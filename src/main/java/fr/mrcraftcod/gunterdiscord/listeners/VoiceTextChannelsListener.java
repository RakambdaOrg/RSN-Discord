package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.VoiceTextChannelsConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
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
			if(new VoiceTextChannelsConfig(event.getGuild()).getAsList().contains(event.getChannelJoined().getIdLong())){
				TextChannel channel = CHANNELS.computeIfAbsent(event.getChannelJoined().getIdLong(), id -> {
					ChannelAction creator = event.getGuild().getController().createTextChannel(event.getChannelJoined().getName());
					creator.setParent(event.getChannelJoined().getParent());
					creator.setTopic("Channel pour les utilisateurs du vocal du même nom");
					creator.addPermissionOverride(event.getGuild().getPublicRole(), List.of(), List.of(Permission.MESSAGE_READ));
					return (TextChannel) creator.complete();
				});
				
				channel.putPermissionOverride(event.getMember()).setAllow(Permission.MESSAGE_READ).queue();
				Actions.sendMessage(channel, "%s a rejoint le channel", Utilities.getMemberToLog(event.getMember()));
			}
		}
		catch(Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event){
		super.onGuildVoiceLeave(event);
		try{
			
			TextChannel channel = CHANNELS.get(event.getChannelLeft().getIdLong());
			if(Objects.nonNull(channel)){
				channel.putPermissionOverride(event.getMember()).setDeny(Permission.MESSAGE_READ).queue();
				if(channel.getMembers().isEmpty()){
					channel.delete().reason("No more users in the vocal").queue();
				}
			}
		}
		catch(Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
}
