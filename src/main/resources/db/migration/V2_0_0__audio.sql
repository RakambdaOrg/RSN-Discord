create table audio
(
    id       integer not null auto_increment,
    volume   integer not null,
    guild_id bigint  not null,
    primary key (Id),
    unique index idx_audio_Guild (guild_id),
    foreign key fk_audio_Guild (guild_id) references guild (id)
) engine = InnoDB;

insert into audio(volume, guild_id)
select volume, id
from guild;

alter table guild
    drop column volume;
