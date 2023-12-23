package fr.rakambda.rsndiscord.spring.storage.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Simkl")
public class SimklEntity {
	@Id
	@Column(nullable = false)
	private Long id;
	@Basic
	private String username;
	@Basic
	private String accessToken;
	@Basic
	private Instant lastActivityDate;
	@Basic
	private boolean enabled;
}
