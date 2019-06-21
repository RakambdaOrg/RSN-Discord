package fr.mrcraftcod.gunterdiscord.commands.music;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class SkipMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	SkipMusicCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		switch(GunterAudioManager.skip(event.getGuild())){
			case NO_MUSIC:
				Actions.reply(event, "%s, no music currently playing", event.getAuthor().getAsMention());
				break;
			case OK:
				Actions.reply(event, "%s skipped the music", event.getAuthor().getAsMention());
				break;
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public boolean isAllowed(@Nullable final Member member){
		return Objects.nonNull(member) && (Utilities.isTeam(member) || GunterAudioManager.isRequester(member.getGuild(), member.getUser()));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Skip";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("skip");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Skips the current music";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
