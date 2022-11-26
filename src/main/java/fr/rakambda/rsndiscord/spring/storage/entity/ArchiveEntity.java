package fr.rakambda.rsndiscord.spring.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Archive")
public class ArchiveEntity{
	@Id
	@Column(nullable = false)
	private Long id;
	@Basic
	@Column(nullable = false)
	private Long tagId;
	
	@Basic
	@Column(nullable = false)
	private Long guildId;
}
