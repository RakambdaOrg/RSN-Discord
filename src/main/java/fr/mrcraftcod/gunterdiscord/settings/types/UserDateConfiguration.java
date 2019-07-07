package fr.mrcraftcod.gunterdiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.utils.json.SQLTimestampDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.json.SQLTimestampSerializer;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	private static final SimpleDateFormat SDF = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
	@JsonProperty("userId")
	private long userId;
	@JsonProperty("date")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	@JsonSerialize(using = SQLTimestampSerializer.class)
	private Date date;
	
	public UserDateConfiguration(){
	}
	
	public UserDateConfiguration(@Nonnull User user, @Nonnull Date date){
		this(user.getIdLong(), date);
	}
	
	public UserDateConfiguration(long userId, @Nonnull Date date){
		this.userId = userId;
		this.date = date;
	}
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof UserDateConfiguration && Objects.equals(this.getUserId(), ((UserDateConfiguration) obj).getUserId()) && Objects.equals(this.getDate(), ((UserDateConfiguration) obj).getDate());
	}
	
	@Override
	public String toString(){
		return this.getUser().map(User::getAsMention).map(s -> s + " " + SDF.format(this.getDate())).orElse("");
	}
	
	@Nonnull
	public Optional<User> getUser(){
		return Optional.ofNullable(Main.getJDA().getUserById(this.getUserId()));
	}
	
	@Nonnull
	public Date getDate(){
		return this.date;
	}
	
	public void setDate(@Nonnull Date date){
		this.date = date;
	}
	
	public long getUserId(){
		return this.userId;
	}
}
