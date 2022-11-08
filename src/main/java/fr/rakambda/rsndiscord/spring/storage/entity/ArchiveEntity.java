package fr.rakambda.rsndiscord.spring.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "GuildId", nullable = false)
	private GuildEntity guild;
}
