package fr.raksrinana.rsndiscord.commands.record;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class StopCommand extends BasicCommand{
	public StopCommand(Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		Receiver.getInstance(event.getGuild()).ifPresentOrElse(receiver -> {
			receiver.stop();
			Actions.reply(event, translate(event.getGuild(), "record.close.success"), null);
		}, () -> Actions.reply(event, translate(event.getGuild(), "record.close.no-recording"), null));
		event.getGuild().getAudioManager().closeAudioConnection();
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.record.stop.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("stop");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.record.stop.description");
	}
	
	@Override
	public @NonNull AccessLevel getAccessLevel(){
		return AccessLevel.CREATOR;
	}
}
