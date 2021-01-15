package fr.raksrinana.rsndiscord.command.impl.schedule.delete;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class DeleteCommandComposite extends CommandComposite{
	public DeleteCommandComposite(Command parent){
		super(parent);
		this.addSubCommand(new ChannelCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.schedule.delete", false);
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
