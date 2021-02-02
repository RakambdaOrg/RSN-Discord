package fr.raksrinana.rsndiscord.command.impl.schedule.delete;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class DeleteCommandComposite extends CommandComposite{
	public DeleteCommandComposite(Command parent){
		super(parent);
		addSubCommand(new ChannelCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.schedule.delete", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.schedule.delete.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.schedule.delete.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("delete");
	}
}
