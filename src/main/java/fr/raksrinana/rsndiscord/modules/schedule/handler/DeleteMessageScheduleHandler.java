package fr.raksrinana.rsndiscord.modules.schedule.handler;

import fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.modules.schedule.config.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
@ScheduleHandler
public class DeleteMessageScheduleHandler implements IScheduleHandler{
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
	
	private static Optional<CompletableFuture<Message>> getMessage(ScheduleConfiguration configuration){
		return Optional.ofNullable(configuration.getData().get(DeleteMessageScheduleHandler.MESSAGE_ID_KEY)).map(Long::parseLong).flatMap(messageId -> configuration.getChannel().getChannel().map(channel -> Utilities.getMessageById(channel, messageId)));
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
