package fr.raksrinana.rsndiscord.command.impl.birthday;

import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class BirthdayCommandComposite extends CommandComposite{
	public BirthdayCommandComposite(){
		addSubCommand(new AddCommand(this));
		addSubCommand(new GetCommand(this));
		addSubCommand(new ListCommand(this));
		addSubCommand(new RemoveCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.birthday", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.birthday.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.birthday.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("birthday");
	}
}
