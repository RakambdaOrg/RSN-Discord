package fr.raksrinana.rsndiscord.commands.generic;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

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
		if(!this.isAllowed(requireNonNull(event.getMember()))){
			throw new NotAllowedException("You're not allowed to execute this command");
		}
		return SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return isNull(this.getParent()) || this.getParent() instanceof CommandComposite
				? ""
				: this.getParent().getCommandUsage();
	}
	
	@NonNull
	protected Optional<User> getFirstUserMentioned(@NonNull GuildMessageReceivedEvent event){
		return event.getMessage().getMentionedUsers().stream().findFirst();
	}
	
	@NonNull
	protected Optional<Integer> getArgumentAsInteger(@NonNull LinkedList<String> args){
		return getArgumentAs(args, Integer::parseInt);
	}
	
	@NonNull
	protected <T> Optional<T> getArgumentAs(@NonNull LinkedList<String> args, Function<String, T> converter){
		return Optional.ofNullable(args.poll()).map(arg -> {
			try{
				return converter.apply(arg);
			}
			catch(RuntimeException ignored){
			}
			return null;
		});
	}
	
	@NonNull
	protected Optional<TextChannel> getFirstChannelMentioned(@NonNull GuildMessageReceivedEvent event){
		return event.getMessage().getMentionedChannels().stream().findFirst();
	}
	
	@NonNull
	protected Optional<Role> getFirstRoleMentioned(@NonNull GuildMessageReceivedEvent event){
		return event.getMessage().getMentionedRoles().stream().findFirst();
	}
	
	@NonNull
	protected Optional<Member> getFirstMemberMentioned(@NonNull GuildMessageReceivedEvent event){
		return event.getMessage().getMentionedMembers().stream().findFirst();
	}
	
	@Override
	public Command getParent(){
		return this.parent;
	}
	
	protected boolean noUserIsMentioned(@NonNull GuildMessageReceivedEvent event){
		return event.getMessage().getMentionedUsers().isEmpty();
	}
	
	protected boolean noMemberIsMentioned(@NonNull GuildMessageReceivedEvent event){
		return event.getMessage().getMentionedMembers().isEmpty();
	}
}
