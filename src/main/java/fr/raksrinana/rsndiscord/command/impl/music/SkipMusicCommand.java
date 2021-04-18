package fr.raksrinana.rsndiscord.command.impl.music;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.reply.SkipMusicReply;
import fr.raksrinana.rsndiscord.reply.UserReplyEventListener;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.isModerator;
import static java.awt.Color.ORANGE;
import static java.util.Optional.ofNullable;

public class SkipMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	SkipMusicCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		var track = RSNAudioManager.currentTrack(guild);
		
		if(track.isEmpty()){
			JDAWrappers.message(event, translate(guild, "music.nothing-playing")).submit();
			return SUCCESS;
		}
		
		if(track.get().getDuration() - track.get().getPosition() < 30000){
			JDAWrappers.message(event, translate(guild, "music.skip.soon-finish")).submit();
			return SUCCESS;
		}
		
		if(RSNAudioManager.isRequester(guild, author) || isModerator(event.getMember())){
			skip(event);
			return SUCCESS;
		}
		
		var requiredVote = ofNullable(guild.getAudioManager().getConnectedChannel())
				.map(voiceChannel -> voiceChannel.getMembers().size())
				.map(count -> count - 1)
				.map(count -> (int) Math.ceil(count / 2.0))
				.orElse(1);
		
		Log.getLogger(guild).info("Will start vote to skip music, will require {} votes", requiredVote);
		
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(ORANGE)
				.setTitle(translate(guild, "music.skip.title"))
				.addField(translate(guild, "music.skip.votes-required"), "" + requiredVote, true)
				.build();
		JDAWrappers.message(event, embed).submit()
				.thenAccept(message -> {
					JDAWrappers.addReaction(message, CHECK_OK).submit();
					UserReplyEventListener.handleReply(new SkipMusicReply(event, message, requiredVote, track.get()));
				});
		
		return SUCCESS;
	}
	
	private void skip(@NotNull GuildMessageReceivedEvent event){
		var guild = event.getGuild();
		var message = switch(RSNAudioManager.skip(guild)){
			case NO_MUSIC -> "music.nothing-playing";
			case OK -> "music.skipped";
			case IMPOSSIBLE -> "unknown";
		};
		
		JDAWrappers.message(event, translate(guild, message, event.getAuthor().getAsMention())).submit()
				.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
	}
	
	@Override
	public boolean isAllowed(@NotNull Member member){
		return super.isAllowed(member);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.music.skip", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.music.skip.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.music.skip.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("skip");
	}
}
