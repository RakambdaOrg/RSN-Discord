package fr.raksrinana.rsndiscord.log;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.CloseableThreadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

public class LogContext implements AutoCloseable{
	private static final String GUILD_ID_KEY = "guild_id";
	private static final String GUILD_NAME_KEY = "guild_name";
	private static final String USER_ID_KEY = "user_id";
	private final CloseableThreadContext.Instance ctc;
	
	private LogContext(@Nullable Guild guild){
		if(Objects.nonNull(guild)){
			ctc = CloseableThreadContext
					.push(GUILD_ID_KEY, guild.getId())
					.put(GUILD_NAME_KEY, guild.getName());
		}
		else{
			ctc = null;
		}
	}
	
	@NotNull
	public static LogContext empty(){
		return new LogContext(null);
	}
	
	@NotNull
	public static LogContext with(@Nullable Guild guild){
		return new LogContext(guild);
	}
	
	@NotNull
	public LogContext with(@NotNull User user){
		ctc.put(USER_ID_KEY, user.getId());
		return this;
	}
	
	@Override
	public void close(){
		if(Objects.nonNull(ctc)){
			ctc.close();
		}
	}
}
