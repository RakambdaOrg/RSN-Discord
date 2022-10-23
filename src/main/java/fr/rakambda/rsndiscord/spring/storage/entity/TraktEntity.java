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
@Table(name = "Trakt")
public class TraktEntity{
	@Id
	@Column(nullable = false)
	private Long id;
	@Basic
	private String username;
	@Basic
	private String accessToken;
	@Basic
	private String refreshToken;
	@Basic
	private Instant refreshTokenExpire;
	@Basic
	private Instant lastActivityDate;
}
