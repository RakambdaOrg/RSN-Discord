package fr.raksrinana.rsndiscord.commands.schedule.delete;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class DeleteCommandComposite extends CommandComposite{
	public DeleteCommandComposite(Command parent){
		super(parent);
		this.addSubCommand(new ChannelCommand(this));
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.schedule.delete.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("delete");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.schedule.delete.description");
	}
}
