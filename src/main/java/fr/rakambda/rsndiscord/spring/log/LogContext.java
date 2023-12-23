package fr.rakambda.rsndiscord.spring.log;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.MDC;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

@Slf4j
public class LogContext implements AutoCloseable{
	private static final String GUILD_ID_KEY = "guild_id";
	private static final String GUILD_NAME_KEY = "guild_name";
	private static final String USER_ID_KEY = "user_id";
	
	private final Collection<Closeable> closeables;
	
	private LogContext(@Nullable Guild guild){
		closeables = new LinkedList<>();
		
		if(Objects.nonNull(guild)){
			closeables.add(MDC.putCloseable(GUILD_ID_KEY, guild.getId()));
			closeables.add(MDC.putCloseable(GUILD_NAME_KEY, guild.getName()));
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
		closeables.add(MDC.putCloseable(USER_ID_KEY, user.getId()));
		return this;
	}
	
	@Override
	public void close(){
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.error("Failed to close MDC", e);
            }
        }
	}
}
