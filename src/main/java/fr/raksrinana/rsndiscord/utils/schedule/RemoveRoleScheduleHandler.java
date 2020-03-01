package fr.raksrinana.rsndiscord.utils.schedule;

import fr.raksrinana.rsndiscord.settings.guild.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Role;
import java.util.Objects;
import java.util.Optional;

public class RemoveRoleScheduleHandler implements ScheduleHandler{
	public static final String ROLE_ID_KEY = "roleId";
	
	@Override
	public boolean acceptTag(@NonNull ScheduleTag tag){
		return Objects.equals(tag, ScheduleTag.REMOVE_ROLE);
	}
	
	@Override
	public boolean accept(@NonNull ScheduleConfiguration reminder){
		getRole(reminder).ifPresent(role -> reminder.getUser().getUser().flatMap(user -> Optional.ofNullable(role.getGuild().getMember(user))).ifPresent(member -> Actions.removeRole(member, role)));
		return true;
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
	
	public static Optional<Role> getRole(@NonNull ScheduleConfiguration configuration){
		return Optional.ofNullable(configuration.getData().get(RemoveRoleScheduleHandler.ROLE_ID_KEY)).map(Long::parseLong).flatMap(Utilities::getRoleById);
	}
}
