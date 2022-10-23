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
}
