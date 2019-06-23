package fr.mrcraftcod.gunterdiscord.settings.configurations;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
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
	protected MultipleRoleConfiguration(@Nullable final Guild guild){
		super(guild);
	}
	
	@Nonnull
	@Override
	protected Function<Role, String> getValueParser(){
		return role -> Objects.isNull(role) ? null : "" + role.getIdLong();
	}
	
	@Nonnull
	@Override
	protected BiFunction<GuildMessageReceivedEvent, String, String> getMessageParser(){
		return (event, arg) -> {
			if(event.getMessage().getMentionedRoles().isEmpty()){
				throw new IllegalStateException("Please mention a role");
			}
			return "" + event.getMessage().getMentionedRoles().get(0).getIdLong();
		};
	}
	
	@Nonnull
	@Override
	protected Function<String, Role> getConfigParser(){
		return Objects.isNull(this.guild) ? role -> null : this.guild::getRoleById;
	}
}
