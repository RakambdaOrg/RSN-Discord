package fr.rakambda.rsndiscord.spring.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Twitter")
public class TwitterEntity{
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(nullable = false)
	private Integer id;
	@Basic
	@Column(nullable = false)
	private String search;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TwitterType type;
	@Basic
	private String lastTweetId;
	
	@Basic
	@Column(nullable = false)
	private Long guildId;
}
