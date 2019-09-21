package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.BotCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.player.RSNAudioManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
@BotCommand
public class AnnoyCommand extends BasicCommand{
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The target of the prank", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		args.poll();
		event.getMessage().getMentionedUsers().stream().findAny().ifPresentOrElse(u -> {
			final var member = event.getGuild().getMember(u);
			if(Optional.ofNullable(member).map(Member::getVoiceState).map(GuildVoiceState::inVoiceChannel).orElse(false)){
				RSNAudioManager.getFor(event.getGuild()).ifPresent(audioManager -> {
					final var botChannel = audioManager.getChannel();
					if(Objects.equals(botChannel, member.getVoiceState().getChannel())){
						final var identifier = String.join(" ", args).trim();
						RSNAudioManager.play(event.getAuthor(), member.getVoiceState().getChannel(), "".equals(identifier) ? "https://www.youtube.com/watch?v=J4X2b-CEGNg" : identifier);
					}
					else{
						Actions.reply(event, "Sorry, the user is in another channel");
					}
				});
			}
			else{
				Actions.reply(event, "The user isn't in a voice channel");
			}
		}, () -> Actions.reply(event, "Please mention a user"));
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user>";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Annoys someone";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("annoy");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Go annoy a user in a voice channel";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
