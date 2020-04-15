package fr.raksrinana.rsndiscord.utils.schedule;

import fr.raksrinana.rsndiscord.settings.guild.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DeleteMessageScheduleHandler implements ScheduleHandler{
	public static final String MESSAGE_ID_KEY = "messageId";
	
	@Override
	public boolean acceptTag(@NonNull ScheduleTag tag){
		return Objects.equals(tag, ScheduleTag.DELETE_MESSAGE);
	}
	
	@Override
	public boolean accept(@NonNull ScheduleConfiguration reminder){
		getMessage(reminder).ifPresent(future -> future.thenAccept(Actions::deleteMessage));
		return true;
	}
	
	private Optional<CompletableFuture<Message>> getMessage(ScheduleConfiguration configuration){
		return Optional.ofNullable(configuration.getData().get(DeleteMessageScheduleHandler.MESSAGE_ID_KEY)).map(Long::parseLong).flatMap(messageId -> configuration.getChannel().getChannel().map(channel -> Utilities.getMessageById(channel, messageId)));
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
