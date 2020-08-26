package fr.raksrinana.rsndiscord.commands.action;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.utils.giphy.requests.RandomGifRequest;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.action.Action.SLAP;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class SlapActionCommand extends GenericActionCommand{
	protected SlapActionCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected RandomGifRequest getRequest(){
		return new RandomGifRequest(getAction().getTag(), getAction().getRating());
	}
	
	@Override
	protected Action getAction(){
		return SLAP;
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.action.slap", false);
	}
	
	@Override
	public @NonNull String getName(@NonNull Guild guild){
		return translate(guild, "command.action.slap.name");
	}
	
	@Override
	public @NonNull List<String> getCommandStrings(){
		return List.of("slap");
	}
	
	@Override
	public @NonNull String getDescription(@NonNull Guild guild){
		return translate(guild, "command.action.slap.description");
	}
}
