package fr.mrcraftcod.gunterdiscord.commands.generic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	protected BasicCommand(@Nullable final Command parent){
		this.parent = parent;
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		if(event.isWebhookMessage()){
			throw new NotHandledException("This message is from a webhook");
		}
		if(!isAllowed(event.getMember())){
			throw new NotAllowedException();
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return Objects.isNull(getParent()) || getParent() instanceof CommandComposite ? "" : getParent().getCommandUsage();
	}
	
	@Nullable
	@Override
	public Command getParent(){
		return this.parent;
	}
}
