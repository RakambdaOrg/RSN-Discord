package fr.raksrinana.rsndiscord.commands.trombinoscope;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.TrombinoscopeUtils.isBanned;

@BotCommand
public class TrombinoscopeCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public TrombinoscopeCommandComposite(){
		super();
		this.addSubCommand(new AddCommand(this));
		this.addSubCommand(new GetCommand(this));
		this.addSubCommand(new RemoveCommand(this));
		this.addSubCommand(new StatsCommand(this));
		this.addSubCommand(new BanCommand(this));
		this.addSubCommand(new UnbanCommand(this));
		this.addSubCommand(new GlobalCommand(this));
	}
	
	@Override
	public boolean isAllowed(Member member){
		if(super.isAllowed(member)){
			return !isBanned(member);
		}
		return false;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Trombinoscope";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("trombinoscope", "trombi", "t");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Trombinoscope related commands";
	}
}
