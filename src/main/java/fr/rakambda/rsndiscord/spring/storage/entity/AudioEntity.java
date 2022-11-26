package fr.rakambda.rsndiscord.spring.storage.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Audio")
public class AudioEntity{
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(nullable = false)
	private Integer id;
	@Basic
	@Column(nullable = false)
	private Integer volume;
	
	@Basic
	@Column(nullable = false)
	private Long guildId;
}
