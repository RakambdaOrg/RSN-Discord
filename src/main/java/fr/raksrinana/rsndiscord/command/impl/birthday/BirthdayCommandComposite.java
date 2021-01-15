package fr.raksrinana.rsndiscord.command.impl.birthday;

import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class BirthdayCommandComposite extends CommandComposite{
	public BirthdayCommandComposite(){
		this.addSubCommand(new AddCommand(this));
		this.addSubCommand(new GetCommand(this));
		this.addSubCommand(new ListCommand(this));
		this.addSubCommand(new RemoveCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.birthday", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.birthday.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("birthday");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.birthday.description");
	}
}
