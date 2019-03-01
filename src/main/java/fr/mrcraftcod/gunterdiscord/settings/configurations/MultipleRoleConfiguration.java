package fr.mrcraftcod.gunterdiscord.settings.configurations;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-12
 */
public abstract class MultipleRoleConfiguration extends ListConfiguration<Role>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected MultipleRoleConfiguration(final Guild guild){
		super(guild);
	}
	
	@Override
	protected Function<Role, String> getValueParser(){
		return role -> "" + role.getIdLong();
	}
	
	@Override
	protected BiFunction<MessageReceivedEvent, String, String> getMessageParser(){
		return (event, arg) -> {
			if(event.getMessage().getMentionedRoles().isEmpty()){
				throw new IllegalStateException("Please mention a role");
			}
			return "" + event.getMessage().getMentionedRoles().get(0).getIdLong();
		};
	}
	
	@Override
	protected Function<String, Role> getConfigParser(){
		return guild::getRoleById;
	}
}
