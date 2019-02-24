package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.settings.configs.TwitchIRCChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.entities.Guild;
import net.engio.mbassy.listener.Handler;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import java.util.Optional;

public class TwitchIRCListener{
	private final Guild guild;
	private final String user;
	
	public TwitchIRCListener(Guild guild, String user){
		this.guild = guild;
		this.user = user;
	}
	
	@Handler
	public void onMessageReceived(ChannelMessageEvent event){
		Optional.ofNullable(new TwitchIRCChannelConfig(getGuild()).getObject(null)).ifPresent(channel -> Actions.sendMessage(channel, String.format("%s: %s", event.getActor().getNick(), event.getMessage())));
	}
	
	private Guild getGuild(){
		return this.guild;
	}
	
	public String getUser(){
		return user;
	}
}
