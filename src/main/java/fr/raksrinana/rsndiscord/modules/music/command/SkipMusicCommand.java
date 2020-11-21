package fr.raksrinana.rsndiscord.modules.music.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.modules.music.reply.SkipMusicReply;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.reply.UserReplyEventListener;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class SkipMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	SkipMusicCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@Override
	public boolean isAllowed(final @NonNull Member member){
		return super.isAllowed(member);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		
		var track = RSNAudioManager.currentTrack(event.getGuild());
		if(track.isEmpty()){
			Actions.reply(event, translate(event.getGuild(), "music.nothing-playing"), null);
			return CommandResult.SUCCESS;
		}
		
		if(track.get().getDuration() - track.get().getPosition() < 30000){
			Actions.reply(event, translate(event.getGuild(), "music.skip.soon-finish"), null);
			return CommandResult.SUCCESS;
		}
		
		if(RSNAudioManager.isRequester(event.getGuild(), event.getAuthor()) || Utilities.isModerator(event.getMember())){
			skip(event);
			return CommandResult.SUCCESS;
		}
		
		var requiredVote = Optional.ofNullable(event.getGuild().getAudioManager().getConnectedChannel())
				.map(voiceChannel -> voiceChannel.getMembers().size())
				.map(count -> count - 1)
				.map(count -> (int) Math.ceil(count / 2.0))
				.orElse(1);
		
		var builder = Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, translate(event.getGuild(), "music.skip.title"), null);
		builder.addField(translate(event.getGuild(), "music.skip.votes-required"), "" + requiredVote, true);
		Actions.sendEmbed(event.getChannel(), builder.build()).thenAccept(message -> {
			Actions.addReaction(message, BasicEmotes.CHECK_OK.getValue());
			UserReplyEventListener.handleReply(new SkipMusicReply(event, message, requiredVote));
		});
		
		return CommandResult.SUCCESS;
	}
	
	private void skip(@NonNull final GuildMessageReceivedEvent event){
		switch(RSNAudioManager.skip(event.getGuild())){
			case NO_MUSIC -> Actions.reply(event, translate(event.getGuild(), "music.nothing-playing"), null);
			case OK -> Actions.reply(event, translate(event.getGuild(), "music.skipped", event.getAuthor().getAsMention()), null);
		}
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.music.skip", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.music.skip.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("skip");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.music.skip.description");
	}
}
