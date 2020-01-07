package fr.raksrinana.rsndiscord.commands.generic;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.Objects;

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
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args) throws RuntimeException{
		if(event.isWebhookMessage()){
			throw new NotHandledException("This message is from a webhook");
		}
		if(!this.isAllowed(event.getMember())){
			throw new NotAllowedException("You're not allowed to execute this command");
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return Objects.isNull(this.getParent()) || this.getParent() instanceof CommandComposite ? "" : this.getParent().getCommandUsage();
	}
	
	@Override
	public Command getParent(){
		return this.parent;
	}
}
