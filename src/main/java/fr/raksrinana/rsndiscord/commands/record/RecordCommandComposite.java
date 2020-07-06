package fr.raksrinana.rsndiscord.commands.record;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class RecordCommandComposite extends CommandComposite{
	public RecordCommandComposite(){
		this.addSubCommand(new StartCommand(this));
		this.addSubCommand(new StopCommand(this));
	}
	
	@NonNull
	@Override
	public String getName(@NonNull  Guild guild){
		return translate(guild, "command.record.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("r");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.record.description");
	}
}
