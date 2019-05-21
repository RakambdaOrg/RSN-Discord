package fr.mrcraftcod.gunterdiscord.settings.configurations;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-04
 */
@SuppressWarnings("WeakerAccess")
public abstract class SingleUserConfiguration extends ValueConfiguration<Member>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected SingleUserConfiguration(final Guild guild){
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
		final var member = getObject(null);
		if(Objects.isNull(member)){
			return false;
		}
		return Objects.equals(ID, member.getIdLong());
	}
	
	@Override
	protected BiFunction<GuildMessageReceivedEvent, String, String> getMessageParser(){
		return (event, arg) -> {
			if(event.getMessage().getMentionedMembers().isEmpty()){
				throw new IllegalStateException("Please mention a member");
			}
			return "" + event.getMessage().getMentionedMembers().get(0).getIdLong();
		};
	}
	
	@Override
	protected Function<String, Member> getConfigParser(){
		return this.guild::getMemberById;
	}
	
	@Override
	protected Function<Member, String> getValueParser(){
		return member -> "" + member.getIdLong();
	}
}
