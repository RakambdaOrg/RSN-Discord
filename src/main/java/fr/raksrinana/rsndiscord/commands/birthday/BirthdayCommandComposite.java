package fr.raksrinana.rsndiscord.commands.birthday;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class BirthdayCommandComposite extends CommandComposite{
	public BirthdayCommandComposite(){
		this.addSubCommand(new AddCommand(this));
		this.addSubCommand(new GetCommand(this));
		this.addSubCommand(new RemoveCommand(this));
	}
	
	@NonNull
	@Override
	public String getName(@NonNull  Guild guild){
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
	
	@Override
	public @NonNull AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
}
