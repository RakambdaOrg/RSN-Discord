package fr.mrcraftcod.gunterdiscord.settings.configurations;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-12
 */
public abstract class MultipleUserConfiguration extends ListConfiguration<User>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected MultipleUserConfiguration(final Guild guild){
		super(guild);
	}
	
	@Override
	protected Function<User, String> getValueParser(){
		return user -> "" + user.getIdLong();
	}
	
	@Override
	protected BiFunction<MessageReceivedEvent, String, String> getMessageParser(){
		return (event, arg) -> {
			if(event.getMessage().getMentionedRoles().isEmpty()){
				throw new IllegalStateException("Please mention a role");
			}
			return "" + event.getMessage().getMentionedUsers().get(0).getIdLong();
		};
	}
	
	@Override
	protected Function<String, User> getConfigParser(){
		return id -> this.guild.getJDA().getUserById(id);
	}
}
