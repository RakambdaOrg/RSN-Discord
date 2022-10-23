package fr.rakambda.rsndiscord.spring.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "HermitcraftVideo")
public class HermitcraftVideoEntity{
	@Id
	@Column(nullable = false, length = 32)
	private String id;
	@Basic
	private Instant notificationDate;
}
