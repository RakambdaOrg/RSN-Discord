package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.api.IAtomicConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Category;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class CategoryConfiguration implements IAtomicConfiguration{
	@JsonProperty("categoryId")
	@Setter
	private long categoryId;
	
	public CategoryConfiguration(@NotNull Category category){
		this(category.getIdLong());
	}
	
	public CategoryConfiguration(long categoryId){
		this.categoryId = categoryId;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(getCategoryId()).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof CategoryConfiguration that)){
			return false;
		}
		return new EqualsBuilder().append(getCategoryId(), that.getCategoryId()).isEquals();
	}
	
	@Override
	public String toString(){
		return "Category(" + getCategoryId() + ')';
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return getCategory().isEmpty();
	}
	
	@NotNull
	public Optional<Category> getCategory(){
		return ofNullable(Main.getJda().getCategoryById(getCategoryId()));
	}
	
	public void setCategory(@NotNull Category category){
		setCategoryId(category.getIdLong());
	}
}
