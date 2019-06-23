package fr.mrcraftcod.gunterdiscord.settings.configurations;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
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
public abstract class MultipleUserConfiguration extends ListConfiguration<User>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected MultipleUserConfiguration(@Nullable final Guild guild){
		super(guild);
	}
	
	@Nonnull
	@Override
	protected Function<User, String> getValueParser(){
		return user -> Objects.isNull(user) ? null : "" + user.getIdLong();
	}
	
	@Nonnull
	@Override
	protected BiFunction<GuildMessageReceivedEvent, String, String> getMessageParser(){
		return (event, arg) -> {
			if(event.getMessage().getMentionedUsers().isEmpty()){
				throw new IllegalStateException("Please mention a user");
			}
			return "" + event.getMessage().getMentionedUsers().get(0).getIdLong();
		};
	}
	
	@Nonnull
	@Override
	protected Function<String, User> getConfigParser(){
		return Objects.isNull(this.guild) ? id -> null : id -> this.guild.getJDA().getUserById(id);
	}
}
