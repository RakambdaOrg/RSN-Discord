package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The target of the prank", false);
	}
	
	@Override
	public CommandResult execute(final GuildMessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		args.poll();
		event.getMessage().getMentionedUsers().stream().findAny().ifPresentOrElse(u -> {
			final var member = event.getGuild().getMember(u);
			if(member.getVoiceState().inVoiceChannel()){
				final var botChannel = GunterAudioManager.currentAudioChannel(event.getGuild());
				if(Objects.isNull(botChannel) || Objects.equals(botChannel, member.getVoiceState().getChannel())){
					final var identifier = String.join(" ", args).trim();
					GunterAudioManager.play(event.getAuthor(), member.getVoiceState().getChannel(), identifier.equals("") ? "https://www.youtube.com/watch?v=J4X2b-CEGNg" : identifier);
				}
				else{
					Actions.reply(event, "Sorry, the user is in another channel");
				}
			}
			else{
				Actions.reply(event, "The user isn't in a voice channel");
			}
		}, () -> Actions.reply(event, "Please mention a user"));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user>";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Override
	public String getName(){
		return "Annoys someone";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("annoy");
	}
	
	@Override
	public String getDescription(){
		return "Go annoy a user in a voice channel";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
