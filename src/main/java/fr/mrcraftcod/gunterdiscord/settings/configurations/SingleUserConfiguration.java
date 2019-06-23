package fr.mrcraftcod.gunterdiscord.settings.configurations;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-04
 */
public abstract class SingleUserConfiguration extends ValueConfiguration<Member>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected SingleUserConfiguration(@Nullable final Guild guild){
		super(guild);
	}
	
	/**
	 * Tells if this config represents the given member.
	 *
	 * @param ID The ID of the member.
	 *
	 * @return True if the same members, false otherwise.
	 */
	public boolean isMember(final long ID){
		return Optional.ofNullable(getObject(null)).map(user -> Objects.equals(ID,  user.getIdLong())).orElse(false);
	}
	
	@Nonnull
	@Override
	protected BiFunction<GuildMessageReceivedEvent, String, String> getMessageParser(){
		return (event, arg) -> {
			if(event.getMessage().getMentionedMembers().isEmpty()){
				throw new IllegalStateException("Please mention a member");
			}
			return "" + event.getMessage().getMentionedMembers().get(0).getIdLong();
		};
	}
	
	@Nonnull
	@Override
	protected Function<String, Member> getConfigParser(){
		return Objects.isNull(this.guild) ? user -> null : this.guild::getMemberById;
	}
	
	@Nonnull
	@Override
	protected Function<Member, String> getValueParser(){
		return member -> Objects.isNull(member) ? null : "" + member.getIdLong();
	}
}
