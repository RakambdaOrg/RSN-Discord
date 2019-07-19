package fr.mrcraftcod.gunterdiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.utils.json.LocalDateTimeDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.json.LocalDateTimeSerializer;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDateConfiguration{
	public static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	@JsonProperty("userId")
	private long userId;
	@JsonProperty("date")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime date;
	
	public UserDateConfiguration(){
	}
	
	public UserDateConfiguration(@Nonnull User user, @Nonnull LocalDateTime date){
		this(user.getIdLong(), date);
	}
	
	public UserDateConfiguration(long userId, @Nonnull LocalDateTime date){
		this.userId = userId;
		this.date = date;
	}
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof UserDateConfiguration && Objects.equals(this.getUserId(), ((UserDateConfiguration) obj).getUserId()) && Objects.equals(this.getDate(), ((UserDateConfiguration) obj).getDate());
	}
	
	@Override
	public String toString(){
		return this.getUser().map(User::getAsMention).map(s -> s + " " + this.getDate().format(DF)).orElse("");
	}
	
	@Nonnull
	public Optional<User> getUser(){
		return Optional.ofNullable(Main.getJDA().getUserById(this.getUserId()));
	}
	
	@Nonnull
	public LocalDateTime getDate(){
		return this.date;
	}
	
	public void setDate(@Nonnull LocalDateTime date){
		this.date = date;
	}
	
	public long getUserId(){
		return this.userId;
	}
}
