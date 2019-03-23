package fr.mrcraftcod.gunterdiscord.commands.generic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public abstract class BasicCommand implements Command{
	private final Command parent;
	
	/**
	 * Constructor.
	 */
	protected BasicCommand(){
		this(null);
	}
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	protected BasicCommand(final Command parent){
		this.parent = parent;
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		if(!isAllowed(event.getMember())){
			throw new NotAllowedException();
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return Objects.isNull(getParent()) || getParent() instanceof CommandComposite ? "" : getParent().getCommandUsage();
	}
	
	@Override
	public Command getParent(){
		return parent;
	}
}
