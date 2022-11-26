package fr.rakambda.rsndiscord.spring.storage.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "Channel")
public class ChannelEntity{
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(nullable = false)
	private Integer id;
	@Basic
	@Column(nullable = false)
	private Long channelId;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ChannelType type;
	
	@Basic
	@Column(nullable = false)
	private Long guildId;
}
