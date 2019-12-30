package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.Main;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Category;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class CategoryConfiguration{
	@JsonProperty("categoryId")
	@Setter
	private long categoryId;
	
	public CategoryConfiguration(final Category category){
		this(category.getIdLong());
	}
	
	public CategoryConfiguration(final long categoryId){
		this.categoryId = categoryId;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getCategoryId()).toHashCode();
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof CategoryConfiguration)){
			return false;
		}
		final var that = (CategoryConfiguration) o;
		return new EqualsBuilder().append(this.getCategoryId(), that.getCategoryId()).isEquals();
	}
	
	@Override
	public String toString(){
		return this.getCategory().map(Category::getName).orElse("<Unknown category>");
	}
	
	@NonNull
	public Optional<Category> getCategory(){
		return Optional.ofNullable(Main.getJda().getCategoryById(this.getCategoryId()));
	}
	
	public void setCategory(@NonNull final Category category){
		this.setCategoryId(category.getIdLong());
	}
}
