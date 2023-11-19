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
@Table(name = "Anilist")
public class AnilistEntity{
	@Id
	@Column(nullable = false)
	private Long id;
	@Basic
	private Integer userId;
	@Basic
	private String accessToken;
	@Basic
	private String refreshToken;
	@Basic
	private Instant refreshTokenExpire;
	@Basic
	private Instant lastMediaListDate;
	@Basic
	private Instant lastNotificationDate;
	@Basic
	private boolean enabled;
}
