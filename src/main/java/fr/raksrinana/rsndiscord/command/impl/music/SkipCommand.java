package fr.raksrinana.rsndiscord.command.impl.music;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.reply.SkipMusicReply;
import fr.raksrinana.rsndiscord.reply.UserReplyEventListener;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.isModerator;
import static java.awt.Color.ORANGE;
import static java.util.Optional.ofNullable;

@Log4j2
public class SkipCommand extends SubCommand{
	@Override
	@NotNull
	public String getId(){
		return "skip";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Skip the current music";
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	public boolean isSpecificAllowed(@NotNull Member member){
		return RSNAudioManager.isRequester(member.getGuild(), member.getUser());
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var author = event.getUser();
		var track = RSNAudioManager.currentTrack(guild);
		
		if(track.isEmpty()){
			JDAWrappers.edit(event, translate(guild, "music.nothing-playing")).submit();
			return HANDLED;
		}
		
		if(track.get().getDuration() - track.get().getPosition() < 30000){
			JDAWrappers.edit(event, translate(guild, "music.skip.soon-finish")).submit();
			return HANDLED;
		}
		
		if(RSNAudioManager.isRequester(guild, author) || isModerator(member)){
			var message = skip(guild);
			JDAWrappers.edit(event, translate(guild, message, event.getUser().getAsMention())).submitAndDelete(5);
			return HANDLED;
		}
		
		var requiredVote = ofNullable(guild.getAudioManager().getConnectedChannel())
				.map(voiceChannel -> voiceChannel.getMembers().size())
				.map(count -> count - 1)
				.map(count -> (int) Math.ceil(count / 2.0))
				.orElse(1);
		
		log.info("Will start vote to skip music, will require {} votes", requiredVote);
		
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(ORANGE)
				.setTitle(translate(guild, "music.skip.title"))
				.addField(translate(guild, "music.skip.votes-required"), "" + requiredVote, true)
				.build();
		JDAWrappers.edit(event, embed).submit()
				.thenAccept(message -> {
					JDAWrappers.addReaction(message, CHECK_OK).submit();
					UserReplyEventListener.handleReply(new SkipMusicReply(event, message, requiredVote, track.get()));
				});
		
		return HANDLED;
	}
	
	private String skip(@NotNull Guild guild){
		return switch(RSNAudioManager.skip(guild)){
			case NO_MUSIC -> "music.nothing-playing";
			case OK -> "music.skipped";
			case IMPOSSIBLE -> "unknown";
		};
	}
}
