package fr.raksrinana.rsndiscord.commands.warn;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@BotCommand
public class CustomWarnCommand extends WarnCommand{
	@NonNull
	@Override
	protected Optional<Role> getRole(@NonNull final Guild guild, @NonNull final Message message, @NonNull final LinkedList<String> args){
		args.pop();
		return Optional.of(message.getMentionedRoles().get(0));
	}
	
	@Override
	protected long getTime(@NonNull final Guild guild, @NonNull final Message message, @NonNull final LinkedList<String> args){
		return Long.parseLong(Objects.requireNonNull(args.poll()));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Custom warn";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("cwarn");
	}
}
