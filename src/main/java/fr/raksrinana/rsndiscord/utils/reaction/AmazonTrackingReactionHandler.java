package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.net.URL;
import java.util.Objects;

public class AmazonTrackingReactionHandler extends TodosReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.AMAZON_TRACKER);
	}
	
	@Override
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return BasicEmotes.CROSS_NO == emote;
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
	
	@Override
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emotes, @NonNull WaitingReactionMessageConfiguration todo){
		try{
			final var url = new URL(todo.getData().get(ReactionUtils.URL_KEY));
			final var userId = Long.parseLong(todo.getData().get(ReactionUtils.USER_ID_KEY));
			if(Settings.get(event.getGuild()).getAmazonTrackings().removeIf(tracking -> {
				final var delete = Objects.equals(url, tracking.getUrl()) && Objects.equals(userId, tracking.getUser().getUserId());
				if(delete){
					Actions.replyPrivate(event.getGuild(), event.getUser(), "This product won't be tracked anymore", null);
				}
				return delete;
			})){
				Utilities.getMessageById(event.getChannel(), event.getMessageIdLong()).thenAccept(Actions::clearReactions);
			}
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).error("Failed to stop tracking Amazon product", e);
		}
		return ReactionHandlerResult.PROCESSED_DELETE;
	}
}
