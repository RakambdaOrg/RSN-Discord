package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class AnnoyCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("utilisateur", "L'utilisateur que le bot tentera de rejoindre", false);
	}
	
	@Override
	public CommandResult execute(@NotNull MessageReceivedEvent event, @NotNull LinkedList<String> args) throws Exception{
		super.execute(event, args);
		String arg1 = args.poll();
		if(Objects.equals(arg1, "stop")){
			GunterAudioManager.leave(event.getGuild());
		}
		else{
			event.getMessage().getMentionedUsers().stream().findAny().ifPresentOrElse(u -> {
				
				Member member = event.getGuild().getMember(u);
				if(member.getVoiceState().inVoiceChannel()){
					String identifier = String.join(" ", args).trim();
					GunterAudioManager.play(member.getVoiceState().getChannel(), identifier.equals("") ? "https://www.youtube.com/watch?v=J4X2b-CEGNg" : identifier);
				}
				else{
					Actions.reply(event, "Cet utilisateur n'est pas dans un channel vocal");
				}
			}, () -> Actions.reply(event, "Merci de mentionner un utilisateur valide"));
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@utilisateur>";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Override
	public String getName(){
		return "Fais chier";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("annoy");
	}
	
	@Override
	public String getDescription(){
		return "Rejoins un channel et fait du bruit";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
