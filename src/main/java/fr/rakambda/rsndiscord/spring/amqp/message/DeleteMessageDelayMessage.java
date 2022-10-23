package fr.rakambda.rsndiscord.spring.amqp.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMessageDelayMessage implements IDelayedMessage{
	private long channelId;
	private long messageId;
}
