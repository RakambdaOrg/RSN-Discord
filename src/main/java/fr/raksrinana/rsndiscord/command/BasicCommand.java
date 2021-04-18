package fr.raksrinana.rsndiscord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

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
	protected BasicCommand(Command parent){
		this.parent = parent;
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws RuntimeException{
		if(event.isWebhookMessage()){
			throw new NotHandledException("This message is from a webhook");
		}
		if(!isAllowed(requireNonNull(event.getMember()))){
			throw new NotAllowedException("You're not allowed to execute this command");
		}
		return SUCCESS;
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return isNull(getParent()) || getParent() instanceof CommandComposite
				? ""
				: getParent().getCommandUsage();
	}
	
	@Override
	@Nullable
	public Command getParent(){
		return parent;
	}
	
	@NotNull
	protected Optional<User> getFirstUserMentioned(@NotNull GuildMessageReceivedEvent event){
		return event.getMessage().getMentionedUsers().stream().findFirst();
	}
	
	@NotNull
	protected Optional<Integer> getArgumentAsInteger(@NotNull LinkedList<String> args){
		return getArgumentAs(args, Integer::parseInt);
	}
	
	@NotNull
	protected <T> Optional<T> getArgumentAs(@NotNull LinkedList<String> args, Function<String, T> converter){
		return ofNullable(args.poll()).map(arg -> {
			try{
				return converter.apply(arg);
			}
			catch(RuntimeException ignored){
			}
			return null;
		});
	}
	
	@NotNull
	protected Optional<Long> getArgumentAsLong(@NotNull LinkedList<String> args){
		return getArgumentAs(args, Long::parseLong);
	}
	
	@NotNull
	protected Optional<TextChannel> getFirstChannelMentioned(@NotNull GuildMessageReceivedEvent event){
		return event.getMessage().getMentionedChannels().stream().findFirst();
	}
	
	@NotNull
	protected Optional<Role> getFirstRoleMentioned(@NotNull GuildMessageReceivedEvent event){
		return event.getMessage().getMentionedRoles().stream().findFirst();
	}
	
	@NotNull
	protected Optional<Member> getFirstMemberMentioned(@NotNull GuildMessageReceivedEvent event){
		return event.getMessage().getMentionedMembers().stream().findFirst();
	}
	
	protected boolean noUserIsMentioned(@NotNull GuildMessageReceivedEvent event){
		return event.getMessage().getMentionedUsers().isEmpty();
	}
	
	protected boolean noMemberIsMentioned(@NotNull GuildMessageReceivedEvent event){
		return event.getMessage().getMentionedMembers().isEmpty();
	}
}
