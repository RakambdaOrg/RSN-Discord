package fr.rakambda.rsndiscord.spring.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Rss")
public class RssEntity{
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(nullable = false)
	private Integer id;
	@Basic
	@Column(nullable = false)
	private String url;
	@Basic
	private String title;
	@Basic
	private Instant lastPublicationDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "GuildId", nullable = false)
	private GuildEntity guild;
}
