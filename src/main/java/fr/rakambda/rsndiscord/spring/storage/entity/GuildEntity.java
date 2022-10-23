package fr.rakambda.rsndiscord.spring.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Guild")
public class GuildEntity{
	@Id
	@Column(nullable = false)
	private Long id;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "guild")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Set<AudioEntity> audios;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "guild")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Set<ChannelEntity> channels;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "guild")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Set<RssEntity> rss;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "guild")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Set<TwitterEntity> twitter;
}
